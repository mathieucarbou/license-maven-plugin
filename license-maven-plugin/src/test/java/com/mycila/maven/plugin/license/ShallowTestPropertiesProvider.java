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

import com.mycila.maven.plugin.license.document.Document;

import java.util.Collections;
import java.util.Map;

/**
 * Test-only {@link PropertiesProvider} that simulates shallow-repository detection.
 * Activated by setting {@code mojo.skipOnShallow} or {@code mojo.failOnShallow}; a no-op
 * otherwise, so it does not interfere with unrelated tests.
 */
public class ShallowTestPropertiesProvider implements PropertiesProvider {

  @Override
  public void init(AbstractLicenseMojo mojo, Map<String, String> currentProperties) {
    if (mojo.skipOnShallow) {
      throw new ShallowRepositorySkipException("Shallow git repository detected (test stub). Skipping plugin execution.");
    }
    if (mojo.failOnShallow) {
      throw new ShallowRepositoryException("Shallow git repository detected (test stub). Year property values may not be accurate. "
          + "To resolve: perform a full clone or set license.failOnShallow=false.");
    }
  }

  @Override
  public Map<String, String> adjustProperties(AbstractLicenseMojo mojo,
                                              Map<String, String> currentProperties, Document document) {
    return Collections.emptyMap();
  }
}
