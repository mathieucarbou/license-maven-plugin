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

import org.apache.maven.plugins.annotations.Parameter;

import java.util.Arrays;

public class Multi {

  public static final String DEFAULT_SEPARATOR =
      "---------------------------------------------------------------------";

  /**
   * Preamble text which if present is placed before the first header.
   */
  @Parameter
  String preamble;

  /**
   * Location of each header. It can be a relative path, absolute path,
   * classpath resource, any URL. The plugin first check if the name specified
   * is a relative file, then an absolute file, then in the classpath. If not
   * found, it tries to construct a URL from the location.
   */
  @Parameter(alias = "header")
  String[] headers;

  /**
   * Header, as text, directly in pom file. Using a CDATA section is strongly recommended.
   */
  @Parameter(alias = "inlineHeader")
  String[] inlineHeaders;

  /**
   * One of more separators between the headers.
   * If there is only one separator it is placed between each header.
   * If there are multiple separators, then the first separator is placed
   * between the first and second license, the second separator is placed
   * between the second and third license, and so on...
   */
  @Parameter(alias = "separator")
  String[] separators;

  public String getPreamble() {
    return preamble;
  }

  public void setPreamble(final String preamble) {
    this.preamble = preamble;
  }

  public String[] getHeaders() {
    return headers;
  }

  /**
   * Set a header.
   * <p>
   * Used by Maven to configure the {@link #headers}
   * field, as the class field name is different
   * to the alias name used in the pom.xml
   * plugin configuration.
   *
   * @param header the header to set
   */
  public void setHeader(final String header) {
    if (headers == null) {
      headers = new String[]{header};
    } else {
      headers = Arrays.copyOf(headers, headers.length + 1);
      headers[headers.length - 1] = header;
    }
  }

  public void setHeaders(final String[] headers) {
    this.headers = headers;
  }

  public String[] getInlineHeaders() {
    return inlineHeaders;
  }

  /**
   * Set an inline header.
   * <p>
   * Used by Maven to configure the {@link #inlineHeaders}
   * field, as the class field name is different
   * to the alias name used in the pom.xml
   * plugin configuration.
   *
   * @param inlineHeader the inline header to set
   */
  public void setInlineHeader(final String inlineHeader) {
    if (inlineHeaders == null) {
      inlineHeaders = new String[]{inlineHeader};
    } else {
      inlineHeaders = Arrays.copyOf(inlineHeaders, inlineHeaders.length + 1);
      inlineHeaders[inlineHeaders.length - 1] = inlineHeader;
    }
  }

  public void setInlineHeaders(final String[] inlineHeaders) {
    this.inlineHeaders = inlineHeaders;
  }

  public String[] getSeparators() {
    return separators;
  }

  /**
   * Set a separator.
   * <p>
   * Used by Maven to configure the {@link #separators}
   * field, as the class field name is different
   * to the alias name used in the pom.xml
   * plugin configuration.
   *
   * @param separator the separator to set
   */
  public void setSeparator(final String separator) {
    if (separators == null) {
      separators = new String[]{separator};
    } else {
      separators = Arrays.copyOf(separators, separators.length + 1);
      separators[separators.length - 1] = separator;
    }
  }

  public void setSeparators(final String[] separators) {
    this.separators = separators;
  }
}
