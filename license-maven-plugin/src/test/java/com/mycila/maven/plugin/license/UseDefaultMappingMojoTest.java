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

import java.io.File;
import org.apache.maven.monitor.logging.DefaultLog;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.testing.stubs.MavenProjectStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
class UseDefaultMappingMojoTest {
  @Test
  void test_not_useDefaultMapping() throws Exception {
    LicenseCheckMojo check = new LicenseCheckMojo();
    check.defaultBasedir = new File("src/test/resources/check");
    check.legacyConfigHeader = "header.txt";
    check.project = new MavenProjectStub();
    check.useDefaultMapping = false;
    check.strictCheck = true;
    MockedLog logger = new MockedLog();
    check.setLog(new DefaultLog(logger));
    check.execute();
    Assertions.assertFalse(logger.getContent().contains("header style: text"));
    Assertions.assertTrue(logger.getContent().contains("header style: unknown"));
    String absoluteFileName = new File("src/test/resources/check/doc1.txt").getCanonicalPath().replace('\\', '/');
    Assertions.assertTrue(logger.getContent().contains("Unknown file extension: " + absoluteFileName));
  }

  @Test
  void test_useDefaultMapping() throws Exception {
    LicenseCheckMojo check = new LicenseCheckMojo();
    check.defaultBasedir = new File("src/test/resources/check");
    check.legacyConfigHeader = "header.txt";
    check.project = new MavenProjectStub();
    check.useDefaultMapping = true;
    check.strictCheck = true;
    MockedLog logger = new MockedLog();
    check.setLog(new DefaultLog(logger));
    try {
      check.execute();
      Assertions.fail();
    } catch (MojoExecutionException e) {
      Assertions.assertTrue(logger.getContent().contains("header style: text"));
      String absoluteDockerfileName = new File("src/test/resources/check/Dockerfile").getCanonicalPath().replace('\\', '/');
      Assertions.assertTrue(logger.getContent().contains("Header OK in: " + absoluteDockerfileName));
      Assertions.assertEquals("Some files do not have the expected license header", e.getMessage());
    }
  }
}
