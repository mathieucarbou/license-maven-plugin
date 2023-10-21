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
package com.mycila.maven.plugin.license.header;

import com.mycila.maven.plugin.license.util.FileContent;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class HeaderParserTest {

  @Test
  void test_no_header() throws Exception {
    HeaderParser parser = new HeaderParser(new FileContent(new File("src/test/resources/doc/doc1.txt"), System.getProperty("file.encoding")),
        HeaderType.TEXT.getDefinition(), new String[]{"copyright"});
    Assertions.assertFalse(parser.gotAnyHeader());
  }

  @Test
  void test_has_header() throws Exception {
    HeaderParser parser = new HeaderParser(new FileContent(new File("src/test/resources/doc/doc2.txt"), System.getProperty("file.encoding")),
        HeaderType.TEXT.getDefinition(), new String[]{"copyright"});
    Assertions.assertTrue(parser.gotAnyHeader());
    Assertions.assertEquals(0, parser.getBeginPosition());
    Assertions.assertEquals(43, parser.getEndPosition());
  }

  @Test
  void test_has_header2() throws Exception {
    HeaderParser parser = new HeaderParser(new FileContent(new File("src/test/resources/doc/doc3.txt"), System.getProperty("file.encoding")),
        HeaderType.TEXT.getDefinition(), new String[]{"copyright"});
    Assertions.assertTrue(parser.gotAnyHeader());
    Assertions.assertEquals(0, parser.getBeginPosition());
    Assertions.assertEquals(49, parser.getEndPosition());
  }

  @ParameterizedTest
  @MethodSource("parameters")
  void test_parsing_xml(String document, int endPosition) throws Exception {
    HeaderParser parser = new HeaderParser(new FileContent(new File(document), System.getProperty("file.encoding")),
        HeaderType.XML_STYLE.getDefinition(), new String[]{"copyright"});
    if (document == "src/test/resources/doc/doc6.xml") {
      Assertions.assertFalse(parser.gotAnyHeader());
    } else {
      Assertions.assertTrue(parser.gotAnyHeader());
    }
    Assertions.assertEquals(45, parser.getBeginPosition());
    Assertions.assertEquals(endPosition, parser.getEndPosition());
  }

  private static Stream<Object[]> parameters() {
      final List<Object[]> parameters = Arrays.asList(new Object[][] {
        {"src/test/resources/doc/doc4.xml", 862},
        {"src/test/resources/doc/doc5.xml", 864},
        {"src/test/resources/doc/doc6.xml", -1}
      });
      return parameters.stream();
    }

  @Test
  void test_parsing_xml4() throws Exception {
    HeaderParser parser = new HeaderParser(new FileContent(new File("src/test/resources/doc/doc7.xml"), System.getProperty("file.encoding")),
        HeaderType.XML_STYLE.getDefinition(), new String[]{"copyright"});
    Assertions.assertFalse(parser.gotAnyHeader());
  }

  @Test
  void test_parsing_xml5() throws Exception {
    HeaderParser parser = new HeaderParser(new FileContent(new File("src/test/resources/doc/doc8.xml"), System.getProperty("file.encoding")),
        HeaderType.XML_STYLE.getDefinition(), new String[]{"copyright"});
    Assertions.assertFalse(parser.gotAnyHeader());
  }

  @Test
  void test_parsing_xml6() throws Exception {
    HeaderParser parser = new HeaderParser(new FileContent(new File("src/test/resources/doc/doc9.xml"), System.getProperty("file.encoding")),
        HeaderType.XML_STYLE.getDefinition(), new String[]{"copyright"});
    Assertions.assertTrue(parser.gotAnyHeader());
    Assertions.assertEquals(45, parser.getBeginPosition());
    Assertions.assertEquals(864, parser.getEndPosition());
  }
  
  @Test
  public void test_parsing_java1() throws Exception {
     HeaderParser parser = getCustomJavadocHeaderParser("src/test/resources/doc/java1.txt");
     assertTrue(parser.gotAnyHeader());
     assertEquals("package", parser.getFileContent().getContent().substring(parser.getEndPosition()).trim().substring(0, 7));
  }
  
  @Test
  public void test_parsing_java2() throws Exception {
     HeaderParser parser = getCustomJavadocHeaderParser("src/test/resources/doc/java2.txt");
     assertTrue(parser.gotAnyHeader());
     assertEquals("package", parser.getFileContent().getContent().substring(parser.getEndPosition()).trim().substring(0, 7));
  }
  
  @Test
  public void test_parsing_java3() throws Exception {
     HeaderParser parser = getCustomJavadocHeaderParser("src/test/resources/doc/java3.txt");
     assertTrue(parser.gotAnyHeader());
     assertEquals("package", parser.getFileContent().getContent().substring(parser.getEndPosition()).trim().substring(0, 7));
  }
  
  private HeaderParser getCustomJavadocHeaderParser(final String fileName) {
     final HeaderDefinition headerDefinition = new HeaderDefinition(
           "javadoc2_style", "/**", " ** ", " **/", "", null, "^\\s*/\\*.*$", "^\\s*\\*.*$", "^.*\\*/\\s*$", false, true, false);
     return new HeaderParser(new FileContent(new File(fileName), System.getProperty("file.encoding")), headerDefinition, new String[]{"copyright"});
  }
  
  
}
