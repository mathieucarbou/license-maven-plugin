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
package com.mycila.maven.plugin.license.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

class FileUtilsTest {
  @Test
  void test_jar_url_read() {
    // the assumption here is, that junit's @Test class is within a jar on the classpath
    URL resourceUrl = Test.class.getResource("/" + Test.class.getName().replace('.', '/') + ".class");
    final Charset encoding = StandardCharsets.US_ASCII;
    final String magic = encoding.decode(ByteBuffer.wrap(new byte[]{(byte) 0xCA, (byte) 0xFE, (byte) 0xBA, (byte) 0xBE})).toString();

    String singlePlainContent = Assertions.assertDoesNotThrow(() -> FileUtils.read(resourceUrl, encoding));
    Assertions.assertEquals(magic, singlePlainContent.substring(0, 4));

    String[] multipleContents = Assertions.assertDoesNotThrow(() -> FileUtils.read(new URL[]{ resourceUrl, resourceUrl }, encoding));
    Assertions.assertEquals(2, multipleContents.length);
    Assertions.assertEquals(magic, multipleContents[0].substring(0, 4));
    Assertions.assertEquals(magic, multipleContents[1].substring(0, 4));

    String interpolatedContent = Assertions.assertDoesNotThrow(() -> FileUtils.read(resourceUrl, encoding, new HashMap<>()));
    Assertions.assertEquals(magic, interpolatedContent.substring(0, 4));
  }

  @Test
  void test_read_first_lines() throws Exception {
    String s = FileUtils.readFirstLines(new File("src/test/data/compileCP/test2.txt"), 3, StandardCharsets.ISO_8859_1);
    Assertions.assertTrue(s.contains("c"));
    Assertions.assertFalse(s.contains("d"));
  }
}
