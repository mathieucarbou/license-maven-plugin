/*
 * Copyright (C) 2008-2025 Mycila (mathieu.carbou@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mycila.maven.plugin.license;

import org.apache.maven.monitor.logging.DefaultLog;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.testing.stubs.MavenProjectStub;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for shallow/sparse repository detection:
 * {@code license.warnIfShallow}, {@code license.skipOnShallow}, and
 * {@code license.failOnShallow}.
 */
class ShallowRepositoryTest {

  /**
   * When {@code skipOnShallow=true} the mojo must exit cleanly without any
   * exception and without reporting missing or updated headers.
   */
  @Test
  void test_skipOnShallow_skips_execution_cleanly() throws Exception {
    MockedLog logger = new MockedLog();

    LicenseCheckMojo check = new LicenseCheckMojo();
    check.defaultBasedir = new File("src/test/resources/check");
    check.legacyConfigHeader = "header.txt";
    check.project = new MavenProjectStub();
    check.skipOnShallow = true;
    check.setLog(new DefaultLog(logger));

    // Must complete without any exception
    check.execute();

    // Should log an informational skip message
    assertThat(logger.getContent()).contains("License Plugin skipped");
    assertThat(logger.getContent()).contains("Shallow git repository detected (test stub)");

    // No missing-header failures collected
    assertThat(check.missingHeaders).isEmpty();
  }

  /**
   * When {@code skipOnShallow=true} the format mojo must also exit cleanly
   * without modifying any files.
   */
  @Test
  void test_skipOnShallow_skips_format_execution_cleanly() throws Exception {
    MockedLog logger = new MockedLog();

    LicenseFormatMojo format = new LicenseFormatMojo();
    format.defaultBasedir = new File("src/test/resources/check");
    format.legacyConfigHeader = "header.txt";
    format.project = new MavenProjectStub();
    format.skipOnShallow = true;
    format.setLog(new DefaultLog(logger));

    // Must complete without any exception
    format.execute();

    // Should log an informational skip message
    assertThat(logger.getContent()).contains("License Plugin skipped");
    assertThat(logger.getContent()).contains("Shallow git repository detected (test stub)");
  }

  /**
   * When {@code failOnShallow=true} the mojo must fail the build with a
   * {@link MojoFailureException} containing an actionable message.
   */
  @Test
  void test_failOnShallow_fails_build() {
    LicenseCheckMojo check = new LicenseCheckMojo();
    check.defaultBasedir = new File("src/test/resources/check");
    check.legacyConfigHeader = "header.txt";
    check.project = new MavenProjectStub();
    check.failOnShallow = true;

    assertThatThrownBy(check::execute)
        .isInstanceOf(MojoFailureException.class)
        .hasMessageContaining("Shallow git repository detected (test stub)");
  }

  /**
   * When neither {@code skipOnShallow} nor {@code failOnShallow} is set the
   * mojo must execute normally (warn only, no exception).
   */
  @Test
  void test_warnIfShallow_default_continues_execution() throws Exception {
    MockedLog logger = new MockedLog();

    LicenseCheckMojo check = new LicenseCheckMojo();
    check.defaultBasedir = new File("src/test/resources/check");
    check.legacyConfigHeader = "header.txt";
    check.project = new MavenProjectStub();
    // skipOnShallow=false (default), failOnShallow=false (default)
    check.setLog(new DefaultLog(logger));

    // With neither flag set, the ShallowTestPropertiesProvider is a no-op
    // and the mojo should run to completion (possibly reporting missing headers)
    check.failIfMissing = false;
    check.execute();

    // No "skipped" log; normal execution occurred
    assertThat(logger.getContent()).doesNotContain("License Plugin skipped");
  }

  /**
   * {@link ShallowRepositorySkipException} must be a subclass of
   * {@link ShallowRepositoryException} so that existing {@code catch}
   * blocks that handle the parent class also catch the skip variant
   * when the subclass is checked first.
   */
  @Test
  void test_ShallowRepositorySkipException_is_subclass_of_ShallowRepositoryException() {
    ShallowRepositorySkipException skipEx = new ShallowRepositorySkipException("test");
    assertThat(skipEx).isInstanceOf(ShallowRepositoryException.class);
    assertThat(skipEx.getMessage()).isEqualTo("test");
  }
}
