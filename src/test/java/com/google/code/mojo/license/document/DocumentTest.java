/**
 * Copyright (C) 2008 http://code.google.com/p/maven-license-plugin/
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

package com.google.code.mojo.license.document;

import com.google.code.mojo.license.header.Header;
import com.google.code.mojo.license.util.FileUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.*;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
public final class DocumentTest {

    Header header;

    @BeforeClass
    public void setup() throws MalformedURLException {
        Map<String, String> props = new HashMap<String, String>();
        props.put("year", "2008");
        header = new Header(new File("src/test/resources/test-header1.txt").toURI().toURL(), props, null);
    }

    @Test
    public void test_create() throws Exception {
        Document doc = new Document(
                new File("src/test/resources/doc/doc1.txt"),
                DocumentType.TXT.getDefaultHeaderType().getDefinition(),
                System.getProperty("file.encoding"), new String[]{"copyright"});
        assertEquals(doc.getFile().getName(), "doc1.txt");
        assertFalse(doc.isNotSupported());
    }

    @Test
    public void test_unsupported() throws Exception {
        Document doc = new Document(
                new File("src/test/resources/doc/doc1.txt"),
                DocumentType.UNKNOWN.getDefaultHeaderType().getDefinition(),
                System.getProperty("file.encoding"), new String[]{"copyright"});
        assertEquals(doc.getFile().getName(), "doc1.txt");
        assertTrue(doc.isNotSupported());
    }

    @Test
    public void test_hasHeader() throws Exception {
        Document doc = new Document(
                new File("src/test/resources/doc/doc1.txt"),
                DocumentType.TXT.getDefaultHeaderType().getDefinition(),
                System.getProperty("file.encoding"), new String[]{"copyright"});
        assertFalse(doc.hasHeader(header, true));
    }

    @Test
    public void test_isHeader() throws Exception {
        Document doc = new Document(
                new File("src/test/resources/doc/doc1.txt"),
                DocumentType.TXT.getDefaultHeaderType().getDefinition(),
                System.getProperty("file.encoding"), new String[]{"copyright"});
        assertFalse(doc.is(header));

        doc = new Document(
                new File("src/test/resources/test-header1.txt"),
                DocumentType.TXT.getDefaultHeaderType().getDefinition(),
                System.getProperty("file.encoding"), new String[]{"copyright"});
        assertTrue(doc.is(header));
    }

    @Test
    public void test_remove_header1() throws Exception {
        Document doc = new Document(
                new File("src/test/resources/doc/doc1.txt"),
                DocumentType.TXT.getDefaultHeaderType().getDefinition(),
                System.getProperty("file.encoding"), new String[]{"copyright"});
        doc.parseHeader();
        doc.removeHeader();
        assertEquals(doc.getContent(),
                FileUtils.read(new File("src/test/resources/doc/doc1.txt"), System.getProperty("file.encoding")));
    }

    @Test
    public void test_remove_header2() throws Exception {
        Document doc = new Document(
                new File("src/test/resources/doc/doc2.txt"),
                DocumentType.TXT.getDefaultHeaderType().getDefinition(),
                System.getProperty("file.encoding"), new String[]{"copyright"});
        doc.parseHeader();
        doc.removeHeader();
        assertEquals(doc.getContent(), "some data\r\n");
    }

    @Test
    public void test_remove_header3() throws Exception {
        Document doc = new Document(
                new File("src/test/resources/doc/doc3.txt"),
                DocumentType.TXT.getDefaultHeaderType().getDefinition(),
                System.getProperty("file.encoding"), new String[]{"copyright"});
        doc.parseHeader();
        doc.removeHeader();
        assertEquals(doc.getContent(), "some data\r\nand other data\r\n");
    }

    @Test
    public void test_remove_header_xml_1() throws Exception {
        Document doc = new Document(
                new File("src/test/resources/doc/doc4.xml"),
                DocumentType.XML.getDefaultHeaderType().getDefinition(),
                System.getProperty("file.encoding"), new String[]{"copyright"});
        doc.parseHeader();
        doc.removeHeader();
        assertEquals(doc.getContent(), "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\r\n<web-app/>\r\n");
    }

    @Test
    public void test_remove_header_xml_2() throws Exception {
        Document doc = new Document(
                new File("src/test/resources/doc/doc5.xml"),
                DocumentType.XML.getDefaultHeaderType().getDefinition(),
                System.getProperty("file.encoding"), new String[]{"copyright"});
        doc.parseHeader();
        doc.removeHeader();
        assertEquals(doc.getContent(), "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\r\n<web-app/>\r\n");
    }

    @Test
    public void test_remove_header_xml_3() throws Exception {
        Document doc = new Document(
                new File("src/test/resources/doc/doc6.xml"),
                DocumentType.XML.getDefaultHeaderType().getDefinition(),
                System.getProperty("file.encoding"), new String[]{"copyright"});
        doc.parseHeader();
        doc.removeHeader();
        assertEquals(doc.getContent(), "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\r\n<web-app/>\r\n");
    }

    @Test
    public void test_remove_header_xml_4() throws Exception {
        Document doc = new Document(
                new File("src/test/resources/doc/doc8.xml"),
                DocumentType.XML.getDefaultHeaderType().getDefinition(),
                System.getProperty("file.encoding"), new String[]{"copyright"});
        doc.parseHeader();
        doc.removeHeader();
        assertTrue(doc.getContent().contains("no key word"));
    }

    @Test
    public void test_remove_header_xml_5() throws Exception {
        Document doc = new Document(
                new File("src/test/resources/doc/doc9.xml"),
                DocumentType.XML.getDefaultHeaderType().getDefinition(),
                System.getProperty("file.encoding"), new String[]{"copyright"});
        doc.parseHeader();
        doc.removeHeader();
        assertEquals(doc.getContent(), "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\r\n" +
                "<web-app>\r\n" +
                "\r\n" +
                "</web-app>\r\n");
    }

}
