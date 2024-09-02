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

import com.mycila.maven.plugin.license.header.HeaderDefinition;
import org.apache.maven.plugins.annotations.Parameter;

public class HeaderStyle {

  /** The name of this header style. */
  @Parameter(required = true)
  public String name;

  /**
   * The first fixed line of this header. Default to none.
   */
  @Parameter
  public String firstLine = "";

  /**
   * The last fixed line of this header. Default to none.
   */
  @Parameter
  public String endLine = "";

  /**
   * The characters to prepend before each license header lines. Default to empty.
   */
  @Parameter
  public String beforeEachLine = "";

  /**
   * The characters to append after each license header lines. Default to empty.
   */
  @Parameter
  public String afterEachLine = "";

  /**
   * Specify whether this is a multi-line comment style or not.
   * <p>
   * A multi-line comment style is equivalent to what we have in Java, where a first line and line will delimit a whole
   * multi-line comment section.
   * <p>
   * A style that is not multi-line is usually repeating in each line the characters before and after each line to delimit a one-line comment.
   */
  @Parameter(alias = "multiline")
  public boolean multiLine = true;

  /**
   * Only for multi-line comments: specify if blank lines are allowed.
   * <p>
   * Defaulted to false because most of the time, a header has some characters on each line ({@link #beforeEachLine})
   */
  @Parameter
  public boolean allowBlankLines;

  /**
   * Only for non multi-line comments: specify if some spaces should be added after the header line and before the {@link #afterEachLine} characters so that all the lines are aligned.
   * <p>
   * Default to false.
   */
  @Parameter
  public boolean padLines;

  /**
   * A regex to define a first line in a file that should be skipped and kept untouched, like the XML declaration at the top of XML documents
   * <p>
   * Non set by default.
   */
  @Parameter
  public String skipLinePattern;

  /** The regex used to detect the start of a header section or line. */
  @Parameter(required = true)
  public String firstLineDetectionPattern;

  /** The regex used to detect the end of a header section or line. */
  @Parameter(required = true)
  public String lastLineDetectionPattern;

  public HeaderDefinition toHeaderDefinition() {
    return new HeaderDefinition(name, firstLine, beforeEachLine, endLine, afterEachLine, skipLinePattern, firstLineDetectionPattern, lastLineDetectionPattern, allowBlankLines, multiLine, padLines);
  }
}
