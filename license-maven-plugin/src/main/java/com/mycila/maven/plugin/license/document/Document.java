/*
 * Copyright (C) 2008-2021 Mycila (mathieu.carbou@gmail.com)
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
package com.mycila.maven.plugin.license.document;

import com.mycila.maven.plugin.license.header.Header;
import com.mycila.maven.plugin.license.header.HeaderDefinition;
import com.mycila.maven.plugin.license.header.HeaderParser;
import com.mycila.maven.plugin.license.header.HeaderType;
import com.mycila.maven.plugin.license.util.FileContent;
import com.mycila.maven.plugin.license.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.mycila.maven.plugin.license.util.FileUtils.readFirstLines;
import static com.mycila.maven.plugin.license.util.FileUtils.remove;

/**
 * <b>Date:</b> 16-Feb-2008<br> <b>Author:</b> Mathieu Carbou (mathieu.carbou@gmail.com)
 */
public final class Document {
  private static final Pattern YEAR = Pattern.compile("\\\\d{4}");
  
  private final File file;
  private final HeaderDefinition headerDefinition;
  private final String encoding;
  private final String[] keywords;
  private final DocumentPropertiesLoader documentPropertiesLoader;
  private final PropertyPlaceholderResolver placeholderResolver = new PropertyPlaceholderResolver();
  private HeaderParser parser;


  public Document(File file, HeaderDefinition headerDefinition, String encoding, String[] keywords, DocumentPropertiesLoader documentPropertiesLoader) {
    this.keywords = keywords.clone();
    this.file = file;
    this.headerDefinition = headerDefinition;
    this.encoding = encoding;
    this.documentPropertiesLoader = documentPropertiesLoader;
  }

  public HeaderDefinition getHeaderDefinition() {
    return headerDefinition;
  }

  public File getFile() {
    return file;
  }

  public String getFilePath() {
    return getFile().getPath().replace('\\', '/');
  }

  public String getEncoding() {
    return encoding;
  }

  public boolean isNotSupported() {
    return headerDefinition == null || HeaderType.UNKNOWN.getDefinition().getType().equals(headerDefinition.getType());
  }

  public boolean hasHeader(Header header, boolean strictCheck) {
    return hasHeader(header, strictCheck, false);
  }
  
  public boolean hasHeader(Header header, boolean strictCheck, boolean skipExistingCopyrightFirstYear) {
    if (!strictCheck) {
      try {
        String fileHeader = readFirstLines(file, header.getLineCount() + 10, encoding);
        String fileHeaderOneLine = remove(fileHeader, headerDefinition.getFirstLine().trim(), headerDefinition.getEndLine().trim(), headerDefinition.getBeforeEachLine().trim(), "\n", "\r", "\t", " ");
        String headerOnOnelIne = mergeProperties(header.asOneLineString());
        if (skipExistingCopyrightFirstYear) {
          String existingCopyrightFirstYear = getCopyrightFirstYear(fileHeaderOneLine);
          if (existingCopyrightFirstYear != null) {
            headerOnOnelIne = replaceCopyrightFirstYear(headerOnOnelIne, existingCopyrightFirstYear);
          }
        }
        return fileHeaderOneLine.contains(remove(headerOnOnelIne, headerDefinition.getFirstLine().trim(), headerDefinition.getEndLine().trim(), headerDefinition.getBeforeEachLine().trim()));
      } catch (IOException e) {
        throw new IllegalStateException("Cannot read file " + getFilePath() + ". Cause: " + e.getMessage(), e);
      }
    }
    try {
      if (skipExistingCopyrightFirstYear) {
        return header.isMatchForTextKeepingFirstYear(this, headerDefinition, true, encoding);        
      }
      return header.isMatchForText(this, headerDefinition, true, encoding);
    } catch (IOException e) {
      throw new IllegalStateException("Cannot read file " + getFilePath() + ". Cause: " + e.getMessage(), e);
    }
  }

  public static String getCopyrightFirstYear(String header) {
    int index = header.toLowerCase().indexOf("copyright");
    if (index >= 0) {
      Matcher m = YEAR.matcher(header);
      if (m.find(index) && m.start() < index + 20) {        
        return m.group();
      }
    }
    return null;
  }

  public static String replaceCopyrightFirstYear(String header, String existingYear) {
    int index = header.toLowerCase().indexOf("copyright");
    if (index >= 0) {
      Matcher m = YEAR.matcher(header);
      if (m.find(index) && m.start() < index + 20) {      
        return header.substring(0, m.start()) + existingYear + header.substring(m.end());
      }
    }
    return header;
  }

  public void updateHeader(Header header) {
    String headerStr = header.applyDefinitionAndSections(parser.getHeaderDefinition(), parser.getFileContent().isUnix());
    parser.getFileContent().insert(parser.getBeginPosition(), mergeProperties(headerStr));
  }

  public String mergeProperties(String str) {
    return placeholderResolver.replacePlaceholders(str, documentPropertiesLoader.load(this));
  }

  public void save() {
    saveTo(file);
  }

  public void saveTo(File dest) {
    if (parser != null) {
      try {
        FileUtils.write(dest, parser.getFileContent().getContent(), encoding);
      } catch (IOException e) {
        throw new IllegalStateException("Cannot write new header in file " + getFilePath() + ". Cause: " + e.getMessage(), e);
      }
    }
  }

  public String getContent() {
    return parser == null ? "" : parser.getFileContent().getContent();
  }

  public void removeHeader() {
    if (headerDetected()) {
      parser.getFileContent().delete(parser.getBeginPosition(), parser.getEndPosition());
    }
  }

  public boolean is(Header header) {
    try {
      return header.getLocation().isFromUrl(this.file.toURI().toURL());
    } catch (Exception e) {
      throw new IllegalStateException("Error comparing document " + this.file + " with file " + file + ". Cause: " + e.getMessage(), e);
    }
  }

  public void parseHeader() {
    if (parser == null) {
      parser = new HeaderParser(new FileContent(file, encoding), headerDefinition, keywords);
    }
  }

  public boolean headerDetected() {
    return parser.gotAnyHeader();
  }

  @Override
  public String toString() {
    return "Document " + getFilePath();
  }
}
