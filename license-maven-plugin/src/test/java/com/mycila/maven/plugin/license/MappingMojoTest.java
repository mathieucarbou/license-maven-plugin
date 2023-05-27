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
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.apache.maven.plugin.testing.stubs.MavenProjectStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;

class MappingMojoTest {

  @Test
  void test_mapping() throws Exception {
    LicenseCheckMojo check = new LicenseCheckMojo();
    MockedLog logger = new MockedLog();
    check.setLog(new DefaultLog(logger));
    check.defaultBasedir = new File("src/test/resources/check");
    check.legacyConfigHeader = "header.txt";
    check.project = new MavenProjectStub();
    check.useDefaultMapping = true;
    check.strictCheck = true;

    try {
      check.execute();
      Assertions.fail();
    } catch (MojoExecutionException e) {
      e.printStackTrace(System.out);
      //assertFalse(logger.getContent().contains("header style: javadoc_style"));
      //assertTrue(logger.getContent().contains("header style: text"));
      Assertions.assertEquals("Some files do not have the expected license header. Run license:format to update them.", e.getMessage());
    }

    logger.clear();
    check.mapping = new LinkedHashMap<String, String>() {{
      put("txt", "javadoc_style");
    }};

    try {
      check.execute();
      Assertions.fail();
    } catch (MojoExecutionException e) {
      Assertions.assertTrue(logger.getContent().contains("header style: javadoc_style"));
      Assertions.assertFalse(logger.getContent().contains("header style: text"));
      Assertions.assertEquals("Some files do not have the expected license header. Run license:format to update them.", e.getMessage());
    }
  }

  @Test
  void test_mapping_composed_extension() throws Exception {
    LicenseCheckMojo check = new LicenseCheckMojo();
    MockedLog logger = new MockedLog();
    check.setLog(new DefaultLog(logger));
    //check.setLog(new SystemStreamLog());
    check.defaultBasedir = new File("src/test/resources/check");
    check.legacyConfigHeader = "header.txt";
    check.project = new MavenProjectStub();
    check.legacyConfigIncludes = new String[]{"test.apt.vm"};
    check.defaultProperties = new HashMap<String, String>() {{
      put("year", "2008");
    }};

    try {
      check.execute();
      Assertions.fail();
    } catch (MojoExecutionException e) {
      e.printStackTrace(System.out);
      Assertions.assertTrue(logger.getContent().contains("test.apt.vm [header style: sharpstar_style]"));
      Assertions.assertEquals("Some files do not have the expected license header. Run license:format to update them.", e.getMessage());
    }

    check.setLog(new SystemStreamLog());
    check.mapping = new LinkedHashMap<String, String>() {{
      put("apt.vm", "DOUBLETILDE_STYLE");
    }};

    check.execute();
  }

  @Test
  void test_mapping_composed_extension_ordered() throws Exception {
    LicenseCheckMojo check = new LicenseCheckMojo();
    MockedLog logger = new MockedLog();
    check.setLog(new DefaultLog(logger));
    //check.setLog(new SystemStreamLog());
    check.defaultBasedir = new File("src/test/resources/check/issue107");
    check.legacyConfigHeader = "header.txt";
    check.project = new MavenProjectStub();
    check.legacyConfigIncludes = new String[]{"test.xml.tmpl"};
    check.defaultProperties = new HashMap<String, String>() {{
      put("year", "2008");
    }};

    check.setLog(new SystemStreamLog());
    check.mapping = new LinkedHashMap<String, String>() {{
      put("jmx", "XML_STYLE");
      put("feature", "SCRIPT_STYLE");
      put("properties.tmpl", "SCRIPT_STYLE");
      put("xml.tmpl", "XML_STYLE");
      put("tmpl", "SCRIPT_STYLE");
    }};

    check.execute();
  }

  @Test
  void test_mapping_extension_less_file() throws Exception {
    LicenseCheckMojo check = new LicenseCheckMojo();
    MockedLog logger = new MockedLog();
    check.setLog(new DefaultLog(logger));
    //check.setLog(new SystemStreamLog());
    check.defaultBasedir = new File("src/test/resources/extensionless");
    check.legacyConfigHeader = "header.txt";
    check.project = new MavenProjectStub();
    check.legacyConfigIncludes = new String[]{"extensionless-file"};
    check.defaultProperties = new HashMap<String, String>() {{
      put("year", "2008");
    }};

    /* Run with no mapping first */
    check.execute();
    Assertions.assertTrue(logger.getContent().contains("extensionless-file [header style: unknown]"));

    /* Add the mapping and expect the missing header */
    MockedLog mappedLogger = new MockedLog();
    check.setLog(new DefaultLog(mappedLogger));
    check.mapping = new LinkedHashMap<String, String>() {{
      put("extensionless-file", "SCRIPT_STYLE");
    }};

    try {
      check.execute();
      Assertions.fail();
    } catch (MojoExecutionException e) {
      e.printStackTrace(System.out);
      Assertions.assertTrue(mappedLogger.getContent().contains("extensionless-file [header style: script_style]"));
      Assertions.assertEquals("Some files do not have the expected license header. Run license:format to update them.", e.getMessage());
    }

  }


  @Test
  void test_mapping_unknown_file() throws Exception {
    LicenseCheckMojo check = new LicenseCheckMojo();
    MockedLog logger = new MockedLog();
    check.setLog(new DefaultLog(logger));
    //check.setLog(new SystemStreamLog());
    check.defaultBasedir = new File("src/test/resources/unknown");
    check.legacyConfigHeader = "header.txt";
    check.project = new MavenProjectStub();
    check.legacyConfigIncludes = new String[]{"file.unknown"};
    check.defaultProperties = new HashMap<String, String>() {{
      put("year", "2008");
    }};
    check.failIfUnknown = true;

    /* Run with no mapping first */
    try {
      check.execute();
      Assertions.fail();
    } catch (MojoExecutionException e) {
      String expected = "Unable to find a comment style definition for some "
          + "files. You may want to add a custom mapping for the relevant file extensions.";
      Assertions.assertEquals(expected, e.getMessage());
    }

  }
}
