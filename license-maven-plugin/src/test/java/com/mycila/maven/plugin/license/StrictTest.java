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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.testing.stubs.MavenProjectStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
class StrictTest {

  @Test
  void test_strict() throws Exception {
    MavenProjectStub project = new MavenProjectStub();

        /*LicenseFormatMojo format = new LicenseFormatMojo();
        format.basedir = new File("src/test/resources/check/issue76");
        format.header = "src/test/resources/test-header1.txt";
        format.project = project;
        format.execute();*/

    // all the headers are by default checked not strictlty
    LicenseCheckMojo check = new LicenseCheckMojo();
    check.defaultBasedir = new File("src/test/resources/check/issue76");
    check.legacyConfigHeader = "src/test/resources/test-header1.txt";
    check.project = project;
    check.strictCheck = false;
    check.execute();

    // all the headers are by default checked not strictlty
    check.strictCheck = true;
    try {
      check.execute();
    } catch (MojoExecutionException e) {
      Assertions.assertEquals("Some files do not have the expected license header", e.getMessage());
    }
    System.out.println(check.missingHeaders);
    Assertions.assertEquals(4, check.missingHeaders.size());
  }

  @Test
  void test_space() throws Exception {
    MavenProjectStub project = new MavenProjectStub();

        /*LicenseFormatMojo format = new LicenseFormatMojo();
        format.basedir = new File("src/test/resources/check/strict");
        format.header = "src/test/resources/test-header1-diff.txt";
        format.project = project;
        format.execute();*/

    // all the headers are by default checked not strictlty
    LicenseCheckMojo check = new LicenseCheckMojo();
    check.defaultBasedir = new File("src/test/resources/check/strict");
    check.legacyConfigHeader = "src/test/resources/test-header1-diff.txt";
    check.project = project;
    check.execute();

    // all the headers are by default checked not strictlty
    check.strictCheck = true;
    check.execute();
    System.out.println(check.missingHeaders);
    Assertions.assertEquals(0, check.missingHeaders.size());
  }

}
