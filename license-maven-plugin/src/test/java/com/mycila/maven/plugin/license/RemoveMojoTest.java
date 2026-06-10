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

import com.google.common.io.Files;
import com.mycila.maven.plugin.license.util.FileUtils;
import org.apache.maven.plugin.testing.stubs.MavenProjectStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

final class RemoveMojoTest {
  public static final String LS = "\n";

  @Test
  void test_remove() throws Exception {
    File tmp = new File("target/test/remove/txt");
    tmp.mkdirs();
    FileUtils.copyFileToFolder(new File("src/test/resources/remove/doc1.txt"), tmp);
    FileUtils.copyFileToFolder(new File("src/test/resources/remove/doc2.txt"), tmp);

    LicenseRemoveMojo remove = new LicenseRemoveMojo();
    remove.legacyDefaultBasedir = tmp;
    remove.legacyConfigHeader = "src/test/resources/remove/header.txt";
    remove.project = new MavenProjectStub();
    remove.execute();

    Assertions.assertEquals("some data\r\nand other data\r\n", FileUtils.read(new File(tmp, "doc1.txt"), Charset.defaultCharset()));
    Assertions.assertEquals("some data\r\nand other data\r\n", FileUtils.read(new File(tmp, "doc2.txt"), Charset.defaultCharset()));
  }

  @Test
  void test_remove_xml_txt() throws Exception {
    File tmp = new File("target/test/remove/other");
    tmp.mkdirs();
    FileUtils.copyFileToFolder(new File("src/test/resources/doc/doc9.xml"), tmp);
    FileUtils.copyFileToFolder(new File("src/test/resources/doc/doc3.txt"), tmp);

    LicenseRemoveMojo remove = new LicenseRemoveMojo();
    remove.legacyDefaultBasedir = tmp;
    remove.legacyConfigHeader = "src/test/resources/remove/header.txt";
    remove.project = new MavenProjectStub();
    remove.execute();

    Assertions.assertEquals("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\r\n" +
        "\r\n" +
        "\r\n" +
        "\r\n" +
        "<web-app>\r\n" +
        "\r\n" +
        "</web-app>\r\n", FileUtils.read(new File(tmp, "doc9.xml"), Charset.defaultCharset()));
    Assertions.assertEquals("some data\r\nand other data\r\n", FileUtils.read(new File(tmp, "doc3.txt"), Charset.defaultCharset()));
  }

  @Test
  void test_remove_script_style() throws Exception {
    File tmp = new File("target/test/remove/issue44");
    tmp.mkdirs();
    FileUtils.copyFileToFolder(new File("src/test/resources/remove/issue44.rb"), tmp);
    FileUtils.copyFileToFolder(new File("src/test/resources/remove/issue44-2.rb"), tmp);
    FileUtils.copyFileToFolder(new File("src/test/resources/remove/issue44-3.rb"), tmp);

    LicenseRemoveMojo remove = new LicenseRemoveMojo();
    remove.legacyDefaultBasedir = tmp;
    remove.legacyConfigHeader = "src/test/resources/remove/header.txt";
    remove.project = new MavenProjectStub();
    remove.execute();

    Assertions.assertEquals("ruby code here" + LS + "and other data" + LS + "", FileUtils.read(new File(tmp, "issue44.rb"), Charset.defaultCharset()));
    Assertions.assertEquals("# code comment" + LS + "ruby code here" + LS + "and other data" + LS + "", FileUtils.read(new File(tmp, "issue44-2.rb"), Charset.defaultCharset()));
    Assertions.assertEquals("# code comment" + LS + "ruby code here" + LS + "and other data" + LS + "", FileUtils.read(new File(tmp, "issue44-3.rb"), Charset.defaultCharset()));
  }

  @Test
  void test_remove_multiline() throws Exception {
    File tmp = new File("target/test/remove/multi");
    tmp.mkdirs();
    FileUtils.copyFileToFolder(new File("src/test/resources/remove/test.xml"), tmp);

    LicenseRemoveMojo remove = new LicenseRemoveMojo();
    remove.legacyDefaultBasedir = tmp;
    remove.legacyConfigHeader = "src/test/resources/remove/header.txt";
    remove.project = new MavenProjectStub();
    remove.execute();

    Assertions.assertEquals("<assembly>" + LS + "" +
        "  <id>project</id>" + LS + "" +
        "</assembly>" + LS + "", FileUtils.read(new File(tmp, "test.xml"), Charset.defaultCharset()));
  }

  @Test
  void test_js_1() throws Exception {
    File tmp = new File("target/test/remove/js");
    tmp.mkdirs();
    FileUtils.copyFileToFolder(new File("src/test/resources/remove/test1.js"), tmp);

    LicenseRemoveMojo remove = new LicenseRemoveMojo();
    remove.legacyDefaultBasedir = tmp;
    remove.legacyConfigHeader = "src/test/resources/remove/header.txt";
    remove.project = new MavenProjectStub();
    remove.execute();

    Assertions.assertEquals("/**" + LS + "" +
        " * jrunscript JavaScript built-in functions and objects." + LS + "" +
        " */" + LS + "" +
        "" + LS + "" +
        "function a(){}", FileUtils.read(new File(tmp, "test1.js"), Charset.defaultCharset()).trim());
  }

  @Test
  void test_js_2() throws Exception {
    File tmp = new File("target/test/remove/js");
    tmp.mkdirs();
    FileUtils.copyFileToFolder(new File("src/test/resources/remove/test2.js"), tmp);

    LicenseRemoveMojo remove = new LicenseRemoveMojo();
    remove.legacyDefaultBasedir = tmp;
    remove.legacyConfigHeader = "src/test/resources/remove/header.txt";
    remove.project = new MavenProjectStub();
    remove.execute();

    Assertions.assertEquals("/**" + LS + "" +
        " * jrunscript JavaScript built-in functions and objects." + LS + "" +
        " */" + LS + "" +
        "" + LS + "" +
        "function a(){}", FileUtils.read(new File(tmp, "test2.js"), Charset.defaultCharset()).trim());
  }

  @Test
  void test_issue30_RemoveSucceedsOnAOneLineCommentFile() throws Exception {
    File tmp = new File("target/test/remove/issue30");
    tmp.mkdirs();
    FileUtils.copyFileToFolder(new File("src/test/resources/remove/issue-30/one-line-comment.java"), tmp);
    FileUtils.copyFileToFolder(new File("src/test/resources/remove/issue-30/one-line-comment.ftl"), tmp);

    LicenseRemoveMojo remove = new LicenseRemoveMojo();
    remove.legacyDefaultBasedir = tmp;
    remove.legacyConfigHeader = "src/test/resources/remove/header.txt";
    remove.project = new MavenProjectStub();
    remove.execute();

    // NPE was thrown in issue-30, let junit check that no Exception is thrown
  }

  @Test
  void test_issue41_cannotRemoveEmptyHeader() throws Exception {
    File tmp = new File("target/test/remove/issue41");
    tmp.mkdirs();
    FileUtils.copyFileToFolder(new File("src/test/resources/remove/issue-41/ASimpleClass.java"), tmp);
    final File destFile = new File(tmp, "ASimpleClass.java");

    List<String> initialLines = Files.readLines(destFile, StandardCharsets.UTF_8);
    org.assertj.core.api.Assertions.assertThat(initialLines).hasSize(2);

    // Let's apply the licene
    LicenseFormatMojo format = new LicenseFormatMojo();
    format.legacyDefaultBasedir = tmp;
    format.legacyConfigHeader = "com/mycila/maven/plugin/license/templates/GPL-3.txt";
    format.project = new MavenProjectStub();
    format.execute();

    // Let's try to remove it
    LicenseRemoveMojo remove = new LicenseRemoveMojo();
    remove.legacyDefaultBasedir = tmp;
    remove.legacyConfigHeader = "com/mycila/maven/plugin/license/templates/GPL-3.txt";
    remove.project = new MavenProjectStub();
//        remove.keywords = new String[]{"GNU"};
    remove.execute();

    List<String> linesAfterRemove = Files.readLines(destFile, StandardCharsets.UTF_8);
    org.assertj.core.api.Assertions.assertThat(linesAfterRemove).hasSize(2);
  }
}
