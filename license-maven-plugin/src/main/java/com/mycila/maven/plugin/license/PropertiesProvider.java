/*
 * Copyright (C) 2008-2023 Mycila (mathieu.carbou@gmail.com)
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

import java.io.Closeable;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

/**
 * The Interface PropertiesProvider.
 */
public interface PropertiesProvider extends Closeable {

  /**
   * Inits the.
   *
   * @param mojo the mojo
   * @param currentProperties the current properties
   */
  default void init(AbstractLicenseMojo mojo, Map<String, String> currentProperties) {
      // Do nothing on default
  }

  /**
   * Adjust properties.
   *
   * @param mojo the mojo
   * @param currentProperties the current properties
   * @param document the document
   * @return the map
   */
  default Map<String, String> adjustProperties(AbstractLicenseMojo mojo,
                                               Map<String, String> currentProperties, Document document) {
    Properties properties = new Properties();
    properties.putAll(currentProperties);
    return getAdditionalProperties(mojo, properties, document);
  }

  /**
   * Gets the additional properties.
   *
   * @param mojo the mojo
   * @param currentProperties the current properties
   * @param document the document
   * @return the additional properties
   * @deprecated Use instead {@link #adjustProperties(AbstractLicenseMojo, Map, Document)}
   */
  @Deprecated
  default Map<String, String> getAdditionalProperties(AbstractLicenseMojo mojo,
                                                      Properties currentProperties, Document document) {
    return Collections.emptyMap();
  }

  /**
   * Close.
   */
  @Override
  default void close() {
      // Do nothing on default
  }
}
