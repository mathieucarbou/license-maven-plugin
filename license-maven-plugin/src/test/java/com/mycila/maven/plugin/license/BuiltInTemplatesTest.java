/*
 * Copyright (C) 2008-2025 Mycila (mathieu.carbou@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mycila.maven.plugin.license;

import com.mycila.maven.plugin.license.header.Header;
import com.mycila.maven.plugin.license.header.HeaderSource.UrlHeaderSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class BuiltInTemplatesTest {

  private static final String TEMPLATES_BASE = "/com/mycila/maven/plugin/license/templates/";

  static Stream<String> templates() {
    return Stream.of(
        "AGPL-3.txt",
        "APACHE-2-noemail.txt",
        "APACHE-2.txt",
        "Artistic-2.0.txt",
        "BSD-2.txt",
        "BSD-3.txt",
        "BSD-4.txt",
        "BUSL-11.txt",
        "CC0-1.0.txt",
        "CDDL-1.0.txt",
        "COMMONS-CLAUSE-1.txt",
        "CPAL.txt",
        "EPL-1.txt",
        "EPL-2.txt",
        "EUPL-1.1.txt",
        "EUPL-1.2.txt",
        "EUPL-1.txt",
        "GPL-2-ONLY.txt",
        "GPL-2.txt",
        "GPL-3-ONLY.txt",
        "GPL-3.txt",
        "ISC.txt",
        "LGPL-21-ONLY.txt",
        "LGPL-21.txt",
        "LGPL-3-ONLY.txt",
        "LGPL-3.txt",
        "MIT.txt",
        "MPL-1.txt",
        "MPL-2.txt",
        "MirOS.txt",
        "SSPL-1.txt",
        "UNLICENSE.txt",
        "WTFPL.txt"
    );
  }

  @Test
  void test_template_count() {
    assertThat(templates().count()).isEqualTo(33);
  }

  @ParameterizedTest
  @MethodSource("templates")
  void test_template_loadable(String templateName) throws Exception {
    URL url = getClass().getResource(TEMPLATES_BASE + templateName);
    assertThat(url).as("Template not found: " + templateName).isNotNull();
    Header header = new Header(new UrlHeaderSource(url, StandardCharsets.UTF_8), null);
    assertThat(header.getLineCount()).as("Template has no content: " + templateName).isGreaterThan(0);
  }
}
