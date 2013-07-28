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

import java.util.regex.Pattern;

/**
 * The <code>HeaderDefinition</code> class defines what is needed to output a header text into the of the given file
 * type and what is needed to match the first line as well as the last line of a previous header of the given file
 * type.<br/> Optionally you can define the lines you want to skip before outputting the header.
 *
 * @author Cedric Pronzato
 */
public final class HeaderDefinition {
    private final String type;
    private String firstLine;
    private String beforeEachLine;
    private String endLine;
    private Boolean allowBlankLines;

    private Pattern skipLinePattern;
    private Pattern firstLineDetectionPattern;
    private Pattern lastLineDetectionPattern;
    private Boolean isMultiline;

    /**
     * Constructs a new <code>HeaderDefinition</code> object with every header definition properties.
     *
     * @param type                      The type name for this header definition.
     * @param firstLine                 The string to output before the content of the first line of this header.
     * @param beforeEachLine            The string to output before the content of each line of this header (except
     *                                  firstLine and endLine).
     * @param endLine                   The string to output before the content of the last line of this header.
     * @param skipLinePattern           The pattern of lines to skip before being allowed to output this header or null
     *                                  if it can be outputted from the line of the file.
     * @param firstLineDetectionPattern The pattern to detect the first line of a previous header.
     * @param lastLineDetectionPattern  The pattern to detect the last line of a previous header.
     * @throws IllegalArgumentException If the type name is null.
     */
    public HeaderDefinition(String type, String firstLine, String beforeEachLine, String endLine, String skipLinePattern, String firstLineDetectionPattern, String lastLineDetectionPattern, boolean allowBlankLines, boolean isMultiline) {
        this(type);
        this.firstLine = firstLine;
        this.beforeEachLine = beforeEachLine;
        this.endLine = endLine;
        this.skipLinePattern = compile(skipLinePattern);
        this.firstLineDetectionPattern = compile(firstLineDetectionPattern);
        this.lastLineDetectionPattern = compile(lastLineDetectionPattern);
        this.allowBlankLines = allowBlankLines;
        this.isMultiline = isMultiline;
        if (!"unknown".equals(type)) validate();
    }

    /**
     * Constructs a new <code>HeaderDefinition</code> with only initializing the header type. You must then set all the
     * other definitions properties manually in order to have a coherent object.
     *
     * @param type The type name for this header definition.
     * @throws IllegalArgumentException If the type name is null.
     * @see #check(String, String)
     * @see #setPropertyFromString(String, String)
     */
    public HeaderDefinition(String type) {
        if (type == null) {
            throw new IllegalArgumentException("The type of a header definition cannot be null");
        }
        this.type = type.toLowerCase();
    }

    private Pattern compile(String regexp) {
        return regexp == null ? null : Pattern.compile(regexp);
    }

    public String getFirstLine() {
        return firstLine;
    }

    public String getBeforeEachLine() {
        return beforeEachLine;
    }

    public String getEndLine() {
        return endLine;
    }

    public String getType() {
        return type;
    }

    public boolean allowBlankLines() {
        return allowBlankLines;
    }

    /**
     * Tells if the given content line must be skipped according to this header definition. The header is outputted
     * after any skipped line if any pattern defined on this point or on the first line if not pattern defined.
     *
     * @param line The line to test.
     * @return true if this line must be skipped or false.
     */
    public boolean isSkipLine(String line) {
        return skipLinePattern != null && line != null && skipLinePattern.matcher(line).matches();
    }

    /**
     * Tells if the given content line is the first line of a possible header of this definition kind.
     *
     * @param line The line to test.
     * @return true if the first line of a header have been recognized or false.
     */
    public boolean isFirstHeaderLine(String line) {
        return firstLineDetectionPattern != null && line != null && firstLineDetectionPattern.matcher(line).matches();
    }

    /**
     * Tells if the given content line is the last line of a possible header of this definition kind.
     *
     * @param line The line to test.
     * @return true if the last line of a header have been recognized or false.
     */
    public boolean isLastHeaderLine(String line) {
        return lastLineDetectionPattern != null && line != null && lastLineDetectionPattern.matcher(line).matches();
    }

    protected Pattern getSkipLinePattern() {
        return skipLinePattern;
    }

    /**
     * Sets header definition properties using its property name and its string value.<br> If you want to set a property
     * to null you must not call this function.<br/> This function is mainly used while parsing properties from the XML
     * configuration file.
     *
     * @param property The property name.
     * @param value    The property value.
     * @throws IllegalArgumentException If the property value is null.
     */
    public void setPropertyFromString(String property, String value) {
        if (isEmpty(value)) {
            throw new IllegalArgumentException("The value cannot be empty for XML tag " + property + " for type " + type);
        }
        if ("firstLine".equalsIgnoreCase(property))
            firstLine = value;
        else if ("allowBlankLines".equalsIgnoreCase(property))
            allowBlankLines = Boolean.valueOf(value);
        else if ("isMultiline".equalsIgnoreCase(property))
            isMultiline = Boolean.valueOf(value);
        else if ("beforeEachLine".equalsIgnoreCase(property))
            beforeEachLine = value;
        else if ("endLine".equalsIgnoreCase(property))
            endLine = value;
        else if ("skipLine".equalsIgnoreCase(property))
            skipLinePattern = compile(value);
        else if ("firstLineDetectionPattern".equalsIgnoreCase(property))
            firstLineDetectionPattern = compile(value);
        else if ("lastLineDetectionPattern".equalsIgnoreCase(property))
            lastLineDetectionPattern = compile(value);

    }

    /**
     * Checks this header definition consistency, in other words if all the mandatory properties of the definition have
     * been set.
     *
     * @throws IllegalStateException If a mandatory property has not been set.
     */
    public void validate() {
        check("firstLine", this.firstLine);
        check("beforeEachLine", this.beforeEachLine);
        check("endLine", this.endLine);
        check("firstLineDetectionPattern", this.firstLineDetectionPattern);
        check("lastLineDetectionPattern", this.lastLineDetectionPattern);
        check("isMultiline", this.isMultiline);
        check("allowBlankLines", this.allowBlankLines);
        // skip line can be null
    }

    private void check(String name, Boolean value) {
        if (value == null) {
            throw new IllegalStateException(String.format("The property '%s' is missing for header definition '%s'", name, type));
        }
    }

    private void check(String name, String value) {
        if (isEmpty(value)) {
            throw new IllegalStateException(String.format("The property '%s' is missing for header definition '%s'", name, type));
        }
    }

    private void check(String name, Pattern value) {
        if (isEmpty(value.pattern())) {
            throw new IllegalStateException(String.format("The property '%s' is missing for header definition '%s'", name, type));
        }
    }

    private boolean isEmpty(String s) {
        return s == null || "".equals(s);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HeaderDefinition that = (HeaderDefinition) o;
        return !(type != null ? !type.equals(that.type) : that.type != null);
    }

    @Override
    public int hashCode() {
        return type != null ? type.hashCode() : 0;
    }

    @Override
    public String toString() {
        return type;
    }

    public boolean isMultiLine() {
        return isMultiline;
    }
}
