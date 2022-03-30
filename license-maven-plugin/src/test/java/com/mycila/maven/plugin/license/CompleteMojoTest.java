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

import com.mycila.maven.plugin.license.header.HeaderType;
import com.mycila.maven.plugin.license.util.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.testing.stubs.MavenProjectStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Stream;

import static com.mycila.maven.plugin.license.header.HeaderType.*;
import static java.util.Arrays.asList;
import static java.util.EnumSet.complementOf;
import static java.util.EnumSet.copyOf;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
class CompleteMojoTest {

  private static Stream<Object[]> parameters() {
    final List<Object[]> parameters = asList(new Object[][]{
        {ASCIIDOC_STYLE, "adoc"},
        {MVEL_STYLE, "mv"},
        {APOSTROPHE_STYLE, "vba"},
        {JAVADOC_STYLE, "java"},
        {SCALA_STYLE, "scala"},
        {JAVAPKG_STYLE, "java"},
        {SCRIPT_STYLE, "sh"},
        {HAML_STYLE, "haml"},
        {XML_STYLE, "xml"},
        {XML_PER_LINE, "xml"},
        {SEMICOLON_STYLE, "asm"},
        {EXCLAMATION_STYLE, "f"},
        {DOUBLEDASHES_STYLE, "e"},
        {SLASHSTAR_STYLE, "java"},
        {BRACESSTAR_STYLE, "pas"},
        {SHARPSTAR_STYLE, "vm"},
        {DOUBLETILDE_STYLE, "apt"},
        {DYNASCRIPT_STYLE, "jsp"},
        {DYNASCRIPT3_STYLE, "cfm"},
        {PERCENT_STYLE, "tex"},
        {PERCENT3_STYLE, "erl"},
        {EXCLAMATION3_STYLE, "el"},
        {DOUBLESLASH_STYLE, "java"},
        {SINGLE_LINE_DOUBLESLASH_STYLE, "java"},
        {TRIPLESLASH_STYLE, "ts"},
        {PHP, "php"},
        {ASP, "asp"},
        {LUA, "lua"},
        {FTL, "ftl"},
        {FTL_ALT, "ftl"},
        {TEXT, "txt"},
        {BATCH, "bat"},
        {MUSTACHE_STYLE, "mustache"}
    });

    EnumSet<HeaderType> set = complementOf(copyOf(parameters.stream()
        .map(oo -> oo[0])
        .map(HeaderType.class::cast)
        .collect(toList())));
    set.remove(UNKNOWN);
    if (!set.isEmpty()) {
      Assertions.fail("Missing test cases: " + set);
    }

    return parameters.stream();
  }

  @ParameterizedTest
  @MethodSource("parameters")
  void test_add(HeaderType headerType, String extension) throws Exception {
    File tmp = new File("target/test/complete/" + headerType + "/test_add");
    FileUtils.copyFilesToFolder(new File("src/test/resources/complete/" + headerType), tmp);

    AbstractLicenseMojo plugin = new LicenseFormatMojo();
    plugin.project = new MavenProjectStub();
    plugin.defaultBasedir = tmp;
    plugin.legacyConfigHeader = "src/test/resources/complete/header1.txt";
    plugin.legacyConfigIncludes = new String[]{"file." + extension};
    plugin.mapping = Collections.singletonMap(extension, headerType.name());

    plugin.execute();

    String processed = FileUtils.read(new File(tmp, "file." + extension), System.getProperty("file.encoding"));
    String expected = FileUtils.read(new File("src/test/resources/complete/" + headerType + "/expected1." + extension), System.getProperty("file.encoding"));
    assertThat(processed, is(equalTo(expected)));
  }

  @ParameterizedTest
  @MethodSource("parameters")
  void test_update(HeaderType headerType, String extension) throws Exception {
    File tmp = new File("target/test/complete/" + headerType + "/test_update");
    FileUtils.copyFilesToFolder(new File("src/test/resources/complete/" + headerType), tmp);

    AbstractLicenseMojo plugin = new LicenseFormatMojo();
    plugin.project = new MavenProjectStub();
    plugin.defaultBasedir = tmp;
    plugin.legacyConfigHeader = "src/test/resources/complete/header1.txt";
    plugin.legacyConfigIncludes = new String[]{"file." + extension};
    plugin.mapping = Collections.singletonMap(extension, headerType.name());

    plugin.execute();

    plugin = new LicenseFormatMojo();
    plugin.project = new MavenProjectStub();
    plugin.defaultBasedir = tmp;
    plugin.legacyConfigHeader = "src/test/resources/complete/header2.txt";
    plugin.legacyConfigIncludes = new String[]{"file." + extension};
    plugin.mapping = Collections.singletonMap(extension, headerType.name());

    plugin.execute();

    String processed = FileUtils.read(new File(tmp, "file." + extension), System.getProperty("file.encoding"));
    String expected = FileUtils.read(new File("src/test/resources/complete/" + headerType + "/expected2." + extension), System.getProperty("file.encoding"));
    assertThat(processed, is(equalTo(expected)));
  }

  @ParameterizedTest
  @MethodSource("parameters")
  void test_remove(HeaderType headerType, String extension) throws Exception {
    File tmp = new File("target/test/complete/" + headerType + "/test_remove");
    FileUtils.copyFilesToFolder(new File("src/test/resources/complete/" + headerType), tmp);

    AbstractLicenseMojo plugin = new LicenseFormatMojo();
    plugin.project = new MavenProjectStub();
    plugin.defaultBasedir = tmp;
    plugin.legacyConfigHeader = "src/test/resources/complete/header1.txt";
    plugin.legacyConfigIncludes = new String[]{"file." + extension};
    plugin.mapping = Collections.singletonMap(extension, headerType.name());

    plugin.execute();

    plugin = new LicenseRemoveMojo();
    plugin.project = new MavenProjectStub();
    plugin.defaultBasedir = tmp;
    plugin.legacyConfigHeader = "src/test/resources/complete/header1.txt";
    plugin.legacyConfigIncludes = new String[]{"file." + extension};
    plugin.mapping = Collections.singletonMap(extension, headerType.name());
    //plugin.setLog(new DebugLog());

    plugin.execute();

    String processed = FileUtils.read(new File(tmp, "file." + extension), System.getProperty("file.encoding"));
    String expected = FileUtils.read(new File("src/test/resources/complete/" + headerType + "/file." + extension), System.getProperty("file.encoding"));
    assertThat(processed, is(equalTo(expected)));
  }

  @ParameterizedTest
  @MethodSource("parameters")
  void test_check_failed(HeaderType headerType, String extension) throws Exception {
    File tmp = new File("target/test/complete/" + headerType + "/test_check_failed");
    FileUtils.copyFilesToFolder(new File("src/test/resources/complete/" + headerType), tmp);

    AbstractLicenseMojo plugin = new LicenseCheckMojo();
    plugin.project = new MavenProjectStub();
    plugin.defaultBasedir = tmp;
    plugin.legacyConfigHeader = "src/test/resources/complete/header1.txt";
    plugin.legacyConfigIncludes = new String[]{"file." + extension};
    plugin.mapping = Collections.singletonMap(extension, headerType.name());


    try {
      plugin.execute();
      Assertions.fail();
    } catch (MojoExecutionException e) {
      Assertions.assertEquals("Some files do not have the expected license header", e.getMessage());
    }
  }

  @ParameterizedTest
  @MethodSource("parameters")
  void test_check_success(HeaderType headerType, String extension) throws Exception {
    File tmp = new File("target/test/complete/" + headerType + "/test_check_success");
    FileUtils.copyFilesToFolder(new File("src/test/resources/complete/" + headerType), tmp);

    AbstractLicenseMojo plugin = new LicenseFormatMojo();
    plugin.project = new MavenProjectStub();
    plugin.defaultBasedir = tmp;
    plugin.legacyConfigHeader = "src/test/resources/complete/header1.txt";
    plugin.legacyConfigIncludes = new String[]{"file." + extension};
    plugin.mapping = Collections.singletonMap(extension, headerType.name());

    plugin.execute();

    plugin = new LicenseCheckMojo();
    plugin.project = new MavenProjectStub();
    plugin.defaultBasedir = tmp;
    plugin.legacyConfigHeader = "src/test/resources/complete/header1.txt";
    plugin.legacyConfigIncludes = new String[]{"file." + extension};
    plugin.mapping = Collections.singletonMap(extension, headerType.name());

    plugin.execute();
  }
//
//  public static void main(String[] args) throws IOException, MojoExecutionException, MojoFailureException {
//    for (Object[] parameter : parameters()) {
//      HeaderType headerType = (HeaderType) parameter[0];
//      String extension = (String) parameter[1];
//      final File root = new File("src/test/resources/complete/" + headerType);
//
//      if (root.exists()) {
//        continue;
//      }
//
//      root.mkdirs();
//
//      Files.write(new File(root, "file." + extension).toPath(), "### Hello world!\n\nI am a Markdown doc\n".getBytes(StandardCharsets.UTF_8));
//      Files.write(new File(root, "expected1." + extension).toPath(), "### Hello world!\n\nI am a Markdown doc\n".getBytes(StandardCharsets.UTF_8));
//      Files.write(new File(root, "expected2." + extension).toPath(), "### Hello world!\n\nI am a Markdown doc\n".getBytes(StandardCharsets.UTF_8));
//
//      AbstractLicenseMojo plugin = new LicenseFormatMojo();
//      plugin.project = new MavenProjectStub();
//      plugin.defaultBasedir = root;
//      plugin.legacyConfigHeader = "src/test/resources/complete/header1.txt";
//      plugin.legacyConfigIncludes = new String[]{"expected1." + extension};
//      plugin.mapping = Collections.singletonMap(extension, headerType.name());
//      plugin.execute();
//
//      plugin = new LicenseFormatMojo();
//      plugin.project = new MavenProjectStub();
//      plugin.defaultBasedir = root;
//      plugin.legacyConfigHeader = "src/test/resources/complete/header2.txt";
//      plugin.legacyConfigIncludes = new String[]{"expected2." + extension};
//      plugin.mapping = Collections.singletonMap(extension, headerType.name());
//      plugin.execute();
//    }
//  }
}
