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

import com.mycila.maven.plugin.license.util.FileContent;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
public final class HeaderParserTest {

    @Test
    public void test_no_header() throws Exception {
        HeaderParser parser = new HeaderParser(new FileContent(new File("src/test/resources/doc/doc1.txt"), System.getProperty("file.encoding")),
                HeaderType.TEXT.getDefinition(), new String[]{"copyright"});
        assertFalse(parser.gotAnyHeader());
    }

    @Test
    public void test_has_header() throws Exception {
        HeaderParser parser = new HeaderParser(new FileContent(new File("src/test/resources/doc/doc2.txt"), System.getProperty("file.encoding")),
                HeaderType.TEXT.getDefinition(), new String[]{"copyright"});
        assertTrue(parser.gotAnyHeader());
        assertEquals(parser.getBeginPosition(), 0);
        assertEquals(parser.getEndPosition(), 43);
    }

    @Test
    public void test_has_header2() throws Exception {
        HeaderParser parser = new HeaderParser(new FileContent(new File("src/test/resources/doc/doc3.txt"), System.getProperty("file.encoding")),
                HeaderType.TEXT.getDefinition(), new String[]{"copyright"});
        assertTrue(parser.gotAnyHeader());
        assertEquals(parser.getBeginPosition(), 0);
        assertEquals(parser.getEndPosition(), 49);
    }

    @Test
    public void test_parsing_xml1() throws Exception {
        HeaderParser parser = new HeaderParser(new FileContent(new File("src/test/resources/doc/doc4.xml"), System.getProperty("file.encoding")),
                HeaderType.XML_STYLE.getDefinition(), new String[]{"copyright"});
        assertTrue(parser.gotAnyHeader());
        assertEquals(parser.getBeginPosition(), 45);
        assertEquals(parser.getEndPosition(), 862);
    }

    @Test
    public void test_parsing_xml2() throws Exception {
        HeaderParser parser = new HeaderParser(new FileContent(new File("src/test/resources/doc/doc5.xml"), System.getProperty("file.encoding")),
                HeaderType.XML_STYLE.getDefinition(), new String[]{"copyright"});
        assertTrue(parser.gotAnyHeader());
        assertEquals(parser.getBeginPosition(), 45);
        assertEquals(parser.getEndPosition(), 866);
    }

    @Test
    public void test_parsing_xml3() throws Exception {
        HeaderParser parser = new HeaderParser(new FileContent(new File("src/test/resources/doc/doc6.xml"), System.getProperty("file.encoding")),
                HeaderType.XML_STYLE.getDefinition(), new String[]{"copyright"});
        assertFalse(parser.gotAnyHeader());
    }

    @Test
    public void test_parsing_xml4() throws Exception {
        HeaderParser parser = new HeaderParser(new FileContent(new File("src/test/resources/doc/doc7.xml"), System.getProperty("file.encoding")),
                HeaderType.XML_STYLE.getDefinition(), new String[]{"copyright"});
        assertFalse(parser.gotAnyHeader());
    }

    @Test
    public void test_parsing_xml5() throws Exception {
        HeaderParser parser = new HeaderParser(new FileContent(new File("src/test/resources/doc/doc8.xml"), System.getProperty("file.encoding")),
                HeaderType.XML_STYLE.getDefinition(), new String[]{"copyright"});
        assertFalse(parser.gotAnyHeader());
    }

    @Test
    public void test_parsing_xml6() throws Exception {
        HeaderParser parser = new HeaderParser(new FileContent(new File("src/test/resources/doc/doc9.xml"), System.getProperty("file.encoding")),
                HeaderType.XML_STYLE.getDefinition(), new String[]{"copyright"});
        assertTrue(parser.gotAnyHeader());
        assertEquals(parser.getBeginPosition(), 45);
        assertEquals(parser.getEndPosition(), 870);
    }
}
