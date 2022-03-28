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
import org.apache.maven.plugin.testing.stubs.MavenProjectStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class LicenseSetTest {

  @Test
  void multipleLicenseSets() throws Exception {
    final LicenseSet licenseSet1 = new LicenseSet();
    licenseSet1.basedir = new File("src/test/resources/check/strict");
    licenseSet1.header = "src/test/resources/test-header1-diff.txt";

    final LicenseSet licenseSet2 = new LicenseSet();
    licenseSet2.basedir = new File("src/test/resources/check/issue76");
    licenseSet2.header = "src/test/resources/test-header1.txt";

    final LicenseSet licenseSetWithoutBaseDir = new LicenseSet();
    licenseSetWithoutBaseDir.header = "test-header1.txt";

    final LicenseSet[] licenseSets = {
        licenseSet1,
        licenseSet2,
        licenseSetWithoutBaseDir
    };

    final LicenseCheckMojo check = new LicenseCheckMojo();
    check.licenseSets = licenseSets;
    check.project = new MavenProjectStub();
    check.strictCheck = false;
    check.defaultBasedir = new File("src/test/resources/unknown");
    final MockedLog logger = new MockedLog();
    check.setLog(new DefaultLog(logger));
    check.execute();

    final String log = logger.getContent();
    final String fileFromFirstSet = new File("src/test/resources/check/strict/space.java").getCanonicalPath().replace('\\', '/');
    final String fileFromSecondSet = new File("src/test/resources/check/issue76/after.xml").getCanonicalPath().replace('\\', '/');
    final String fileFromDefaultBaseDirSet = new File("src/test/resources/unknown/header.txt").getCanonicalPath().replace('\\', '/');

    Assertions.assertTrue(log.contains("Header OK in: " + fileFromFirstSet));
    Assertions.assertTrue(log.contains("Header OK in: " + fileFromSecondSet));
    Assertions.assertTrue(log.contains("Header OK in: " + fileFromDefaultBaseDirSet));
  }

  @Test
  void multipleLicenseSetsWithRelativePaths() throws Exception {
    final LicenseSet licenseSet1 = new LicenseSet();
    licenseSet1.basedir = new File("src/test/resources/check/def/../strict");
    licenseSet1.header = "src/test/resources/test-header1-diff.txt";

    final LicenseSet licenseSet2 = new LicenseSet();
    licenseSet2.basedir = new File("src/test/resources/check/def/../issue76");
    licenseSet2.header = "src/test/resources/test-header1.txt";

    final LicenseSet licenseSetWithoutBaseDir = new LicenseSet();
    licenseSetWithoutBaseDir.header = "test-header1.txt";

    final LicenseSet[] licenseSets = {
        licenseSet1,
        licenseSet2,
        licenseSetWithoutBaseDir
    };

    final LicenseCheckMojo check = new LicenseCheckMojo();
    check.licenseSets = licenseSets;
    check.project = new MavenProjectStub();
    check.strictCheck = false;
    check.defaultBasedir = new File("src/test/resources/unknown/../unknown");
    final MockedLog logger = new MockedLog();
    check.setLog(new DefaultLog(logger));
    check.execute();

    final String log = logger.getContent();
    final String fileFromFirstSet = new File("src/test/resources/check/strict/space.java").getCanonicalPath().replace('\\', '/');
    final String fileFromSecondSet = new File("src/test/resources/check/issue76/after.xml").getCanonicalPath().replace('\\', '/');
    final String fileFromDefaultBaseDirSet = new File("src/test/resources/unknown/header.txt").getCanonicalPath().replace('\\', '/');

    Assertions.assertTrue(log.contains("Header OK in: " + fileFromFirstSet));
    Assertions.assertTrue(log.contains("Header OK in: " + fileFromSecondSet));
    Assertions.assertTrue(log.contains("Header OK in: " + fileFromDefaultBaseDirSet));
  }


}
