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
package com.mycila.maven.plugin.license.util;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
public final class FileContentTest {
    @Test
    public void test_states() throws Exception {
        FileContent c = new FileContent(new File("src/test/data/compileCP/test2.txt"), System.getProperty("file.encoding"));
        assertEquals(c.nextLine(), "a");
        assertEquals(c.getPosition(), 3);
    }

    @Test
    public void test_delete() throws Exception {
        FileContent c = new FileContent(new File("src/test/data/compileCP/test2.txt"), System.getProperty("file.encoding"));
        c.delete(2, 8);
        assertEquals(c.getContent(), "a\r\nd\r\ne\r\nf\r\ng\r\nh\r\ni\r\n");
    }

    @Test
    public void test_insert() throws Exception {
        FileContent c = new FileContent(new File("src/test/data/compileCP/test2.txt"), System.getProperty("file.encoding"));
        c.insert(4, "hello");
        assertEquals(c.getContent(), "a\r\nbhello\r\nc\r\nd\r\ne\r\nf\r\ng\r\nh\r\ni\r\n");
    }

    @Test
    public void test_removeDupliatedEmptyEndLines() throws Exception {
        FileContent c = new FileContent(new File("src/test/data/compileCP/test3.txt"), System.getProperty("file.encoding"));
        c.removeDuplicatedEmptyEndLines();
        assertEquals(c.getContent(), "a\r\nb\r\n");

        c = new FileContent(new File("src/test/data/compileCP/test4.txt"), System.getProperty("file.encoding"));
        c.removeDuplicatedEmptyEndLines();
        assertEquals(c.getContent(), "\r\n");
    }
}
