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
import org.junit.Test;

import java.io.File;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
public final class DefaultHeaderDefinitionTest {
    @Test
    public void test_styles() throws Exception {
        Header header = new Header(getClass().getResource("/test-header1.txt"), "UTF-8", null);
        for (HeaderDefinition definition : HeaderType.defaultDefinitions().values()) {
            final String content = FileUtils.read(new File(format("src/test/resources/styles/%s.txt", definition.getType())), System.getProperty("file.encoding"));
            assertEquals("Bad header for type: " + definition.getType(), content, header.buildForDefinition(definition, !content.contains("\n")));
        }
    }
}
