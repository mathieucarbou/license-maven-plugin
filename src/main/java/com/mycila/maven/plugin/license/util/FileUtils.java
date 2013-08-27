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
package com.mycila.maven.plugin.license.util;

import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.InterpolationFilterReader;

import java.io.*;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.util.Map;

/**
 * <b>Date:</b> 16-Feb-2008<br>
 * <b>Author:</b> Mathieu Carbou (mathieu.carbou@gmail.com)
 */
public final class FileUtils {

    private FileUtils() {
    }

    public static void write(File file, String content, String encoding) throws IOException {
        FileChannel channel = new FileOutputStream(file).getChannel();
        try {
            channel.write(ByteBuffer.wrap(content.getBytes(encoding)));
        } finally {
            channel.close();
        }
    }

    public static String read(URL location, String encoding, Map<String, String> properties) throws IOException {
        Reader reader = new InterpolationFilterReader(new BufferedReader(new InputStreamReader(location.openStream(), encoding)), properties);
        try {
            return IOUtil.toString(reader);
        } finally {
            reader.close();
        }
    }

    public static String read(URL location, String encoding) throws IOException {
        Reader reader = new BufferedReader(new InputStreamReader(location.openStream(), encoding));
        try {
            return IOUtil.toString(reader);
        } finally {
            reader.close();
        }
    }

    public static String read(File file, String encoding) throws IOException {
        FileChannel in = new FileInputStream(file).getChannel();
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            in.transferTo(0, in.size(), Channels.newChannel(baos));
            return baos.toString(encoding);
        } finally {
            in.close();
        }
    }

    public static String readFirstLines(File file, int lineCount, String encoding) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));
        try {
            String line;
            StringBuilder sb = new StringBuilder();
            while (lineCount > 0 && (line = reader.readLine()) != null) {
                lineCount--;
                sb.append(line).append("\n");
            }
            return sb.toString();
        } finally {
            reader.close();
        }
    }

    public static String remove(String str, String... chars) {
        for (String s : chars) {
            str = str.replace(s, "");
        }
        return str;
    }

    public static void copyFileToFolder(File file, File folder) throws IOException {
        File dest = new File(folder, file.getName());
        FileChannel inChannel = new FileInputStream(file).getChannel();
        FileChannel outChannel = new FileOutputStream(dest).getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } finally {
            if (inChannel != null) inChannel.close();
            if (outChannel != null) outChannel.close();
        }

    }
}
