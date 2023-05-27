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

import org.apache.maven.monitor.logging.DefaultLog;
import org.apache.maven.plugin.testing.stubs.MavenProjectStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;

class QuietMojoTest {

  @Test
  void test_load_header_from_relative_file() throws Exception {
    LicenseCheckMojo check = new LicenseCheckMojo();
    check.defaultBasedir = new File("src/test/resources/check");
    check.legacyConfigHeader = "header.txt";
    check.project = new MavenProjectStub();
    check.failIfMissing = false;
    check.strictCheck = true;
    check.legacyConfigExcludes = new String[]{"**/issue107/**"};

    MockedLog logger = new MockedLog();
    check.setLog(new DefaultLog(logger));

    check.execute();
    Assertions.assertTrue(logger.getLogEntries() > 2);

    logger.clear();

    check.quiet = true;
    check.execute();
    Assertions.assertEquals(2, logger.getLogEntries());
  }
}
