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
import java.util.Arrays;
import java.util.List;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
class AggregateMojoTest {
  @Test
  void test_modules_ignored() throws Exception {
    MavenProjectStub project = new MavenProjectStub() {
      @Override
      public List<String> getModules() {
        return Arrays.<String>asList("module1", "module2", "module3");
      }
    };
    LicenseCheckMojo check = new LicenseCheckMojo();
    check.defaultBasedir = new File("src/test/resources/check/modules");
    check.legacyConfigHeader = "header.txt";
    check.project = project;
    check.strictCheck = true;
    check.execute();
  }

  @Test
  void test_modules_scanned() throws Exception {
    MavenProjectStub project = new MavenProjectStub() {
      @Override
      public List<String> getModules() {
        return Arrays.<String>asList("module1", "module2", "module3");
      }
    };
    LicenseCheckMojo check = new LicenseCheckMojo();
    check.project = project;
    check.defaultBasedir = new File("src/test/resources/check/modules");
    check.legacyConfigHeader = "header.txt";
    check.aggregate = true;
    check.strictCheck = true;
    try {
      check.execute();
      Assertions.fail();
    } catch (MojoExecutionException e) {
      Assertions.assertEquals("Some files do not have the expected license header. Run license:format to update them.", e.getMessage());
    }
  }
}
