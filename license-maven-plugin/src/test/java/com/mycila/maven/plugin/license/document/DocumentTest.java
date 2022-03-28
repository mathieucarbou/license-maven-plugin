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
package com.mycila.maven.plugin.license.document;

import com.mycila.maven.plugin.license.header.Header;
import com.mycila.maven.plugin.license.header.HeaderSource.UrlHeaderSource;
import com.mycila.maven.plugin.license.util.FileUtils;
import java.io.File;
import java.io.IOException;
import java.util.Properties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
class DocumentTest {

  static Header header;
  static DocumentPropertiesLoader loader = new DocumentPropertiesLoader() {
    @Override
    public Properties load(Document d) {
      Properties props = new Properties();
      props.put("year", "2008");
      return props;
    }
  };

  @BeforeAll
  static void setup() throws IOException {
    header = new Header(new UrlHeaderSource(new File("src/test/resources/test-header1.txt").toURI().toURL(), "UTF-8"), null);
  }

  @Test
  void test_create() throws Exception {
    Document doc = new Document(
        new File("src/test/resources/doc/doc1.txt"),
        DocumentType.TXT.getDefaultHeaderType().getDefinition(),
        System.getProperty("file.encoding"), new String[]{"copyright"},
        loader);
    Assertions.assertEquals("doc1.txt", doc.getFile().getName());
    Assertions.assertFalse(doc.isNotSupported());
  }

  @Test
  void test_unsupported() throws Exception {
    Document doc = new Document(
        new File("src/test/resources/doc/doc1.txt"),
        DocumentType.UNKNOWN.getDefaultHeaderType().getDefinition(),
        System.getProperty("file.encoding"), new String[]{"copyright"},
        loader);
    Assertions.assertEquals("doc1.txt", doc.getFile().getName());
    Assertions.assertTrue(doc.isNotSupported());
  }

  @Test
  void test_hasHeader() throws Exception {
    Document doc = new Document(
        new File("src/test/resources/doc/doc1.txt"),
        DocumentType.TXT.getDefaultHeaderType().getDefinition(),
        System.getProperty("file.encoding"), new String[]{"copyright"},
        loader);
    Assertions.assertFalse(doc.hasHeader(header, true));
  }

  @Test
  void test_isHeader() throws Exception {
    Document doc = new Document(
        new File("src/test/resources/doc/doc1.txt"),
        DocumentType.TXT.getDefaultHeaderType().getDefinition(),
        System.getProperty("file.encoding"), new String[]{"copyright"},
        loader);
    Assertions.assertFalse(doc.is(header));

    doc = new Document(
        new File("src/test/resources/test-header1.txt"),
        DocumentType.TXT.getDefaultHeaderType().getDefinition(),
        System.getProperty("file.encoding"), new String[]{"copyright"},
        loader);
    Assertions.assertTrue(doc.is(header));
  }

  @Test
  void test_remove_header1() throws Exception {
    Document doc = new Document(
        new File("src/test/resources/doc/doc1.txt"),
        DocumentType.TXT.getDefaultHeaderType().getDefinition(),
        System.getProperty("file.encoding"), new String[]{"copyright"},
        loader);
    doc.parseHeader();
    doc.removeHeader();
    Assertions.assertEquals(doc.getContent(),
        FileUtils.read(new File("src/test/resources/doc/doc1.txt"), System.getProperty("file.encoding")));
  }

  @Test
  void test_remove_header2() throws Exception {
    Document doc = new Document(
        new File("src/test/resources/doc/doc2.txt"),
        DocumentType.TXT.getDefaultHeaderType().getDefinition(),
        System.getProperty("file.encoding"), new String[]{"copyright"},
        loader);
    doc.parseHeader();
    doc.removeHeader();
    Assertions.assertEquals("some data\r\n", doc.getContent());
  }

  @Test
  void test_remove_header3() throws Exception {
    Document doc = new Document(
        new File("src/test/resources/doc/doc3.txt"),
        DocumentType.TXT.getDefaultHeaderType().getDefinition(),
        System.getProperty("file.encoding"), new String[]{"copyright"},
        loader);
    doc.parseHeader();
    doc.removeHeader();
    Assertions.assertEquals("some data\r\nand other data\r\n", doc.getContent());
  }

  @Test
  void test_remove_header_xml_1() throws Exception {
    Document doc = new Document(
        new File("src/test/resources/doc/doc4.xml"),
        DocumentType.XML.getDefaultHeaderType().getDefinition(),
        System.getProperty("file.encoding"), new String[]{"copyright"},
        loader);
    doc.parseHeader();
    doc.removeHeader();
    Assertions.assertEquals("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\r\n<web-app/>\r\n", doc.getContent());
  }

  @Test
  void test_remove_header_xml_2() throws Exception {
    Document doc = new Document(
        new File("src/test/resources/doc/doc5.xml"),
        DocumentType.XML.getDefaultHeaderType().getDefinition(),
        System.getProperty("file.encoding"), new String[]{"copyright"},
        loader);
    doc.parseHeader();
    doc.removeHeader();
    Assertions.assertEquals("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\r\n\r\n<web-app/>\r\n", doc.getContent());
  }

  @Test
  void test_remove_header_xml_3() throws Exception {
    Document doc = new Document(
        new File("src/test/resources/doc/doc6.xml"),
        DocumentType.XML.getDefaultHeaderType().getDefinition(),
        System.getProperty("file.encoding"), new String[]{"copyright"},
        loader);
    doc.parseHeader();
    doc.removeHeader();
    Assertions.assertEquals("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\r\n<web-app/>\r\n", doc.getContent());
  }

  @Test
  void test_remove_header_xml_4() throws Exception {
    Document doc = new Document(
        new File("src/test/resources/doc/doc8.xml"),
        DocumentType.XML.getDefaultHeaderType().getDefinition(),
        System.getProperty("file.encoding"), new String[]{"copyright"},
        loader);
    doc.parseHeader();
    doc.removeHeader();
    Assertions.assertTrue(doc.getContent().contains("no key word"));
  }

  @Test
  void test_remove_header_xml_5() throws Exception {
    Document doc = new Document(
        new File("src/test/resources/doc/doc9.xml"),
        DocumentType.XML.getDefaultHeaderType().getDefinition(),
        System.getProperty("file.encoding"), new String[]{"copyright"},
        loader);
    doc.parseHeader();
    doc.removeHeader();
    Assertions.assertEquals(doc.getContent(), "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\r\n\r\n\r\n\r\n" +
        "<web-app>\r\n" +
        "\r\n" +
        "</web-app>\r\n");
  }

}
