/**
 * Copyright (C) 2008 http://code.google.com/p/maven-license-plugin/
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

package com.google.code.mojo.license.header;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Defines the default header definitions available out of the box within this plugin.
 *
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 * @author Cedric Pronzato
 * @see com.google.code.mojo.license.header.HeaderDefinition
 */
public enum HeaderType {
    ////////// COMMENT TYPES //////////

    //              FirstLine           Before              EndLine             SkipLine                    FirstLineDetection              LastLineDetection       allowBlankLines isMultiline
    //generic
    JAVADOC_STYLE("/**", " * ", " */", null, "(\\s|\\t)*/\\*.*$", ".*\\*/(\\s|\\t)*$", false, true),
    SCRIPT_STYLE("#", "# ", "#EOL", "^#!.*$", "#.*$", "#.*$", false, false),
    HAML_STYLE("-#", "-# ", "-#EOL", "^-#!.*$", "-#.*$", "-#.*$", false, false),
    XML_STYLE("<!--EOL", "    ", "EOL-->", "^<\\?xml.*>$", "(\\s|\\t)*<!--.*$", ".*-->(\\s|\\t)*$", true, true),
    SEMICOLON_STYLE(";", "; ", ";EOL", null, ";.*$", ";.*$", false, false),
    APOSTROPHE_STYLE("'", "' ", "'EOL", null, "'.*$", "'.*$", false, false),
    EXCLAMATION_STYLE("!", "! ", "!EOL", null, "!.*$", "!.*$", false, false),
    DOUBLEDASHES_STYLE("--", "-- ", "--EOL", null, "--.*$", "--.*$", false, false),
    SLASHSTAR_STYLE("/*", " * ", " */", null, "(\\s|\\t)*/\\*.*$", ".*\\*/(\\s|\\t)*$", false, true),
    BRACESSTAR_STYLE("\\{*", " * ", " *\\}", null, "(\\s|\\t)*\\{\\*.*$", ".*\\*\\}(\\s|\\t)*$", false, true),
    SHARPSTAR_STYLE("#*", " * ", " *#", null, "(\\s|\\t)*#\\*.*$", ".*\\*#(\\s|\\t)*$", false, true),
    DOUBLETILDE_STYLE("~~", "~~ ", "~~EOL", null, "~~.*$", "~~.*$", false, false),
    DYNASCRIPT_STYLE("<%--EOL", "    ", "EOL--%>", null, "(\\s|\\t)*<%--.*$", ".*--%>(\\s|\\t)*$", true, true),
    DYNASCRIPT3_STYLE("<!---EOL", "    ", "EOL--->", null, "(\\s|\\t)*<!---.*$", ".*--->(\\s|\\t)*$", true, true),
    PERCENT3_STYLE("%%%", "%%% ", "%%%EOL", null, "%%%.*$", "%%%.*$", false, false),
    EXCLAMATION3_STYLE("!!!", "!!! ", "!!!EOL", null, "!!!.*$", "!!!.*$", false, false),

    DOUBLESLASH_STYLE("//", "// ", "//EOL", null, "//.*$", "//.*$", false, false),
    // non generic
    PHP("/*", " * ", " */", "^<\\?php.*$", "(\\s|\\t)*/\\*.*$", ".*\\*/(\\s|\\t)*$", false, true),
    ASP("<%", "    ", "%>", null, "(\\s|\\t)*<%[^@].*$", ".*%>(\\s|\\t)*$", true, true),
    LUA("--[[EOL", "    ", "EOL]]", null, "--\\[\\[$", "\\]\\]$", true, true),
    FTL("<#--EOL", "    ", "EOL-->", null, "(\\s|\\t)*<#--.*$", ".*-->(\\s|\\t)*$", true, true),
    FTL_ALT("[#--EOL", "    ", "EOL--]", "\\[#ftl(\\s.*)?\\]", "(\\s|\\t)*\\[#--.*$", ".*--\\](\\s|\\t)*$", true, true),
    TEXT("====", "    ", "====EOL", null, "====.*$", "====.*$", true, true),
    BATCH("@REM", "@REM ", "@REMEOL", null, "@REM.*$", "@REM.*$", false, false),
    // unknown
    UNKNOWN("", "", "", null, null, null, false, false);

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

    private HeaderType(String firstLine, String beforeEachLine, String endLine, String skipLinePattern, String firstLineDetectionPattern, String endLineDetectionPattern, boolean allowBlankLines, boolean isMultiline) {
        definition = new HeaderDefinition(this.name().toLowerCase(), firstLine, beforeEachLine, endLine, skipLinePattern, firstLineDetectionPattern, endLineDetectionPattern, allowBlankLines, isMultiline);
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
     *         com.google.code.mojo.license.header.HeaderType#UNKNOWN}.
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
