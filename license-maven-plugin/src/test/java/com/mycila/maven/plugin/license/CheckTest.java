/**
 * Copyright (C) 2008 Mycila (mathieu.carbou@gmail.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mycila.maven.plugin.license;

import com.mycila.maven.plugin.license.util.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.testing.stubs.MavenProjectStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
class CheckTest {

  @Test
  void test_defaultStubProject() throws Exception {
    LicenseCheckMojo check = new LicenseCheckMojo();
    check.project = new MavenProjectStub();
    check.execute();
  }

  @Test
  void test_line_wrapping() throws Exception {
    MavenProjectStub project = new MavenProjectStub();

    LicenseCheckMojo check = new LicenseCheckMojo();
    check.defaultBasedir = new File("src/test/resources/check/linewrap");
    check.legacyConfigHeader = "header.txt";
    check.project = project;

    // check by default - should work
    check.strictCheck = false;
    check.execute();

    // the strict test fail because if the missing blank lines
    try {
      check.strictCheck = true;
      check.execute();
    } catch (MojoExecutionException e) {
      Assertions.assertEquals("Some files do not have the expected license header. Run license:format to update them.", e.getMessage());
    }

    // prepare to reformat the file
    File tmp = new File("target/test/linewrap");
    tmp.mkdirs();
    FileUtils.copyFileToFolder(new File("src/test/resources/check/linewrap/testconfig.xml"), tmp);

    LicenseFormatMojo updater = new LicenseFormatMojo();
    updater.defaultBasedir = tmp;
    updater.legacyConfigHeader = "src/test/resources/check/linewrap/header.txt";
    updater.project = project;
    updater.strictCheck = true;
    updater.execute();

    // the check again, strictly. should work now
    check = new LicenseCheckMojo();
    check.defaultBasedir = tmp;
    check.legacyConfigHeader = "src/test/resources/check/linewrap/header.txt";
    check.project = project;

    check.strictCheck = true;
    check.execute();

  }

}
