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

import com.mycila.maven.plugin.license.HeaderSection;
import com.mycila.maven.plugin.license.util.StringUtils;

import java.net.URL;
import java.util.*;

import static com.mycila.maven.plugin.license.util.FileUtils.read;
import static com.mycila.maven.plugin.license.util.FileUtils.remove;

/**
 * The <code>Header</code> class wraps the license template file, the one which have to be outputted inside the other
 * files.
 *
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
public final class Header {
    private final URL location;
    private final String headerContent;
    private final String headerContentOneLine;
    private String[] lines;
    private final HeaderSection[] sections;

    private static final Map<String, String> EMPTY_PROPERTIES = new HashMap<String, String>(0);

    /**
     * Constructs a <code>Header</code> object pointing to a license template file. In case of the template contains
     * replaceable values (declared as ${<em>valuename</em>}), you can set the map of this values.
     *
     * @param location   The license template file location.
     * @param properties The map of values to replace.
     * @param sections   Any applicable header sections for this header
     * @throws IllegalArgumentException If the header file location is null or if an error occurred while reading the
     *                                  file content.
     */
    public Header(URL location, String encoding, Map<String, String> properties, HeaderSection[] sections) {
        if (location == null) {
            throw new IllegalArgumentException("Cannot read license template header file with a null location");
        }
        if (properties == null) {
            properties = EMPTY_PROPERTIES;
        }
        this.location = location;
        this.sections = sections;
        try {
            this.headerContent = read(location, encoding, properties);
            lines = headerContent.replace("\r", "").split("\n");
            headerContentOneLine = remove(headerContent, " ", "\t", "\r", "\n");
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Cannot read header document " + location + ". Cause: " + e.getMessage(), e);
        }
    }

    public String asString() {
        return headerContent;
    }

    public String asOneLineString() {
        return headerContentOneLine;
    }

    public int getLineCount() {
        return lines.length;
    }

    /**
     * Returns the location of license template file.
     *
     * @return The URL location.
     */
    public URL getLocation() {
        return location;
    }

    public String eol(boolean unix) {
        return unix ? "\n" : "\r\n";
    }

    public String buildForDefinition(HeaderDefinition type, boolean unix, String filename) {
        StringBuilder newHeader = new StringBuilder();
        if (notEmpty(type.getFirstLine())) {
            newHeader.append(type.getFirstLine().replace("EOL", eol(unix)));
            newHeader.append(eol(unix));
        }
        for (String line : getLines()) {
            final String str = type.getBeforeEachLine().replace("EOL", eol(unix)) + line;
            newHeader.append(StringUtils.rtrim(str));
            newHeader.append(eol(unix));
        }
        if (notEmpty(type.getEndLine())) {
            newHeader.append(type.getEndLine().replace("EOL", eol(unix)));
            newHeader.append(eol(unix));
        }
        return newHeader.toString().replace("${file_name}", filename);
    }

    @Override
    public String toString() {
        return asString();
    }

    public String[] getLines() {
        return lines;
    }

    /**
     * Determines if a potential file header (typically, the start of the file
     * plus some buffer space) matches this header, as rendered with the
     * specified {@link HeaderDefinition} and line-ending.
     * 
     * @param potentialFileHeader
     *            the potential file header, usually with some extra buffer
     *            lines
     * @param headerDefinition
     *            the header definition to render the header with
     * @param unix
     *            if true, unix line-endings will be used
     * @return true if the header is matched
     */
    public boolean isMatchForText(String potentialFileHeader, HeaderDefinition headerDefinition, boolean unix, String filename) {

        String expected = buildForDefinition(headerDefinition, unix, filename);

        SortedMap<Integer, HeaderSection> sectionsByIndex = computeSectionsByIndex(expected);

        if (sectionsByIndex.isEmpty())
            return potentialFileHeader.contains(expected);

        List<String> textBetweenSections = buildExpectedTextBetweenSections(expected, sectionsByIndex);
        List<HeaderSection> sectionsInOrder = new ArrayList<HeaderSection>(sectionsByIndex.values());
        return recursivelyFindMatch(potentialFileHeader, headerDefinition, textBetweenSections, sectionsInOrder, 0, 0);
    }
    
    public String applyDefinitionAndSections(HeaderDefinition headerDefinition, boolean unix, String filename) {
        
        String expected = buildForDefinition(headerDefinition, unix, filename);

        SortedMap<Integer, HeaderSection> sectionsByIndex = computeSectionsByIndex(expected);

        if (sectionsByIndex.isEmpty())
            return expected;

        List<String> textBetweenSections = buildExpectedTextBetweenSections(expected, sectionsByIndex);
        List<HeaderSection> sectionsInOrder = new ArrayList<HeaderSection>(sectionsByIndex.values());
        
        StringBuilder b = new StringBuilder();
        for( int i=0; i<textBetweenSections.size();++i) {
            String textBetween = textBetweenSections.get(i);
            b.append(textBetween);
            if( i < sectionsInOrder.size() ) {
                HeaderSection section = sectionsInOrder.get(i);
                String sectionValue = section.getDefaultValue();
                if( notEmpty(sectionValue)) {
                    String[] tokens = sectionValue.split(eol(unix));
                    for( int j=0;j<tokens.length; j++) {
                        if (j > 0) {
                            b.append(eol(unix));
                            if (notEmpty(headerDefinition.getBeforeEachLine()))
                                b.append(headerDefinition.getBeforeEachLine());
                        }
                        b.append(tokens[j]);
                    }
                }
            }
        }
        return b.toString();
    }

    private boolean notEmpty(String str) {
        return str != null && str.length() > 0;
    }

    /**
     * If this Header has any {@link HeaderSection} sections defined, we look
     * for each header key in the expected header text and note the position
     * index of the match.
     * 
     * @param expectedHeaderText
     *            the expected header text
     * @return a sorted-map of matching HeaderSections, with the key being the
     *         index of section in the header text
     */
    private SortedMap<Integer, HeaderSection> computeSectionsByIndex(String expectedHeaderText) {

        SortedMap<Integer, HeaderSection> sectionsByIndex = new TreeMap<Integer, HeaderSection>();

        if (sections == null)
            return sectionsByIndex;

        for (HeaderSection section : sections) {

            String key = section.getKey();
            int index = expectedHeaderText.indexOf(key);
            if (index == -1) {
                // TODO: we need some way to log that a header section key was
                // not found...
                continue;
            }

            /**
             * Verify that the new section doesn't overlap with an existing
             * section
             */
            int indexEnd = index + section.getKey().length();

            for (Map.Entry<Integer, HeaderSection> entry : sectionsByIndex.entrySet()) {

                int existingIndexStart = entry.getKey();
                HeaderSection existingSection = entry.getValue();
                int existingIndexEnd = existingIndexStart + existingSection.getKey().length();

                if (existingIndexStart < indexEnd && index < existingIndexEnd)
                    throw new IllegalArgumentException(String.format(
                            "Existing section '%1$s' overlaps with new section '%2$s'", existingSection.getKey(),
                            section.getKey()));

                sectionsByIndex.put(index, section);
            }

            sectionsByIndex.put(index, section);
        }

        return sectionsByIndex;
    }

    /**
     * Once we have found the set of header sections indexed in the expected
     * header text, we extract out the remaining header text occurring
     * in-between those header sections and return an ordered list of the
     * segments.
     * 
     * As an example, if out text looked like:
     * 
     * "My name is NAME_SECTION and I work for COMPANY_SECTION most days."
     * 
     * where "NAME_SECTION" and "COMPANY_SECTION" are matched sections, the
     * resulting list should look like:
     * 
     * ["My name is ", " and I work for ", " most days."]
     * 
     * @param expectedHeaderText
     *            the expected header text
     * @param sectionsByIndex
     *            a sorted-map of matching HeaderSections, with the key being
     *            the index of section in the header text
     * @return an ordered list of the text segments occurring in-between the
     *         sections
     */
    private List<String> buildExpectedTextBetweenSections(String expectedHeaderText,
            SortedMap<Integer, HeaderSection> sectionsByIndex) {

        List<String> textBetweenSections = new ArrayList<String>();
        int currentIndex = 0;

        for (Map.Entry<Integer, HeaderSection> entry : sectionsByIndex.entrySet()) {
            int index = entry.getKey();
            HeaderSection section = entry.getValue();
            String textBetween = expectedHeaderText.substring(currentIndex, index);
            textBetweenSections.add(textBetween);
            currentIndex = index + section.getKey().length();
        }

        /**
         * Add the tail of the expected text
         */
        String textBetween = expectedHeaderText.substring(currentIndex, expectedHeaderText.length());
        textBetweenSections.add(textBetween);

        return textBetweenSections;
    }

    /**
     * Given a potential file header and our expected segmented header text,
     * this method recursively searches through the expected segments, looking
     * for possible matches.
     * 
     * We recursively search through the potential header for each of the
     * expected text section, advancing our current text segment index and our
     * index into the potential header text. Each step of the recursion
     * considers all possible matches for a text segment, such that the
     * recursion tree will eventually consider ALL valid matches. This can be
     * useful when the user specifies a header like:
     * 
     * "Copyright YEAR NAME - License"
     * 
     * where "YEAR" and "NAME" are sections, meaning that we have to match a " "
     * in-between, which can potentially match in multiple places if the actual
     * values in the potential header contain spaces.
     * 
     * @param potentialFileHeader
     *            the potential file header
     * @param headerDefinition
     *            the header definition
     * @param expectedTextBetweenSections
     *            the expected text between sections
     * @param sectionsInOrder
     *            the sections interleaved with the expected text
     * @param currentTextSegmentIndex
     *            the index of the current expected text segment to search for
     * @param currentPotentialFileHeaderIndex
     *            the current search index into the potentialFileHeader
     * @return true if a valid match is found
     */
    private boolean recursivelyFindMatch(String potentialFileHeader, HeaderDefinition headerDefinition,
            List<String> expectedTextBetweenSections, List<HeaderSection> sectionsInOrder, int currentTextSegmentIndex,
            int currentPotentialFileHeaderIndex) {

        if (currentTextSegmentIndex == expectedTextBetweenSections.size())
            return true;

        int currentSearchFromIndex = currentPotentialFileHeaderIndex;

        while (true) {
            String expectedText = expectedTextBetweenSections.get(currentTextSegmentIndex);
            int index = potentialFileHeader.indexOf(expectedText, currentSearchFromIndex);
            if (index == -1)
                return false;

            if (currentTextSegmentIndex > 0) {
                HeaderSection section = sectionsInOrder.get(currentTextSegmentIndex - 1);
                String sectionValue = potentialFileHeader.substring(currentPotentialFileHeaderIndex, index);
                if (!ensureSectionMatch(headerDefinition, section, sectionValue))
                    return false;
            }

            if (recursivelyFindMatch(potentialFileHeader, headerDefinition, expectedTextBetweenSections,
                    sectionsInOrder, currentTextSegmentIndex + 1, index + expectedText.length()))
                return true;

            currentSearchFromIndex = index + 1;
        }
    }

    /**
     * If a header section has specified an "ensureMatch" value (see
     * {@link HeaderSection#getEnsureMatch()}), then we verify that the contents
     * of the section in the detected header do indeed match.
     * 
     * @param headerDefinition
     *            the header definition for the current header match
     * @param section
     *            the header section
     * @param sectionValue
     *            the detected value of the section in the source file header
     * @return false if the detected section value failed the match
     */
    private boolean ensureSectionMatch(HeaderDefinition headerDefinition, HeaderSection section, String sectionValue) {

        String match = section.getEnsureMatch();
        if (!notEmpty(match))
            return true;

        String[] lines = sectionValue.split("\n");

        /**
         * We need to clean off any header-specific line-start characters before
         * we perform the match
         */
        String before = headerDefinition.getBeforeEachLine();
        if (notEmpty(before)) {
            for (int i = 0; i < lines.length; ++i) {
                String line = lines[i];
                if (line.startsWith(before))
                    lines[i] = line.substring(before.length());
            }
        }

        /**
         * If a multi-line match has been specified, we reconstruct the
         * multi-line string (now sans line-start characters) and perform the
         * match on the result
         */
        if (section.isMultiLineMatch()) {
            StringBuilder b = new StringBuilder();
            for (int i = 0; i < lines.length; ++i) {
                if (i > 0)
                    b.append('\n');
                b.append(lines[i]);
            }
            String multiLineValue = b.toString();
            return multiLineValue.matches(match);
        }

        for (String line : lines) {
            if (!line.matches(match))
                return false;
        }

        return true;
    }
}
