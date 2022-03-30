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

import com.mycila.maven.plugin.license.util.DebugLog;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.testing.stubs.MavenProjectStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Collections;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
class AdditionalHeaderMojoTest {
  @Test
  void test_additionalHeaderDefinitions() throws Exception {
    LicenseCheckMojo check = new LicenseCheckMojo();
    check.defaultBasedir = new File("src/test/resources/check/def");
    check.legacyConfigHeader = "src/test/resources/check/header.txt";
    check.project = new MavenProjectStub();
    check.legacyConfigExcludes = new String[]{"*.xml"};
    check.strictCheck = true;

    try {
      check.execute();
      Assertions.fail();
    } catch (MojoExecutionException e) {
      Assertions.assertEquals("Some files do not have the expected license header", e.getMessage());
    }

    check.defaultHeaderDefinitions = new String[]{"/check/def/additionalHeaderDefinitions.xml"};
    check.execute();
  }

  @Test
  void test_inline() throws Exception {
    LicenseCheckMojo check = new LicenseCheckMojo();
    check.defaultBasedir = new File("src/test/resources/check/def");
    check.legacyConfigHeader = "src/test/resources/check/header.txt";
    check.project = new MavenProjectStub();
    check.legacyConfigExcludes = new String[]{"*.xml"};

    try {
      check.execute();
      Assertions.fail();
    } catch (MojoExecutionException e) {
      Assertions.assertEquals("Some files do not have the expected license header", e.getMessage());
    }

    HeaderStyle style = new HeaderStyle();
    style.name = "smiley";
    style.firstLine = ":(";
    style.beforeEachLine = " ( ";
    style.endLine = ":(";
    style.firstLineDetectionPattern = "\\:\\(";
    style.lastLineDetectionPattern = "\\:\\(";
    style.allowBlankLines = false;
    style.multiLine = false;

    check.defaultInlineHeaderStyles = new HeaderStyle[]{style};
    check.mapping = Collections.singletonMap("txt", "smiley");
    check.setLog(new DebugLog());
    check.execute();
  }
}
