/**
 * Copyright (C) 2008 Mycila (mathieu.carbou@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
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
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 * @author Cedric Pronzato
 * @see com.mycila.maven.plugin.license.header.HeaderDefinition
 */
public enum HeaderType {
    ////////// COMMENT TYPES //////////

    //              FirstLine           Before              EndLine             After             SkipLine                    FirstLineDetection              LastLineDetection       allowBlankLines isMultiline
    //generic
    JAVADOC_STYLE("/**", " * ", " */", "", null, "(\\s|\\t)*/\\*.*$", ".*\\*/(\\s|\\t)*$", false, true, false),
    SCRIPT_STYLE("#", "# ", "#EOL", "", "^#!.*$", "#.*$", "#.*$", false, false, false),
    HAML_STYLE("-#", "-# ", "-#EOL", "", "^-#!.*$", "-#.*$", "-#.*$", false, false, false),
    XML_STYLE("<!--EOL", "    ", "EOL-->", "", "^<\\?xml.*>$", "(\\s|\\t)*<!--.*$", ".*-->(\\s|\\t)*$", true, true, false),
    XML_PER_LINE("EOL", "<!-- ", "EOL", " -->", "^<\\?xml.*>$", "(\\s|\\t)*<!--.*$", ".*-->(\\s|\\t)*$", true, false, true),
    SEMICOLON_STYLE(";", "; ", ";EOL", "", null, ";.*$", ";.*$", false, false, false),
    APOSTROPHE_STYLE("'", "' ", "'EOL", "", null, "'.*$", "'.*$", false, false, false),
    EXCLAMATION_STYLE("!", "! ", "!EOL", "", null, "!.*$", "!.*$", false, false, false),
    DOUBLEDASHES_STYLE("--", "-- ", "--EOL", "", null, "--.*$", "--.*$", false, false, false),
    SLASHSTAR_STYLE("/*", " * ", " */", "", null, "(\\s|\\t)*/\\*.*$", ".*\\*/(\\s|\\t)*$", false, true, false),
    BRACESSTAR_STYLE("\\{*", " * ", " *\\}", "", null, "(\\s|\\t)*\\{\\*.*$", ".*\\*\\}(\\s|\\t)*$", false, true, false),
    SHARPSTAR_STYLE("#*", " * ", " *#", "", null, "(\\s|\\t)*#\\*.*$", ".*\\*#(\\s|\\t)*$", false, true, false),
    DOUBLETILDE_STYLE("~~", "~~ ", "~~EOL", "", null, "~~.*$", "~~.*$", false, false, false),
    DYNASCRIPT_STYLE("<%--EOL", "    ", "EOL--%>", "", null, "(\\s|\\t)*<%--.*$", ".*--%>(\\s|\\t)*$", true, true, false),
    DYNASCRIPT3_STYLE("<!---EOL", "    ", "EOL--->", "", null, "(\\s|\\t)*<!---.*$", ".*--->(\\s|\\t)*$", true, true, false),
    PERCENT3_STYLE("%%%", "%%% ", "%%%EOL", "", null, "%%%.*$", "%%%.*$", false, false, false),
    EXCLAMATION3_STYLE("!!!", "!!! ", "!!!EOL", "", null, "!!!.*$", "!!!.*$", false, false, false),

    DOUBLESLASH_STYLE("//", "// ", "//EOL", "", null, "//.*$", "//.*$", false, false, false),
    // non generic
    PHP("/*", " * ", " */", "", "^<\\?php.*$", "(\\s|\\t)*/\\*.*$", ".*\\*/(\\s|\\t)*$", false, true, false),
    ASP("<%", "    ", "%>", "", null, "(\\s|\\t)*<%[^@].*$", ".*%>(\\s|\\t)*$", true, true, false),
    LUA("--[[EOL", "    ", "EOL]]", "", null, "--\\[\\[$", "\\]\\]$", true, true, false),
    FTL("<#--EOL", "    ", "EOL-->", "", null, "(\\s|\\t)*<#--.*$", ".*-->(\\s|\\t)*$", true, true, false),
    FTL_ALT("[#--EOL", "    ", "EOL--]", "", "\\[#ftl(\\s.*)?\\]", "(\\s|\\t)*\\[#--.*$", ".*--\\](\\s|\\t)*$", true, true, false),
    TEXT("====", "    ", "====EOL", "", null, "====.*$", "====.*$", true, true, false),
    BATCH("@REM", "@REM ", "@REMEOL", "", null, "@REM.*$", "@REM.*$", false, false, false),
    // unknown
    UNKNOWN("", "", "", "", null, null, null, false, false, false);

    ////////////////////////////////////

    //REGEXP NOTE:
    //You can use control characters. I.E. (?si)mathieu.*best maches:
    //Mathieu
    //is the
    //best

    private static final Map<String, HeaderDefinition> DEFINITIONS = new HashMap<String, HeaderDefinition>(values().length);

    static {
        for (HeaderType type : values()) {
            DEFINITIONS.put(type.getDefinition().getType(), type.getDefinition());
        }
    }

    private final HeaderDefinition definition;

    private HeaderType(String firstLine, String beforeEachLine,
                       String endLine, String afterEachLine,
                       String skipLinePattern, String firstLineDetectionPattern, String endLineDetectionPattern,
                       boolean allowBlankLines, boolean isMultiline, boolean padLines) {
        definition = new HeaderDefinition(this.name().toLowerCase(), firstLine, beforeEachLine, endLine, afterEachLine, skipLinePattern, firstLineDetectionPattern, endLineDetectionPattern, allowBlankLines, isMultiline, padLines);
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
     *         com.mycila.maven.plugin.license.header.HeaderType#UNKNOWN}.
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
