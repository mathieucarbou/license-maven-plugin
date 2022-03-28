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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.mycila.maven.plugin.license.util.FileUtils;
import java.io.File;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.testing.stubs.ArtifactStub;
import org.apache.maven.plugin.testing.stubs.MavenProjectStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
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

    String processed = unixify(FileUtils.read(plugin.reportLocation, System.getProperty("file.encoding")));
    String expected = FileUtils.read(new File("src/test/resources/issues/issue-122/check.xml"), System.getProperty("file.encoding"));
    assertThat(processed, is(equalTo(expected)));
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

    String processed = unixify(FileUtils.read(plugin.reportLocation, System.getProperty("file.encoding")));
    String expected = FileUtils.read(new File("src/test/resources/issues/issue-122/check.json"), System.getProperty("file.encoding"));
    assertThat(processed, is(equalTo(expected)));
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

    String processed = unixify(FileUtils.read(plugin.reportLocation, System.getProperty("file.encoding")));
    String expected = FileUtils.read(new File("src/test/resources/issues/issue-122/format.xml"), System.getProperty("file.encoding"));
    assertThat(processed, is(equalTo(expected)));
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

    String processed = unixify(FileUtils.read(plugin.reportLocation, System.getProperty("file.encoding")));
    String expected = FileUtils.read(new File("src/test/resources/issues/issue-122/format.json"), System.getProperty("file.encoding"));
    assertThat(processed, is(equalTo(expected)));
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

    String processed = unixify(FileUtils.read(plugin.reportLocation, System.getProperty("file.encoding")));
    String expected = FileUtils.read(new File("src/test/resources/issues/issue-122/remove.xml"), System.getProperty("file.encoding"));
    assertThat(processed, is(equalTo(expected)));
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

    String processed = unixify(FileUtils.read(plugin.reportLocation, System.getProperty("file.encoding")));
    String expected = FileUtils.read(new File("src/test/resources/issues/issue-122/remove.json"), System.getProperty("file.encoding"));
    assertThat(processed, is(equalTo(expected)));
  }

  private static String unixify(String s) {
    return s.replace("\r", "").replace("\\", "/").replace("//", "/");
  }
}
