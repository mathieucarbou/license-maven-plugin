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
package com.mycila.maven.plugin.license.header;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Defines the default header definitions available out of the box within this plugin.
 *
 * @see com.mycila.maven.plugin.license.header.HeaderDefinition
 */
public enum HeaderType {

  // Comment Type (firstLine, beforeEachLine, endLine, afterEachLine, skipLinePattern, firstLineDetectionPattern, lastLineDetectionPattern, allowBlankLines, multiLine, padLines)

  /** The asciidoc style. */
  ASCIIDOC_STYLE("////", "  // ", "////EOL", "", null, "^////$", "^////$", false, true, false),

  /** The mvel style. */
  MVEL_STYLE("@comment{", "  ", "}", "", null, "@comment\\{$", "\\}$", true, true, false),

  /** The javadoc style. */
  JAVADOC_STYLE("/**", " * ", " */", "", null, "(\\s|\\t)*/\\*.*$", ".*\\*/(\\s|\\t)*$", false, true, false),

  /** The scala style. */
  SCALA_STYLE("/**", "  * ", "  */", "", null, "(\\s|\\t)*/\\*.*$", ".*\\*/(\\s|\\t)*$", false, true, false),

  /** The javapkg style. */
  JAVAPKG_STYLE("EOL/*-", " * ", " */", "", "^package [a-z_]+(\\.[a-z_][a-z0-9_]*)*;$", "(EOL)*(\\s|\\t)*/\\*.*$", ".*\\*/(\\s|\\t)*$", false, true, false),

  /** The script style. */
  SCRIPT_STYLE("#", "# ", "#EOL", "", "^#!.*$", "#.*$", "#.*$", false, false, false),

  /** The haml style. */
  HAML_STYLE("-#", "-# ", "-#EOL", "", "^-#!.*$", "-#.*$", "-#.*$", false, false, false),

  /** The xml style. */
  XML_STYLE("<!--EOL", "    ", "EOL-->", "", "^<\\?xml.*>$", "(\\s|\\t)*<!--.*$", ".*-->(\\s|\\t)*$", true, true, false),

  /** The xml per line. */
  XML_PER_LINE("EOL", "<!-- ", "EOL", " -->", "^<\\?xml.*>$", "(\\s|\\t)*<!--.*$", ".*-->(\\s|\\t)*$", false, false, true),

  /** The semicolon style. */
  SEMICOLON_STYLE(";", "; ", ";EOL", "", null, ";.*$", ";.*$", false, false, false),

  /** The apostrophe style. */
  APOSTROPHE_STYLE("'", "' ", "'EOL", "", null, "'.*$", "'.*$", false, false, false),

  /** The exclamation style. */
  EXCLAMATION_STYLE("!", "! ", "!EOL", "", null, "!.*$", "!.*$", false, false, false),

  /** The doubledashes style. */
  DOUBLEDASHES_STYLE("--", "-- ", "--EOL", "", null, "--.*$", "--.*$", false, false, false),

  /** The slashstar style. */
  SLASHSTAR_STYLE("/*", " * ", " */", "", null, "(\\s|\\t)*/\\*.*$", ".*\\*/(\\s|\\t)*$", false, true, false),

  /** The bracesstar style. */
  BRACESSTAR_STYLE("{*", " * ", " *}", "", null, "(\\s|\\t)*\\{\\*.*$", ".*\\*\\}(\\s|\\t)*$", false, true, false),

  /** The sharpstar style. */
  SHARPSTAR_STYLE("#*", " * ", " *#", "", null, "(\\s|\\t)*#\\*.*$", ".*\\*#(\\s|\\t)*$", false, true, false),

  /** The doubletilde style. */
  DOUBLETILDE_STYLE("~~", "~~ ", "~~EOL", "", null, "~~.*$", "~~.*$", false, false, false),

  /** The dynascript style. */
  DYNASCRIPT_STYLE("<%--EOL", "    ", "EOL--%>", "", null, "(\\s|\\t)*<%--.*$", ".*--%>(\\s|\\t)*$", true, true, false),

  /** The dynascript3 style. */
  DYNASCRIPT3_STYLE("<!---EOL", "    ", "EOL--->", "", null, "(\\s|\\t)*<!---.*$", ".*--->(\\s|\\t)*$", true, true, false),

  /** The percent style. */
  PERCENT_STYLE("", "% ", "EOL", "", null, "^% .*$", "^% .*$", false, false, false),

  /** The percent3 style. */
  PERCENT3_STYLE("%%%", "%%% ", "%%%EOL", "", null, "%%%.*$", "%%%.*$", false, false, false),

  /** The exclamation3 style. */
  EXCLAMATION3_STYLE("!!!", "!!! ", "!!!EOL", "", null, "!!!.*$", "!!!.*$", false, false, false),

  /** The doubleslash style. */
  DOUBLESLASH_STYLE("//", "// ", "//EOL", "", null, "//.*$", "//.*$", false, false, false),

  /** The single line doubleslash style. */
  SINGLE_LINE_DOUBLESLASH_STYLE("", "// ", "", "", null, "//.*$", "//.*$", false, false, false),

  /** The tripleslash style. */
  TRIPLESLASH_STYLE("///", "/// ", "///EOL", "", null, "///.*$", "///.*$", false, false, false),

  /** The php. */
  // non generic
  PHP("/*", " * ", " */", "", "^<\\?php.*$", "(\\s|\\t)*/\\*.*$", ".*\\*/(\\s|\\t)*$", false, true, false),

  /** The asp. */
  ASP("<%", "' ", "%>", "", null, "(\\s|\\t)*<%( .*)?$", ".*%>(\\s|\\t)*$", true, true, false),

  /** The lua. */
  LUA("--[[EOL", "    ", "EOL]]", "", null, "--\\[\\[$", "\\]\\]$", true, true, false),

  /** The ftl. */
  FTL("<#--EOL", "    ", "EOL-->", "", null, "(\\s|\\t)*<#--.*$", ".*-->(\\s|\\t)*$", true, true, false),

  /** The ftl alt. */
  FTL_ALT("[#--EOL", "    ", "EOL--]", "", "\\[#ftl(\\s.*)?\\]", "(\\s|\\t)*\\[#--.*$", ".*--\\](\\s|\\t)*$", true, true, false),

  /** The text. */
  TEXT("====", "    ", "====EOL", "", null, "====.*$", "====.*$", true, true, false),

  /** The batch. */
  BATCH("@REM", "@REM ", "@REMEOL", "", null, "@REM.*$", "@REM.*$", false, false, false),

  /** The mustache style. */
  MUSTACHE_STYLE("{{!", "    ", "}}", "", null, "\\{\\{\\!.*$", "\\}\\}.*$", false, true, false),

  /** The unknown. */
  // unknown
  UNKNOWN("", "", "", "", null, null, null, false, false, false);

  /** The Constant DEFINITIONS. */
  private static final Map<String, HeaderDefinition> DEFINITIONS = new HashMap<String, HeaderDefinition>(values().length);

  static {
    for (HeaderType type : values()) {
      DEFINITIONS.put(type.getDefinition().getType(), type.getDefinition());
    }
  }

  /** The definition. */
  private final HeaderDefinition definition;

  /**
   * Instantiates a new header type.
   *
   * @param firstLine the first line
   * @param beforeEachLine the before each line
   * @param endLine the end line
   * @param afterEachLine the after each line
   * @param skipLinePattern the skip line pattern
   * @param firstLineDetectionPattern the first line detection pattern
   * @param lastLineDetectionPattern the last line detection pattern
   * @param allowBlankLines the allow blank lines
   * @param multiLine the multi line
   * @param padLines the pad lines
   */
  private HeaderType(String firstLine, String beforeEachLine,
                     String endLine, String afterEachLine,
                     String skipLinePattern, String firstLineDetectionPattern, String lastLineDetectionPattern,
                     boolean allowBlankLines, boolean multiLine, boolean padLines) {
    definition = new HeaderDefinition(this.name().toLowerCase(), firstLine, beforeEachLine, endLine, afterEachLine, skipLinePattern, firstLineDetectionPattern, lastLineDetectionPattern, allowBlankLines, multiLine, padLines);
  }

  /**
   * Returns the <code>HeaderDefinition</code> which corresponds to this enumeration instance.
   *
   * @return The header definition.
   */
  public HeaderDefinition getDefinition() {
    return definition;
  }

  /**
   * Returns the <code>HeaderType</code> declared in this enumeration for the given header type name.
   *
   * @param name The header definition type name.
   * @return The <code>HeaderType</code> declared in this enumeration if found or {@link
   * com.mycila.maven.plugin.license.header.HeaderType#UNKNOWN}.
   */
  public static HeaderType fromName(String name) {
    for (HeaderType type : values()) {
      if (type.name().equalsIgnoreCase(name)) {
        return type;
      }
    }
    return UNKNOWN;
  }

  /**
   * Returns the header definitions of every default definitions declared by this enumeration as a map using the
   * header type name as key.
   *
   * @return The default definitions declared by this enumeration.
   */
  public static Map<String, HeaderDefinition> defaultDefinitions() {
    return Collections.unmodifiableMap(DEFINITIONS);
  }
}
