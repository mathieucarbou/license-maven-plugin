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

import com.mycila.maven.plugin.license.header.HeaderSource.UrlHeaderSource;
import com.mycila.maven.plugin.license.util.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static java.lang.String.format;

class DefaultHeaderDefinitionTest {
  @Test
  void test_styles() throws Exception {
    Header header = new Header(new UrlHeaderSource(getClass().getResource("/test-header1.txt"), StandardCharsets.UTF_8), null);
    for (HeaderDefinition definition : HeaderType.defaultDefinitions().values()) {
      final String content = FileUtils.read(new File(format("src/test/resources/styles/%s.txt", definition.getType())), Charset.defaultCharset());
      Assertions.assertEquals(content, header.buildForDefinition(definition, !containsWindowsLineEnding(content)), "Bad header for type: " + definition.getType());
    }
  }

  private boolean containsWindowsLineEnding(final String content) {
    return content.contains("\r");
  }
}
