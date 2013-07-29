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

import com.mycila.maven.plugin.license.util.FileUtils;
import org.apache.maven.plugin.testing.stubs.MavenProjectStub;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
public final class RemoveMojoTest {

    @Test
    public void test_remove() throws Exception {
        File tmp = new File("target/test/remove/txt");
        tmp.mkdirs();
        FileUtils.copyFileToFolder(new File("src/test/resources/remove/doc1.txt"), tmp);
        FileUtils.copyFileToFolder(new File("src/test/resources/remove/doc2.txt"), tmp);
        
        LicenseRemoveMojo remove = new LicenseRemoveMojo();
        remove.basedir = tmp;
        remove.header = "src/test/resources/remove/header.txt";
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
        remove.basedir = tmp;
        remove.header = "src/test/resources/remove/header.txt";
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
        remove.basedir = tmp;
        remove.header = "src/test/resources/remove/header.txt";
        remove.project = new MavenProjectStub();
        remove.execute();

        assertEquals(FileUtils.read(new File(tmp, "issue44.rb"), System.getProperty("file.encoding")), "ruby code here\nand other data\n");
        assertEquals(FileUtils.read(new File(tmp, "issue44-2.rb"), System.getProperty("file.encoding")), "# code comment\nruby code here\nand other data\n");
        assertEquals(FileUtils.read(new File(tmp, "issue44-3.rb"), System.getProperty("file.encoding")), "# code comment\nruby code here\nand other data\n");
    }

    @Test
    public void test_remove_multiline() throws Exception {
        File tmp = new File("target/test/remove/multi");
        tmp.mkdirs();
        FileUtils.copyFileToFolder(new File("src/test/resources/remove/test.xml"), tmp);
        
        LicenseRemoveMojo remove = new LicenseRemoveMojo();
        remove.basedir = tmp;
        remove.header = "src/test/resources/remove/header.txt";
        remove.project = new MavenProjectStub();
        remove.execute();

        assertEquals(FileUtils.read(new File(tmp, "test.xml"), System.getProperty("file.encoding")), "<assembly>\n" +
                "    <id>project</id>\n" +
                "</assembly>\n");
    }

    @Test
    public void test_js() throws Exception {
        File tmp = new File("target/test/remove/js");
        tmp.mkdirs();
        FileUtils.copyFileToFolder(new File("src/test/resources/remove/test.js"), tmp);

        LicenseRemoveMojo remove = new LicenseRemoveMojo();
        remove.basedir = tmp;
        remove.header = "src/test/resources/remove/header.txt";
        remove.project = new MavenProjectStub();
        remove.execute();

        assertEquals(FileUtils.read(new File(tmp, "test.js"), System.getProperty("file.encoding")), "/**\n" +
                " * jrunscript JavaScript built-in functions and objects.\n" +
                " */\n" +
                "\n" +
                "function a(){}");
    }

}