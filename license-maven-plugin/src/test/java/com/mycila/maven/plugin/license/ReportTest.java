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
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.testing.stubs.ArtifactStub;
import org.apache.maven.plugin.testing.stubs.MavenProjectStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.charset.Charset;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;

class ReportTest {

  MavenProjectStub mavenProjectStub = new MavenProjectStub();

  @BeforeEach
  void setUp() {
    ArtifactStub artifact = new ArtifactStub();
    mavenProjectStub.setArtifact(artifact);
    artifact.setGroupId("com.mycila");
    artifact.setArtifactId("license-maven-plugin");
    artifact.setVersion("4.2-SNAPSHOT");
  }

  @Test
  void test_summary_check() {
    File baseDir = mavenProjectStub.getBasedir();
    Report report = new Report(null, Report.Action.CHECK, mavenProjectStub, Clock.systemUTC(), false);
    report.add(new File(baseDir, "file1.java"), Report.Result.PRESENT);
    report.add(new File(baseDir, "file2.java"), Report.Result.PRESENT);
    report.add(new File(baseDir, "file3.java"), Report.Result.MISSING);

    String summary = report.getSummary();
    assertThat(summary).contains("3 file(s)");
    assertThat(summary).contains("OK: 2");
    assertThat(summary).contains("Missing: 1");
    assertThat(summary).contains("Unknown: 0");
  }

  @Test
  void test_summary_format() {
    File baseDir = mavenProjectStub.getBasedir();
    Report report = new Report(null, Report.Action.FORMAT, mavenProjectStub, Clock.systemUTC(), false);
    report.add(new File(baseDir, "file1.java"), Report.Result.ADDED);
    report.add(new File(baseDir, "file2.java"), Report.Result.REPLACED);
    report.add(new File(baseDir, "file3.java"), Report.Result.NOOP);
    report.add(new File(baseDir, "file4.java"), Report.Result.NOOP);

    String summary = report.getSummary();
    assertThat(summary).contains("4 file(s)");
    assertThat(summary).contains("Added: 1");
    assertThat(summary).contains("Replaced: 1");
    assertThat(summary).contains("OK: 2");
    assertThat(summary).contains("Unknown: 0");
  }

  @Test
  void test_summary_remove() {
    File baseDir = mavenProjectStub.getBasedir();
    Report report = new Report(null, Report.Action.REMOVE, mavenProjectStub, Clock.systemUTC(), false);
    report.add(new File(baseDir, "file1.java"), Report.Result.REMOVED);
    report.add(new File(baseDir, "file2.java"), Report.Result.REMOVED);
    report.add(new File(baseDir, "file3.java"), Report.Result.NOOP);

    String summary = report.getSummary();
    assertThat(summary).contains("3 file(s)");
    assertThat(summary).contains("Removed: 2");
    assertThat(summary).contains("Skipped: 1");
    assertThat(summary).contains("Unknown: 0");
  }

  @Test
  void test_summary_with_report_skipped() {
    File baseDir = mavenProjectStub.getBasedir();
    // reportSkipped=true should still allow summary to work (just skips file export)
    Report report = new Report(null, Report.Action.CHECK, mavenProjectStub, Clock.systemUTC(), true);
    report.add(new File(baseDir, "file1.java"), Report.Result.PRESENT);
    report.add(new File(baseDir, "file2.java"), Report.Result.MISSING);

    String summary = report.getSummary();
    assertThat(summary).contains("2 file(s)");
    assertThat(summary).contains("OK: 1");
    assertThat(summary).contains("Missing: 1");
  }

  @Test
  void test_check_xml() throws Exception {
    File tmp = new File("target/test/issues/issue-122/test_check_xml");
    FileUtils.copyFilesToFolder(new File("src/test/resources/issues/issue-122"), tmp);

    AbstractLicenseMojo plugin = new LicenseCheckMojo();
    plugin.clock = Clock.fixed(Instant.ofEpochMilli(1631615047644L), ZoneId.systemDefault());
    plugin.project = mavenProjectStub;
    plugin.defaultBasedir = tmp;
    plugin.legacyConfigHeader = "src/test/resources/issues/issue-122/header.txt";
    plugin.legacyConfigIncludes = new String[]{"file*.*"};
    plugin.reportLocation = new File(tmp, "report/license-plugin-report.xml");

    try {
      plugin.execute();
      Assertions.fail();
    } catch (MojoExecutionException | MojoFailureException e) {
    }

    String processed = unixify(FileUtils.read(plugin.reportLocation, Charset.defaultCharset()));
    String expected = FileUtils.read(new File("src/test/resources/issues/issue-122/check.xml"), Charset.defaultCharset());
    assertThat(processed).isEqualTo(expected);
  }

  @Test
  void test_check_json() throws Exception {
    File tmp = new File("target/test/issues/issue-122/test_check_json");
    FileUtils.copyFilesToFolder(new File("src/test/resources/issues/issue-122"), tmp);

    AbstractLicenseMojo plugin = new LicenseCheckMojo();
    plugin.clock = Clock.fixed(Instant.ofEpochMilli(1631615047644L), ZoneId.systemDefault());
    plugin.project = mavenProjectStub;
    plugin.defaultBasedir = tmp;
    plugin.legacyConfigHeader = "src/test/resources/issues/issue-122/header.txt";
    plugin.legacyConfigIncludes = new String[]{"file*.*"};
    plugin.reportLocation = new File(tmp, "report/license-plugin-report.json");

    try {
      plugin.execute();
      Assertions.fail();
    } catch (MojoExecutionException | MojoFailureException e) {
    }

    String processed = unixify(FileUtils.read(plugin.reportLocation, Charset.defaultCharset()));
    String expected = FileUtils.read(new File("src/test/resources/issues/issue-122/check.json"), Charset.defaultCharset()).trim();
    assertThat(processed).isEqualTo(expected);
  }

  @Test
  void test_format_xml() throws Exception {
    File tmp = new File("target/test/issues/issue-122/test_format_xml");
    FileUtils.copyFilesToFolder(new File("src/test/resources/issues/issue-122"), tmp);

    AbstractLicenseMojo plugin = new LicenseFormatMojo();
    plugin.clock = Clock.fixed(Instant.ofEpochMilli(1631615047644L), ZoneId.systemDefault());
    plugin.project = mavenProjectStub;
    plugin.defaultBasedir = tmp;
    plugin.legacyConfigHeader = "src/test/resources/issues/issue-122/header.txt";
    plugin.legacyConfigIncludes = new String[]{"file*.*"};
    plugin.reportLocation = new File(tmp, "report/license-plugin-report.xml");

    plugin.execute();

    String processed = unixify(FileUtils.read(plugin.reportLocation, Charset.defaultCharset()));
    String expected = FileUtils.read(new File("src/test/resources/issues/issue-122/format.xml"), Charset.defaultCharset());
    assertThat(processed).isEqualTo(expected);
  }

  @Test
  void test_format_json() throws Exception {
    File tmp = new File("target/test/issues/issue-122/test_format_json");
    FileUtils.copyFilesToFolder(new File("src/test/resources/issues/issue-122"), tmp);

    AbstractLicenseMojo plugin = new LicenseFormatMojo();
    plugin.clock = Clock.fixed(Instant.ofEpochMilli(1631615047644L), ZoneId.systemDefault());
    plugin.project = mavenProjectStub;
    plugin.defaultBasedir = tmp;
    plugin.legacyConfigHeader = "src/test/resources/issues/issue-122/header.txt";
    plugin.legacyConfigIncludes = new String[]{"file*.*"};
    plugin.reportLocation = new File(tmp, "report/license-plugin-report.json");

    plugin.execute();

    String processed = unixify(FileUtils.read(plugin.reportLocation, Charset.defaultCharset()));
    String expected = FileUtils.read(new File("src/test/resources/issues/issue-122/format.json"), Charset.defaultCharset()).trim();
    assertThat(processed).isEqualTo(expected);
  }

  @Test
  void test_remove_xml() throws Exception {
    File tmp = new File("target/test/issues/issue-122/test_remove_xml");
    FileUtils.copyFilesToFolder(new File("src/test/resources/issues/issue-122"), tmp);

    AbstractLicenseMojo plugin = new LicenseRemoveMojo();
    plugin.clock = Clock.fixed(Instant.ofEpochMilli(1631615047644L), ZoneId.systemDefault());
    plugin.project = mavenProjectStub;
    plugin.defaultBasedir = tmp;
    plugin.legacyConfigHeader = "src/test/resources/issues/issue-122/header.txt";
    plugin.legacyConfigIncludes = new String[]{"file*.*"};
    plugin.reportLocation = new File(tmp, "report/license-plugin-report.xml");

    plugin.execute();

    String processed = unixify(FileUtils.read(plugin.reportLocation, Charset.defaultCharset()));
    String expected = FileUtils.read(new File("src/test/resources/issues/issue-122/remove.xml"), Charset.defaultCharset());
    assertThat(processed).isEqualTo(expected);
  }

  @Test
  void test_remove_json() throws Exception {
    File tmp = new File("target/test/issues/issue-122/test_remove_json");
    FileUtils.copyFilesToFolder(new File("src/test/resources/issues/issue-122"), tmp);

    AbstractLicenseMojo plugin = new LicenseRemoveMojo();
    plugin.clock = Clock.fixed(Instant.ofEpochMilli(1631615047644L), ZoneId.systemDefault());
    plugin.project = mavenProjectStub;
    plugin.defaultBasedir = tmp;
    plugin.legacyConfigHeader = "src/test/resources/issues/issue-122/header.txt";
    plugin.legacyConfigIncludes = new String[]{"file*.*"};
    plugin.reportLocation = new File(tmp, "report/license-plugin-report.json");

    plugin.execute();

    String processed = unixify(FileUtils.read(plugin.reportLocation, Charset.defaultCharset()));
    String expected = FileUtils.read(new File("src/test/resources/issues/issue-122/remove.json"), Charset.defaultCharset()).trim();
    assertThat(processed).isEqualTo(expected);
  }

  private static String unixify(String s) {
    return s.replace("\r", "").replace("\\", "/").replace("//", "/");
  }
}
