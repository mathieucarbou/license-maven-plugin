/*
 * Copyright (C) 2008-2024 Mycila (mathieu.carbou@gmail.com)
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

import com.mycila.maven.plugin.license.document.Document;

import java.util.Collections;
import java.util.Map;

/**
 */
public interface PropertiesProvider extends AutoCloseable {

  default void init(AbstractLicenseMojo mojo, Map<String, String> currentProperties) {
      // Do nothing on default
  }

  default Map<String, String> adjustProperties(AbstractLicenseMojo mojo,
                                               Map<String, String> currentProperties, Document document) {
    // Return empty collection on default
    return Collections.emptyMap();
  }

  @Override
  default void close() {
      // Do nothing on default
  }
}
