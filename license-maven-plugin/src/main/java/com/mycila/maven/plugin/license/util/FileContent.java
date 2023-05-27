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
package com.mycila.maven.plugin.license.util;

import java.io.File;
import java.io.IOException;

import static com.mycila.maven.plugin.license.util.FileUtils.read;

/**
 * The Class FileContent.
 */
public final class FileContent {
  private final File file;
  private final StringBuilder fileContent;
  private final boolean unix;
  private int oldPos;
  private int position;

  /**
   * Instantiates a new file content.
   *
   * @param file the file
   * @param encoding the encoding
   */
  public FileContent(File file, String encoding) {
    try {
      this.file = file;
      this.fileContent = new StringBuilder(read(file, encoding));
      unix = fileContent.indexOf("\r") == -1;
    } catch (IOException e) {
      throw new IllegalArgumentException("Unable to read file " + file + ". Cause: " + e.getMessage(), e);
    }
  }

  /**
   * Reset to.
   *
   * @param pos the pos
   */
  public void resetTo(int pos) {
    oldPos = position;
    position = pos;
  }

  /**
   * Reset.
   */
  public void reset() {
    oldPos = position;
    position = 0;
  }

  /**
   * Rewind.
   */
  public void rewind() {
    position = oldPos;
  }

  /**
   * End reached.
   *
   * @return true, if successful
   */
  public boolean endReached() {
    return position >= fileContent.length();
  }

  /**
   * Next line.
   *
   * @return the string
   */
  public String nextLine() {
    if (endReached()) {
      return null;
    }
    int lf = fileContent.indexOf("\n", position);
    int eol = lf == -1 || lf == 0 ? fileContent.length() : fileContent.charAt(lf - 1) == '\r' ? lf - 1 : lf;
    String str = fileContent.substring(position, eol);
    oldPos = position;
    position = lf == -1 ? fileContent.length() : lf + 1;
    return str;
  }

  /**
   * Gets the position.
   *
   * @return the position
   */
  public int getPosition() {
    return position;
  }

  /**
   * Delete.
   *
   * @param start the start
   * @param end the end
   */
  public void delete(int start, int end) {
    fileContent.delete(start, end);
  }

  /**
   * Insert.
   *
   * @param index the index
   * @param str the str
   */
  public void insert(int index, String str) {
    fileContent.insert(index, str);
  }

  /**
   * Removes the duplicated empty end lines.
   */
  public void removeDuplicatedEmptyEndLines() {
    int pos;
    while ((pos = fileContent.lastIndexOf("\n")) != -1) {
      boolean cr = false;
      if (pos > 0 && fileContent.charAt(pos - 1) == '\r') {
        cr = true;
        pos--;
      }
      if (pos > 0 && fileContent.charAt(pos - 1) == '\n') {
        fileContent.deleteCharAt(pos);
        if (cr) {
          fileContent.deleteCharAt(pos);
        }
      } else {
        break;
      }
    }
    oldPos = position;
    position = fileContent.length();
  }

  /**
   * Gets the content.
   *
   * @return the content
   */
  public String getContent() {
    return fileContent.toString();
  }

  /**
   * Checks if is unix.
   *
   * @return true, if is unix
   */
  public boolean isUnix() {
    return unix;
  }

  @Override
  public String toString() {
    return file.toString();
  }

}
