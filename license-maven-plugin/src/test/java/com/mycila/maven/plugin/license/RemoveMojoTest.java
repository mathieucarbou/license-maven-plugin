/**
 * Copyright (C) 2008 Mycila (mathieu.carbou@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
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
import org.junit.Test;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
public final class RemoveMojoTest {
    public static final String LS = "\n";

    @Test
    public void test_remove() throws Exception {
        File tmp = new File("target/test/remove/txt");
        tmp.mkdirs();
        FileUtils.copyFileToFolder(new File("src/test/resources/remove/doc1.txt"), tmp);
        FileUtils.copyFileToFolder(new File("src/test/resources/remove/doc2.txt"), tmp);
        
        LicenseRemoveMojo remove = new LicenseRemoveMojo();
        remove.defaultBasedir = tmp;
        remove.legacyConfigHeader = "src/test/resources/remove/header.txt";
        remove.project = new MavenProjectStub();
        remove.execute();

        assertEquals(FileUtils.read(new File(tmp, "doc1.txt"), System.getProperty("file.encoding")), "some data\r\nand other data\r\n");
        assertEquals(FileUtils.read(new File(tmp, "doc2.txt"), System.getProperty("file.encoding")), "some data\r\nand other data\r\n");
    }

    @Test
    public void test_remove_xml_txt() throws Exception {
        File tmp = new File("target/test/remove/other");
        tmp.mkdirs();
        FileUtils.copyFileToFolder(new File("src/test/resources/doc/doc9.xml"), tmp);
        FileUtils.copyFileToFolder(new File("src/test/resources/doc/doc3.txt"), tmp);

        LicenseRemoveMojo remove = new LicenseRemoveMojo();
        remove.defaultBasedir = tmp;
        remove.legacyConfigHeader = "src/test/resources/remove/header.txt";
        remove.project = new MavenProjectStub();
        remove.execute();

        assertEquals(FileUtils.read(new File(tmp, "doc9.xml"), System.getProperty("file.encoding")), "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\r\n" +
                "<web-app>\r\n" +
                "\r\n" +
                "</web-app>\r\n");
        assertEquals(FileUtils.read(new File(tmp, "doc3.txt"), System.getProperty("file.encoding")), "some data\r\nand other data\r\n");
    }

    @Test
    public void test_remove_script_style() throws Exception {
        File tmp = new File("target/test/remove/issue44");
        tmp.mkdirs();
        FileUtils.copyFileToFolder(new File("src/test/resources/remove/issue44.rb"), tmp);
        FileUtils.copyFileToFolder(new File("src/test/resources/remove/issue44-2.rb"), tmp);
        FileUtils.copyFileToFolder(new File("src/test/resources/remove/issue44-3.rb"), tmp);

        LicenseRemoveMojo remove = new LicenseRemoveMojo();
        remove.defaultBasedir = tmp;
        remove.legacyConfigHeader = "src/test/resources/remove/header.txt";
        remove.project = new MavenProjectStub();
        remove.execute();

        assertEquals(FileUtils.read(new File(tmp, "issue44.rb"), System.getProperty("file.encoding")), "ruby code here" + LS + "and other data" + LS + "");
        assertEquals(FileUtils.read(new File(tmp, "issue44-2.rb"), System.getProperty("file.encoding")), "# code comment" + LS + "ruby code here" + LS + "and other data" + LS + "");
        assertEquals(FileUtils.read(new File(tmp, "issue44-3.rb"), System.getProperty("file.encoding")), "# code comment" + LS + "ruby code here" + LS + "and other data" + LS + "");
    }

    @Test
    public void test_remove_multiline() throws Exception {
        File tmp = new File("target/test/remove/multi");
        tmp.mkdirs();
        FileUtils.copyFileToFolder(new File("src/test/resources/remove/test.xml"), tmp);
        
        LicenseRemoveMojo remove = new LicenseRemoveMojo();
        remove.defaultBasedir = tmp;
        remove.legacyConfigHeader = "src/test/resources/remove/header.txt";
        remove.project = new MavenProjectStub();
        remove.execute();

        assertEquals(FileUtils.read(new File(tmp, "test.xml"), System.getProperty("file.encoding")), "<assembly>" + LS + "" +
                "    <id>project</id>" + LS + "" +
                "</assembly>" + LS + "");
    }

    @Test
    public void test_js() throws Exception {
        File tmp = new File("target/test/remove/js");
        tmp.mkdirs();
        FileUtils.copyFileToFolder(new File("src/test/resources/remove/test.js"), tmp);

        LicenseRemoveMojo remove = new LicenseRemoveMojo();
        remove.defaultBasedir = tmp;
        remove.legacyConfigHeader = "src/test/resources/remove/header.txt";
        remove.project = new MavenProjectStub();
        remove.execute();

        assertEquals(FileUtils.read(new File(tmp, "test.js"), System.getProperty("file.encoding")), "/**" + LS + "" +
                " * jrunscript JavaScript built-in functions and objects." + LS + "" +
                " */" + LS + "" +
                "" + LS + "" +
                "function a(){}");
    }

    @Test
    public void test_issue30_RemoveSucceedsOnAOneLineCommentFile() throws Exception {
        File tmp = new File("target/test/remove/issue30");
        tmp.mkdirs();
        FileUtils.copyFileToFolder(new File("src/test/resources/remove/issue-30/one-line-comment.java"), tmp);
        FileUtils.copyFileToFolder(new File("src/test/resources/remove/issue-30/one-line-comment.ftl"), tmp);

        LicenseRemoveMojo remove = new LicenseRemoveMojo();
        remove.defaultBasedir = tmp;
        remove.legacyConfigHeader = "src/test/resources/remove/header.txt";
        remove.project = new MavenProjectStub();
        remove.execute();
        
        // NPE was thrown in issue-30, let junit check that no Exception is thrown
   }
    
    @Test
    public void test_issue41_cannotRemoveEmptyHeader() throws Exception {
        File tmp = new File("target/test/remove/issue41");
        tmp.mkdirs();
        FileUtils.copyFileToFolder(new File("src/test/resources/remove/issue-41/ASimpleClass.java"), tmp);
        final File destFile = new File (tmp, "ASimpleClass.java");

        List<String> initialLines = Files.readLines(destFile, Charset.forName("UTF-8"));
        assertThat(initialLines.size(), is(2));

        // Let's apply the licene
        LicenseFormatMojo format = new LicenseFormatMojo();
        format.defaultBasedir = tmp;
        format.legacyConfigHeader = "com/mycila/maven/plugin/license/templates/GPL-3.txt";
        format.project = new MavenProjectStub();
        format.execute();
        
        // Let's try to remove it
        LicenseRemoveMojo remove = new LicenseRemoveMojo();
        remove.defaultBasedir = tmp;
        remove.legacyConfigHeader = "com/mycila/maven/plugin/license/templates/GPL-3.txt";
        remove.project = new MavenProjectStub();
//        remove.keywords = new String[]{"GNU"};
        remove.execute();
        
        List<String> linesAfterRemove = Files.readLines(destFile, Charset.forName("UTF-8"));
        assertThat(linesAfterRemove.size(), is(2));
    }
}