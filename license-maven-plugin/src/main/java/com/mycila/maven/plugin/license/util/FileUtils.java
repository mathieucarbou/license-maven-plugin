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

import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.InterpolationFilterReader;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Stream;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * <b>Date:</b> 16-Feb-2008<br>
 * <b>Author:</b> Mathieu Carbou (mathieu.carbou@gmail.com)
 */
public final class FileUtils {

  private FileUtils() {
  }

  /**
   * Write.
   *
   * @param file the file
   * @param content the content
   * @param encoding the encoding
   * @throws IOException Signals that an I/O exception has occurred.
   */
  @SuppressWarnings("resource")
  public static void write(File file, String content, String encoding) throws IOException {
    try (FileChannel channel = new FileOutputStream(file).getChannel()) {
      channel.write(ByteBuffer.wrap(content.getBytes(encoding)));
    }
  }

  /**
   * Read.
   *
   * @param location the location
   * @param encoding the encoding
   * @param properties the properties
   * @return the string
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static String read(URL location, String encoding, Map<String, Object> properties) throws IOException {
    try (Reader reader = new InterpolationFilterReader(new BufferedReader(new InputStreamReader(location.openStream(), encoding)), properties)) {
      return IOUtil.toString(reader);
    }
  }

  /**
   * Read.
   *
   * @param location the location
   * @param encoding the encoding
   * @return the string
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static String read(URL location, String encoding) throws IOException {
    try (Reader reader = new BufferedReader(new InputStreamReader(location.openStream(), encoding))) {
      return IOUtil.toString(reader);
    }
  }

  /**
   * Read.
   *
   * @param locations the locations
   * @param encoding the encoding
   * @return the string[]
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static String[] read(final URL[] locations, final String encoding) throws IOException {
    final String[] results = new String[locations.length];
    for (int i = 0; i < locations.length; i++) {
      try (Reader reader = new BufferedReader(new InputStreamReader(locations[i].openStream(), encoding))) {
        results[i] = IOUtil.toString(reader);
      }
    }
    return results;
  }

  /**
   * Read.
   *
   * @param file the file
   * @param encoding the encoding
   * @return the string
   * @throws IOException Signals that an I/O exception has occurred.
   */
  @SuppressWarnings("resource")
  public static String read(File file, String encoding) throws IOException {
    try (FileChannel in = new FileInputStream(file).getChannel()) {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      in.transferTo(0, in.size(), Channels.newChannel(baos));
      return baos.toString(encoding);
    }
  }

  /**
   * Read first lines.
   *
   * @param file the file
   * @param lineCount the line count
   * @param encoding the encoding
   * @return the string
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static String readFirstLines(File file, int lineCount, String encoding) throws IOException {
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding))) {
      String line;
      StringBuilder sb = new StringBuilder();
      while (lineCount > 0 && (line = reader.readLine()) != null) {
        lineCount--;
        sb.append(line).append("\n");
      }
      return sb.toString();
    }
  }

  /**
   * Removes the.
   *
   * @param str the str
   * @param chars the chars
   * @return the string
   */
  public static String remove(String str, String... chars) {
    for (String s : chars) {
      str = str.replace(s, "");
    }
    return str;
  }

  /**
   * Copy file to folder.
   *
   * @param file the file
   * @param folder the folder
   * @throws IOException Signals that an I/O exception has occurred.
   */
  @SuppressWarnings("resource")
  public static void copyFileToFolder(File file, File folder) throws IOException {
    File dest = new File(folder, file.getName());
    try (FileChannel inChannel = new FileInputStream(file).getChannel();
         FileChannel outChannel = new FileOutputStream(dest).getChannel()) {
      inChannel.transferTo(0, inChannel.size(), outChannel);
    }
  }

  /**
   * As path.
   *
   * @param file the file
   * @return the path
   */
  public static Path asPath(final File file) {
    if (file == null) {
      return null;
    }

    return file.toPath();
  }

  /**
   * Copy files to folder.
   *
   * @param src the src
   * @param dst the dst
   */
  @SuppressWarnings({"ConstantConditions", "ResultOfMethodCallIgnored"})
  public static void copyFilesToFolder(File src, File dst) {
    dst.mkdirs();
    Stream.of(src.listFiles()).filter(File::isFile).forEach(file -> {
      try {
        Files.copy(file.toPath(), dst.toPath().resolve(file.getName()), REPLACE_EXISTING);
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
    });
  }
}
