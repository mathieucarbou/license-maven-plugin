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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class HeaderDefinitionTest {

  @Test
  void test_ok1() throws Exception {
    HeaderDefinition def = new HeaderDefinition("aa", "firstLine", "before", "end", "", "skip", "firstDetect", "lastDetect", false, false, false);
    def.validate();
  }

  @Test
  void test_ok2() throws Exception {
    HeaderDefinition def = new HeaderDefinition("aa", "firstLine", "before", "end", "after", null, "firstDetect", "lastDetect", false, false, false);
    def.validate();
  }

  @Test
  void test_missing_firstLine() throws Exception {
    Assertions.assertThrows(IllegalStateException.class, () -> {
      new HeaderDefinition("aa", null, "before", "end", "after", null, "firstDetect", "lastDetect", false, false, false);
    });
  }

  @Test
  void test_missing_before() throws Exception {
    Assertions.assertThrows(IllegalStateException.class, () -> {
      new HeaderDefinition("aa", "firstLine", null, "end", "after", null, "firstDetect", "lastDetect", false, false, false);
    });
  }

  @Test
  void test_missing_end() throws Exception {
    Assertions.assertThrows(IllegalStateException.class, () -> {
      new HeaderDefinition("aa", "firstLine", "before", null, "after", null, "firstDetect", "lastDetect", false, false, false);
    });
  }

  @Test
  void test_missing_after() throws Exception {
    Assertions.assertThrows(IllegalStateException.class, () -> {
      new HeaderDefinition("aa", "firstLine", "before", "end", null, null, "firstDetect", "lastDetect", false, false, false);
    });
  }

  @Test
  void test_missing_firstDetect() throws Exception {
    Assertions.assertThrows(IllegalStateException.class, () -> {
      new HeaderDefinition("aa", "firstLine", "before", "end", "", null, "", "lastDetect", false, false, false);
    });
  }

  @Test
  void test_missing_lastDetect() throws Exception {
    Assertions.assertThrows(IllegalStateException.class, () -> {
      new HeaderDefinition("aa", "firstLine", "before", "end", "", null, "firstDetect", "", false, false, false);
    });
  }
}
