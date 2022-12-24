/*
 * Copyright (C) 2008-2022 Mycila (mathieu.carbou@gmail.com)
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

import org.apache.maven.plugins.annotations.Parameter;

public class HeaderSection {

  /**
   * The name of this section to match. Example:
   * <p>
   * {@code COPYRIGHT_SECTION}
   */
  @Parameter(required = true)
  public String key;

  /**
   * The default value that will be used. Example:
   * <p>
   * {@code Copyright (C) 2011 http://code.google.com/p/maven-license-plugin/}
   */
  @Parameter(required = true)
  public String defaultValue;

  /**
   * The pattern to use to match this section in the header. Example:
   * <p>
   * {@code Copyright \(C\) \d{4} .*}
   */
  @Parameter(required = true)
  public String ensureMatch;

  /**
   * Is the pattern needs to be applied on several header lines ?
   */
  @Parameter(defaultValue = "false")
  boolean multiLineMatch;

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getDefaultValue() {
    return defaultValue;
  }

  public void setDefaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
  }

  public String getEnsureMatch() {
    return ensureMatch;
  }

  public void setEnsureMatch(String ensureMatch) {
    this.ensureMatch = ensureMatch;
  }

  public boolean isMultiLineMatch() {
    return multiLineMatch;
  }

  public void setMultiLineMatch(boolean multiLineMatch) {
    this.multiLineMatch = multiLineMatch;
  }
}
