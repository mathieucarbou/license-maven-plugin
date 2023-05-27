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
package com.mycila.maven.plugin.license.document;

import com.mycila.maven.plugin.license.header.Header;
import com.mycila.maven.plugin.license.header.HeaderDefinition;
import com.mycila.maven.plugin.license.header.HeaderParser;
import com.mycila.maven.plugin.license.header.HeaderType;
import com.mycila.maven.plugin.license.util.FileContent;
import com.mycila.maven.plugin.license.util.FileUtils;

import java.io.File;
import java.io.IOException;

import static com.mycila.maven.plugin.license.util.FileUtils.readFirstLines;
import static com.mycila.maven.plugin.license.util.FileUtils.remove;

/**
 * <b>Date:</b> 16-Feb-2008<br> <b>Author:</b> Mathieu Carbou (mathieu.carbou@gmail.com)
 */
public final class Document {
  private final File file;
  private final HeaderDefinition headerDefinition;
  private final String encoding;
  private final String[] keywords;
  private final DocumentPropertiesLoader documentPropertiesLoader;
  private final PropertyPlaceholderResolver placeholderResolver = new PropertyPlaceholderResolver();
  private HeaderParser parser;


  /**
   * Instantiates a new document.
   *
   * @param file the file
   * @param headerDefinition the header definition
   * @param encoding the encoding
   * @param keywords the keywords
   * @param documentPropertiesLoader the document properties loader
   */
  public Document(File file, HeaderDefinition headerDefinition, String encoding, String[] keywords, DocumentPropertiesLoader documentPropertiesLoader) {
    this.keywords = keywords.clone();
    this.file = file;
    this.headerDefinition = headerDefinition;
    this.encoding = encoding;
    this.documentPropertiesLoader = documentPropertiesLoader;
  }

  /**
   * Gets the header definition.
   *
   * @return the header definition
   */
  public HeaderDefinition getHeaderDefinition() {
    return headerDefinition;
  }

  /**
   * Gets the file.
   *
   * @return the file
   */
  public File getFile() {
    return file;
  }

  /**
   * Gets the file path.
   *
   * @return the file path
   */
  public String getFilePath() {
    return getFile().getPath().replace('\\', '/');
  }

  /**
   * Gets the encoding.
   *
   * @return the encoding
   */
  public String getEncoding() {
    return encoding;
  }

  /**
   * Checks if is not supported.
   *
   * @return true, if is not supported
   */
  public boolean isNotSupported() {
    return headerDefinition == null || HeaderType.UNKNOWN.getDefinition().getType().equals(headerDefinition.getType());
  }

  /**
   * Checks for header.
   *
   * @param header the header
   * @param strictCheck the strict check
   * @return true, if successful
   */
  public boolean hasHeader(Header header, boolean strictCheck) {
    if (!strictCheck) {
      try {
        String fileHeader = readFirstLines(file, header.getLineCount() + 10, encoding);
        String fileHeaderOneLine = remove(fileHeader, headerDefinition.getFirstLine().trim(), headerDefinition.getEndLine().trim(), headerDefinition.getBeforeEachLine().trim(), "\n", "\r", "\t", " ");
        String headerOnOnelIne = mergeProperties(header.asOneLineString());
        return fileHeaderOneLine.contains(remove(headerOnOnelIne, headerDefinition.getFirstLine().trim(), headerDefinition.getEndLine().trim(), headerDefinition.getBeforeEachLine().trim()));
      } catch (IOException e) {
        throw new IllegalStateException("Cannot read file " + getFilePath() + ". Cause: " + e.getMessage(), e);
      }
    }
    try {
      return header.isMatchForText(this, headerDefinition, true, encoding);
    } catch (IOException e) {
      throw new IllegalStateException("Cannot read file " + getFilePath() + ". Cause: " + e.getMessage(), e);
    }
  }

  /**
   * Update header.
   *
   * @param header the header
   */
  public void updateHeader(Header header) {
    String headerStr = header.applyDefinitionAndSections(parser.getHeaderDefinition(), parser.getFileContent().isUnix());
    parser.getFileContent().insert(parser.getBeginPosition(), mergeProperties(headerStr));
  }

  /**
   * Merge properties.
   *
   * @param str the str
   * @return the string
   */
  public String mergeProperties(String str) {
    return placeholderResolver.replacePlaceholders(str, documentPropertiesLoader.load(this));
  }

  /**
   * Save.
   */
  public void save() {
    saveTo(file);
  }

  /**
   * Save to.
   *
   * @param dest the dest
   */
  public void saveTo(File dest) {
    if (parser != null) {
      try {
        FileUtils.write(dest, parser.getFileContent().getContent(), encoding);
      } catch (IOException e) {
        throw new IllegalStateException("Cannot write new header in file " + getFilePath() + ". Cause: " + e.getMessage(), e);
      }
    }
  }

  /**
   * Gets the content.
   *
   * @return the content
   */
  public String getContent() {
    return parser == null ? "" : parser.getFileContent().getContent();
  }

  /**
   * Removes the header.
   */
  public void removeHeader() {
    if (headerDetected()) {
      parser.getFileContent().delete(parser.getBeginPosition(), parser.getEndPosition());
    }
  }

  /**
   * Checks if is.
   *
   * @param header the header
   * @return true, if successful
   */
  public boolean is(Header header) {
    try {
      return header.getLocation().isFromUrl(this.file.toURI().toURL());
    } catch (Exception e) {
      throw new IllegalStateException("Error comparing document " + this.file + " with file " + file + ". Cause: " + e.getMessage(), e);
    }
  }

  /**
   * Parses the header.
   */
  public void parseHeader() {
    if (parser == null) {
      parser = new HeaderParser(new FileContent(file, encoding), headerDefinition, keywords);
    }
  }

  /**
   * Header detected.
   *
   * @return true, if successful
   */
  public boolean headerDetected() {
    return parser.gotAnyHeader();
  }

  @Override
  public String toString() {
    return "Document " + getFilePath();
  }
}
