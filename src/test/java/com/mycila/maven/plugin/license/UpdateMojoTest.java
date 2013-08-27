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

import com.google.common.collect.ImmutableMap;
import com.mycila.maven.plugin.license.util.FileUtils;
import org.apache.maven.plugin.testing.stubs.MavenProjectStub;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
public final class UpdateMojoTest {

    @Test
    public void test_update() throws Exception {
        File tmp = new File("target/test/update");
        tmp.mkdirs();
        FileUtils.copyFileToFolder(new File("src/test/resources/update/doc1.txt"), tmp);
        FileUtils.copyFileToFolder(new File("src/test/resources/update/doc2.txt"), tmp);

        LicenseFormatMojo updater = new LicenseFormatMojo();
        updater.basedir = tmp;
        updater.header = "src/test/resources/update/header.txt";
        updater.project = new MavenProjectStub();
        updater.properties = ImmutableMap.of("year", "2008");
        updater.execute();

        assertEquals(FileUtils.read(new File(tmp, "doc1.txt"), System.getProperty("file.encoding")), "====\r\n    My @Copyright license 2 with my-custom-value and 2008 and doc1.txt\r\n====\r\n\r\nsome data\r\n");
        assertEquals(FileUtils.read(new File(tmp, "doc2.txt"), System.getProperty("file.encoding")), "====\r\n    My @Copyright license 2 with my-custom-value and 2008 and doc2.txt\r\n====\r\n\r\nsome data\r\n");
    }

    @Test
    public void test_issue50() throws Exception {
        File tmp = new File("target/test/update/issue50");
        tmp.mkdirs();
        FileUtils.copyFileToFolder(new File("src/test/resources/update/issue50/test1.properties"), tmp);
        FileUtils.copyFileToFolder(new File("src/test/resources/update/issue50/test2.properties"), tmp);
        FileUtils.copyFileToFolder(new File("src/test/resources/update/issue50/test3.properties"), tmp);

        LicenseFormatMojo updater = new LicenseFormatMojo();
        updater.basedir = tmp;
        updater.header = "src/test/resources/update/header.txt";
        updater.properties = ImmutableMap.of("year", "2008");
        updater.mapping = new HashMap<String, String>() {{
            put("properties", "SCRIPT_STYLE");
        }};
        updater.project = new MavenProjectStub();
        updater.execute();

        String test1 = FileUtils.read(new File(tmp, "test1.properties"), System.getProperty("file.encoding"));
        String test2 = FileUtils.read(new File(tmp, "test2.properties"), System.getProperty("file.encoding"));
        String test3 = FileUtils.read(new File(tmp, "test3.properties"), System.getProperty("file.encoding"));

        assertEquals(test1, test2.replace("test2.properties", "test1.properties"));
        assertEquals(test1, test3.replace("test3.properties", "test1.properties"));
    }

    @Test
    public void test_issue48() throws Exception {
        File tmp = new File("target/test/update/issue48");
        tmp.mkdirs();
        FileUtils.copyFileToFolder(new File("src/test/resources/update/issue48/test1.php"), tmp);
        FileUtils.copyFileToFolder(new File("src/test/resources/update/issue48/test2.php"), tmp);

        LicenseFormatMojo updater = new LicenseFormatMojo();
        updater.basedir = tmp;
        updater.header = "src/test/resources/update/header.txt";
        updater.properties = ImmutableMap.of("year", "2008");
        updater.mapping = new HashMap<String, String>() {{
            put("properties", "SCRIPT_STYLE");
        }};
        updater.project = new MavenProjectStub();
        updater.execute();

        assertEquals(FileUtils.read(new File(tmp, "test1.php"), System.getProperty("file.encoding")), "\r\n" +
            "\r\n" +
            "<?php\r\n" +
            "/*\r\n" +
            " * My @Copyright license 2 with my-custom-value and 2008 and test1.php\r\n" +
            " */\r\n" +
            "\r\n" +
            "class Conference extends Service {}\r\n" +
            "\r\n" +
            "?>\r\n");
        assertEquals(FileUtils.read(new File(tmp, "test2.php"), System.getProperty("file.encoding")), "\r\n" +
            "\r\n" +
            "<?php\r\n" +
            "/*\r\n" +
            " * My @Copyright license 2 with my-custom-value and 2008 and test2.php\r\n" +
            " */\r\n" +
            "\r\n" +
            "class Conference extends Service {}\r\n" +
            "\r\n" +
            "?>\r\n");
    }

    @Test
    public void test_issue44() throws Exception {
        File tmp = new File("target/test/update/issue44");
        tmp.mkdirs();
        FileUtils.copyFileToFolder(new File("src/test/resources/update/issue44/issue44-3.rb"), tmp);
        FileUtils.copyFileToFolder(new File("src/test/resources/update/issue44/test.asp"), tmp);

        LicenseFormatMojo updater = new LicenseFormatMojo();
        updater.basedir = tmp;
        updater.header = "src/test/resources/update/header.txt";
        updater.properties = ImmutableMap.of("year", "2008");
        updater.project = new MavenProjectStub();
        updater.execute();

        assertEquals(FileUtils.read(new File(tmp, "issue44-3.rb"), System.getProperty("file.encoding")), "#\n" +
            "# My @Copyright license 2 with my-custom-value and 2008 and issue44-3.rb\n" +
            "#\n" +
            "\n" +
            "# code comment\n" +
            "ruby code here\n");

        assertEquals(FileUtils.read(new File(tmp, "test.asp"), System.getProperty("file.encoding")), "<%\n" +
            "    My @Copyright license 2 with my-custom-value and 2008 and test.asp\n" +
            "%>" +
            "\n" +
            "asp code");
    }

    @Test
    public void test_issue_14() throws Exception {
        File tmp = new File("target/test/update/issue14");
        tmp.mkdirs();
        FileUtils.copyFileToFolder(new File("src/test/resources/update/issue14/test.properties"), tmp);

        LicenseFormatMojo updater = new LicenseFormatMojo();
        updater.basedir = tmp;
        updater.header = "src/test/resources/update/issue14/header.txt";
        updater.project = new MavenProjectStub();
        updater.execute();

        assertEquals(
            "#\n" +
                "# Copyright (C) 2013 Salzburg Research.\n" +
                "#\n" +
                "# Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
                "# you may not use this file except in compliance with the License.\n" +
                "# You may obtain a copy of the License at\n" +
                "#\n" +
                "#         http://www.apache.org/licenses/LICENSE-2.0\n" +
                "#\n" +
                "# Unless required by applicable law or agreed to in writing, software\n" +
                "# distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                "# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
                "# See the License for the specific language governing permissions and\n" +
                "# limitations under the License.\n" +
                "#\n" +
                "\n" +
                "meta.tables            = SHOW TABLES;\n" +
                "meta.version           = SELECT mvalue FROM metadata WHERE mkey = 'version';\n" +
                "\n" +
                "# get sequence numbers\n" +
                "seq.nodes              = SELECT nextval('seq_nodes')\n" +
                "seq.triples            = SELECT nextval('seq_triples')\n" +
                "seq.namespaces         = SELECT nextval('seq_namespaces')\n",
            FileUtils.read(new File(tmp, "test.properties"), System.getProperty("file.encoding")));
    }

}