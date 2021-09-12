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
import org.junit.Test;

import java.io.File;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
public final class MvelTest {

  @Test
  public void test_add() throws Exception {
    File tmp = new File("target/test/issues/issue-156/test_add");
    FileUtils.copyFilesToFolder(new File("src/test/resources/issues/issue-156"), tmp);

    AbstractLicenseMojo plugin = new LicenseFormatMojo();
    plugin.project = new MavenProjectStub();
    plugin.defaultBasedir = tmp;
    plugin.legacyConfigHeader = "src/test/resources/issues/issue-156/header1.txt";
    plugin.legacyConfigIncludes = new String[]{"file.mv"};

    plugin.execute();

    String processed = FileUtils.read(new File(tmp, "file.mv"), System.getProperty("file.encoding"));
    String expected = FileUtils.read(new File("src/test/resources/issues/issue-156/expected1.mv"), System.getProperty("file.encoding"));
    assertThat(processed, is(equalTo(expected)));
  }

  @Test
  public void test_update() throws Exception {
    File tmp = new File("target/test/issues/issue-156/test_update");
    FileUtils.copyFilesToFolder(new File("src/test/resources/issues/issue-156"), tmp);

    AbstractLicenseMojo plugin = new LicenseFormatMojo();
    plugin.project = new MavenProjectStub();
    plugin.defaultBasedir = tmp;
    plugin.legacyConfigHeader = "src/test/resources/issues/issue-156/header1.txt";
    plugin.legacyConfigIncludes = new String[]{"file.mv"};

    plugin.execute();

    plugin = new LicenseFormatMojo();
    plugin.project = new MavenProjectStub();
    plugin.defaultBasedir = tmp;
    plugin.legacyConfigHeader = "src/test/resources/issues/issue-156/header2.txt";
    plugin.legacyConfigIncludes = new String[]{"file.mv"};

    plugin.execute();

    String processed = FileUtils.read(new File(tmp, "file.mv"), System.getProperty("file.encoding"));
    String expected = FileUtils.read(new File("src/test/resources/issues/issue-156/expected2.mv"), System.getProperty("file.encoding"));
    assertThat(processed, is(equalTo(expected)));
  }

  @Test
  public void test_remove() throws Exception {
    File tmp = new File("target/test/issues/issue-156/test_remove");
    FileUtils.copyFilesToFolder(new File("src/test/resources/issues/issue-156"), tmp);

    AbstractLicenseMojo plugin = new LicenseFormatMojo();
    plugin.project = new MavenProjectStub();
    plugin.defaultBasedir = tmp;
    plugin.legacyConfigHeader = "src/test/resources/issues/issue-156/header1.txt";
    plugin.legacyConfigIncludes = new String[]{"file.mv"};

    plugin.execute();

    plugin = new LicenseRemoveMojo();
    plugin.project = new MavenProjectStub();
    plugin.defaultBasedir = tmp;
    plugin.legacyConfigHeader = "src/test/resources/issues/issue-156/header1.txt";
    plugin.legacyConfigIncludes = new String[]{"file.mv"};

    plugin.execute();

    String processed = FileUtils.read(new File(tmp, "file.mv"), System.getProperty("file.encoding"));
    String expected = FileUtils.read(new File("src/test/resources/issues/issue-156/file.mv"), System.getProperty("file.encoding"));
    assertThat(processed, is(equalTo(expected)));
  }

  @Test
  public void test_check_failed() throws Exception {
    File tmp = new File("target/test/issues/issue-156/test_check_failed");
    FileUtils.copyFilesToFolder(new File("src/test/resources/issues/issue-156"), tmp);

    AbstractLicenseMojo plugin = new LicenseCheckMojo();
    plugin.project = new MavenProjectStub();
    plugin.defaultBasedir = tmp;
    plugin.legacyConfigHeader = "src/test/resources/issues/issue-156/header1.txt";
    plugin.legacyConfigIncludes = new String[]{"file.mv"};


    try {
      plugin.execute();
      fail();
    } catch (MojoExecutionException e) {
      assertEquals("Some files do not have the expected license header", e.getMessage());
    }
  }

  @Test
  public void test_check_success() throws Exception {
    File tmp = new File("target/test/issues/issue-156/test_check_success");
    FileUtils.copyFilesToFolder(new File("src/test/resources/issues/issue-156"), tmp);

    AbstractLicenseMojo plugin = new LicenseFormatMojo();
    plugin.project = new MavenProjectStub();
    plugin.defaultBasedir = tmp;
    plugin.legacyConfigHeader = "src/test/resources/issues/issue-156/header1.txt";
    plugin.legacyConfigIncludes = new String[]{"file.mv"};

    plugin.execute();

    plugin = new LicenseCheckMojo();
    plugin.project = new MavenProjectStub();
    plugin.defaultBasedir = tmp;
    plugin.legacyConfigHeader = "src/test/resources/issues/issue-156/header1.txt";
    plugin.legacyConfigIncludes = new String[]{"file.mv"};

    plugin.execute();
  }
}
