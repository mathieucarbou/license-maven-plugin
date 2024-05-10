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

import org.apache.commons.io.IOUtils;
import org.codehaus.plexus.util.InterpolationFilterReader;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.net.URL;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
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
    // Do not allow instantiation
  }

  @SuppressWarnings("resource")
  public static void write(File file, String content, Charset encoding) throws IOException {
    try (FileChannel channel = new FileOutputStream(file).getChannel()) {
      channel.write(ByteBuffer.wrap(content.getBytes(encoding)));
    }
  }

  private static Reader urlToReader(URL url, Charset encoding) throws IOException {
    return new BufferedReader(new InputStreamReader(url.openStream(), encoding));
  }

  public static String read(URL location, Charset encoding, Map<String, Object> properties) throws IOException, URISyntaxException {
    try (Reader reader = new InterpolationFilterReader(urlToReader(location, encoding), properties)) {
      return IOUtils.toString(reader);
    }
  }

  public static String read(URL location, Charset encoding) throws IOException, URISyntaxException {
    try (Reader reader = urlToReader(location, encoding)) {
      return IOUtils.toString(reader);
    }
  }

  public static String[] read(final URL[] locations, final Charset encoding) throws IOException, URISyntaxException {
    final String[] results = new String[locations.length];
    for (int i = 0; i < locations.length; i++) {
      results[i] = read(locations[i], encoding);
    }
    return results;
  }

  @SuppressWarnings("resource")
  public static String read(File file, Charset encoding) throws IOException {
    try (FileChannel in = new FileInputStream(file).getChannel()) {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      in.transferTo(0, in.size(), Channels.newChannel(baos));
      return baos.toString(encoding.name());
    }
  }

  public static String readFirstLines(File file, int lineCount, Charset encoding) throws IOException {
    try (BufferedReader reader = Files.newBufferedReader(file.toPath(), encoding)) {
      String line;
      StringBuilder sb = new StringBuilder();
      while (lineCount > 0 && (line = reader.readLine()) != null) {
        lineCount--;
        sb.append(line).append("\n");
      }
      return sb.toString();
    }
  }

  public static String remove(String str, String... chars) {
    for (String s : chars) {
      str = str.replace(s, "");
    }
    return str;
  }

  @SuppressWarnings("resource")
  public static void copyFileToFolder(File file, File folder) throws IOException {
    File dest = new File(folder, file.getName());
    try (FileChannel inChannel = new FileInputStream(file).getChannel();
         FileChannel outChannel = new FileOutputStream(dest).getChannel()) {
      inChannel.transferTo(0, inChannel.size(), outChannel);
    }
  }

  public static Path asPath(final File file) {
    if (file == null) {
      return null;
    }

    return file.toPath();
  }

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
