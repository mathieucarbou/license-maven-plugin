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
package com.mycila.maven.plugin.license.header;

import com.mycila.maven.plugin.license.util.FileUtils;
import com.mycila.xmltool.XMLDoc;
import com.mycila.xmltool.XMLTag;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
public final class AdditionalHeaderDefinitionTest {
    @Test
    public void test_load_definitions() throws Exception {
        XMLTag def = XMLDoc.newDocument(true).addRoot("additionalHeaders")
                .addTag("xquery")
                .addTag("firstLine").addText("(:")
                .addTag("beforeEachLine").addText(" : ")
                .addTag("endLine").addText(" :)EOL")
                .addTag("firstLineDetectionPattern").addText("\\(\\:")
                .addTag("lastLineDetectionPattern").addText("\\:\\)")
                .addTag("allowBlankLines").addText("false")
                .addTag("isMultiline").addText("false");

        System.out.println(def.toString());

        AdditionalHeaderDefinition loader = new AdditionalHeaderDefinition(def);

        assertEquals(loader.getDefinitions().size(), 1);
        assertEquals(loader.getDefinitions().get("xquery").getType(), "xquery");
        assertNull(loader.getDefinitions().get("xquery").getSkipLinePattern());

        Header header = new Header(getClass().getResource("/test-header1.txt"), "UTF-8", null);

        //FileUtils.write(new File("src/test/resources/test-header3.txt"), header.buildForDefinition(loader.getDefinitions().get("xquery")));

        final String content = FileUtils.read(new File("src/test/resources/test-header3.txt"), System.getProperty("file.encoding"));
        assertEquals(header.buildForDefinition(loader.getDefinitions().get("xquery"), content.indexOf("\n") == -1),
                content);
    }

    @Test
    public void test_load_definitions2() throws Exception {
        XMLTag def = XMLDoc.newDocument(true).addRoot("additionalHeaders")
                .addTag("text")
                .addTag("firstLine").addText(":(")
                .addTag("beforeEachLine").addText(" ( ")
                .addTag("endLine").addText(":(")
                .addTag("firstLineDetectionPattern").addText("\\:\\(")
                .addTag("lastLineDetectionPattern").addText("\\:\\(")
                .addTag("allowBlankLines").addText("false")
                .addTag("isMultiline").addText("false");

        System.out.println(def.toString());

        AdditionalHeaderDefinition loader = new AdditionalHeaderDefinition(def);

        Header header = new Header(getClass().getResource("/check/header.txt"), "UTF-8", null);

        System.out.println(header.buildForDefinition(loader.getDefinitions().get("text"), false));
    }

    @Test
    public void test_advanced_definitions() throws Exception {
        XMLTag def = XMLDoc.newDocument(true).addRoot("additionalHeaders")
                .addTag("csregion")
                .addTag("firstLine").addText("#region LicenseEOL/**")
                .addTag("beforeEachLine").addText(" * ")
                .addTag("endLine").addText(" */EOL#endregionEOL")
                .addTag("firstLineDetectionPattern").addText("#region.*^EOL/\\*\\*.*$")
                .addTag("lastLineDetectionPattern").addText("\\*/EOL#endregion")
                .addTag("allowBlankLines").addText("false")
                .addTag("isMultiline").addText("false");

        AdditionalHeaderDefinition loader = new AdditionalHeaderDefinition(def);

        Header header = new Header(getClass().getResource("/test-header1.txt"), "UTF-8", null);

        //FileUtils.write(new File("src/test/resources/test-header4.txt"), header.buildForDefinition(loader.getDefinitions().get("csregion"), false), System.getProperty("file.encoding"));

        final String content = FileUtils.read(new File("src/test/resources/test-header4.txt"), System.getProperty("file.encoding"));
        assertEquals(header.buildForDefinition(loader.getDefinitions().get("csregion"), content.indexOf("\n") == -1),
                content);
    }
}
