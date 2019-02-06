/*
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

import com.mycila.maven.plugin.license.util.FileContent;
import com.mycila.maven.plugin.license.util.StringUtils;

/**
 * The <code>HeaderParser</code> class is used to get header information about the current header defined in the given
 * file. The achieve this it will use the <code>HeaderDefinition</code> associated to the type of the given file.
 *
 * Important: is considered a license header a header which contains the word <em>copyright</em> (case insensitive)
 * within a section of the file which match the given <code>HeaderDefinition</code> associated to this
 * <code>HeaderParser</code>.
 *
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 * @see com.mycila.maven.plugin.license.header.HeaderDefinition
 */
public final class HeaderParser {

    private final int beginPosition;
    private final int endPosition;
    private final boolean existingHeader;
    private final FileContent fileContent;
    private final String[] keywords;
    private HeaderDefinition headerDefinition;

    private String line;

    /**
     * Creates a <code>HeaderParser</code> object linked to the given file content and the associated header definition
     * based on the file type.
     *
     * @param fileContent      The file content.
     * @param headerDefinition The associated header definition to use.
     * @throws IllegalArgumentException If the file content is null or if the header definition is null.
     */
    public HeaderParser(FileContent fileContent, HeaderDefinition headerDefinition, String[] keywords) {
        if (fileContent == null) {
            throw new IllegalArgumentException("Cannot create a header parser for null file content");
        }
        if (headerDefinition == null) {
            throw new IllegalArgumentException("Cannot work on file header if the header definition is null");
        }
        this.keywords = keywords.clone();
        this.headerDefinition = headerDefinition;
        this.fileContent = fileContent;
        beginPosition = findBeginPosition();
        existingHeader = hasHeader();
        endPosition = existingHeader ? findEndPosition() : -1;
    }

    /**
     * Returns the index position in the content where the header effectively starts.
     *
     * @return The index in the content.
     */
    public int getBeginPosition() {
        return beginPosition;
    }

    /**
     * Returns the index position in the content where the header effectively ends.
     *
     * @return The index in the content.
     */
    public int getEndPosition() {
        return endPosition;
    }

    /**
     * Tells if the given file already contains a license header.
     *
     * @return true if a license header has been detect or false.
     */
    public boolean gotAnyHeader() {
        return existingHeader;
    }

    /**
     * Returns the file content.
     *
     * @return The content.
     */
    public FileContent getFileContent() {
        return fileContent;
    }

    /**
     * Returns the header definition associated to this header parser (itself bounded to a file).
     *
     * @return The associated header definition.
     */
    public HeaderDefinition getHeaderDefinition() {
        return headerDefinition;
    }

    private int findBeginPosition() {
        int beginPos = 0;
        line = fileContent.nextLine();
        if (headerDefinition.getSkipLinePattern() == null) {
            return beginPos;
        }

        // the format expect to find lines to be skipped
        while (line != null && !headerDefinition.isSkipLine(line)) {
            beginPos = fileContent.getPosition();
            line = fileContent.nextLine();
        }
        
        // at least we have found the line to skip or we are the end of the file
        // this time we are going to skip next lines if they match the skip pattern
        while (line != null && headerDefinition.isSkipLine(line)) {
            beginPos = fileContent.getPosition();
            line = fileContent.nextLine();
        }
        
        if (line == null) {
            // After skipping everything we are at the end of the file
            // Header has to be at the file beginning
            beginPos = 0;
            fileContent.reset();
            line = fileContent.nextLine();
        }
        
        return beginPos;
    }

    private boolean hasHeader() {
        // skip blank lines
        while (line != null && "".equals(line.trim()))
            line = fileContent.nextLine();
        // check if there is already a header
        boolean gotHeader = false;
        if (headerDefinition.isFirstHeaderLine(line)) {
            // skip blank lines before header text
            if (headerDefinition.allowBlankLines()) {
                do line = fileContent.nextLine();
                while (line != null && "".equals(line.trim()));
            }
            
            // first header detected line & potential blank lines have been detected
            // following lines should be header lines
            if (line == null) {
                // we detected previously a one line comment block that matches the header detection
                // it is not an header it is a comment
                return false;
            }
            
            StringBuilder inPlaceHeader = new StringBuilder();
            String before = StringUtils.rtrim(headerDefinition.getBeforeEachLine());
            if ("".equals(before) && !headerDefinition.isMultiLine())
                before = headerDefinition.getBeforeEachLine();
            boolean foundEnd = false;
            do {
                inPlaceHeader.append(line.toLowerCase());
                line = fileContent.nextLine();
                if (headerDefinition.isMultiLine() && headerDefinition.isLastHeaderLine(line)) {
                    foundEnd = true;
                    break;
                }
            }
            while (line != null && line.startsWith(before));
            // skip blank lines after header text
            if (headerDefinition.allowBlankLines())
                do line = fileContent.nextLine();
                while (line != null && "".equals(line.trim()));
            if (headerDefinition.allowBlankLines() || !foundEnd)
                fileContent.rewind();
            if (!headerDefinition.isMultiLine()) {
                // keep track of the position for headers where the end line is the same as the before each line
                int pos = fileContent.getPosition();
                // check if the line is the end line
                while (line != null
                    && !headerDefinition.isLastHeaderLine(line)
                    && (headerDefinition.allowBlankLines() || !"".equals(line.trim()))
                    && line.startsWith(before))
                    line = fileContent.nextLine();
                if (line == null)
                    fileContent.resetTo(pos);
            }
            gotHeader = true;
            for (String keyword : keywords) {
                if (inPlaceHeader.indexOf(keyword.toLowerCase()) == -1) {
                    gotHeader = false;
                    break;
                }
            }
        }
        return gotHeader;
    }

    private int findEndPosition() {
        // we check if there is a header, if the next line is the blank line of the header
        int end = fileContent.getPosition();
        line = fileContent.nextLine();
        if (line != null && "".equals(line.trim()))
            end = fileContent.getPosition();
        return end;
    }
}
