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

import java.io.IOException;
import java.net.URL;

import com.mycila.maven.plugin.license.util.FileUtils;
import com.mycila.maven.plugin.license.util.resource.ResourceFinder;

/**
 * Provides an access to the license template text.
 *
 * @author <a href="https://github.com/ppalaga">Peter Palaga</a>
 */
public abstract class HeaderSource {

    /**
     * A {@link HeaderSource} built from a lincense header template literal.
     */
    public static class LiteralHeaderSource extends HeaderSource {
        public LiteralHeaderSource(String content) {
            super(content, true);
        }

        /**
         * @return always {@code false} because this {@link LiteralHeaderSource} was not loaded from any {@link URL}
         */
        @Override
        public boolean isFromUrl(URL location) {
            return false;
        }

        @Override
        public String toString() {
            return "inline: " + content;
        }

    }

    /**
     * A {@link HeaderSource} loaded from a {@link URL}.
     */
    public static class UrlHeaderSource extends HeaderSource {
        private final URL url;

        public UrlHeaderSource(URL url, String encoding) throws IOException {
            super(FileUtils.read(url, encoding), false);
            this.url = url;
        }

        @Override
        public boolean isFromUrl(URL location) {
            return this.url.equals(location);
        }

        @Override
        public String toString() {
            return url + ": " + content;
        }

    }

    public static HeaderSource of(String headerPath, String encoding, ResourceFinder finder) {
        return of(null, encoding, finder);
    }

    /**
     * Checking the params left to right, returns the first available {@link HeaderSource} that can be created. If
     * {@code inlineHeader} is not {@code null} returns a new {@link LiteralHeaderSource}. Otherwise attempts to create a
     * new {@link UrlHeaderSource} out of {@code headerPath} and {@code encoding}.
     *
     * @param inlineHeader the text of a lincense header template
     * @param headerPath a path resolvable by the {@code finder}
     * @param encoding the encoding to use when readinf {@code headerPath}
     * @param finder the {@link ResourceFinder} to use to resolve {@code headerPath}
     * @return a new {@link HeaderSource}
     */
    public static HeaderSource of(String inlineHeader, String headerPath, String encoding, ResourceFinder finder) {
        if (inlineHeader != null && !inlineHeader.isEmpty()) {
            return new LiteralHeaderSource(reformatInlineHeader(inlineHeader));
        } else if (headerPath == null) {
            throw new IllegalArgumentException("Either inlineHeader or header path need to be specified");
        } else {
            try {
                final URL headerUrl = finder.findResource(headerPath);
                return new UrlHeaderSource(headerUrl, encoding);
            } catch (Exception e) {
                throw new IllegalArgumentException(
                        "Cannot read header document " + headerPath + ". Cause: " + e.getMessage(), e);
            }
        }
    }

  /**
   * Remove indent from multi-line inline headers
   */
    private static String reformatInlineHeader(String inlineHeader) {
      
        // Do not reformat single-line headers
        if(!inlineHeader.contains("\n")) {
          return inlineHeader;
        }
      
        // Choose CRLF or LF based on the input text
        String newline;
        if(inlineHeader.contains("\r")) {
          newline = "\r\n";
        } else {
          newline = "\n";
        }
      
        StringBuilder reformatted = new StringBuilder();
        String[] lines = inlineHeader.split("\\r?\\n");

        for (String line : lines) {
            reformatted.append(line.trim()).append(newline);
        }
        
        return reformatted.toString();
    }

    protected final String content;
    private final boolean inline;

    public HeaderSource(String content, boolean inline) {
        super();
        this.content = content;
        this.inline = inline;
    }

    /**
     * @return the text of the license template
     */
    public String getContent() {
        return content;
    }

    /**
     * @return {@code true} if this {@link HeaderSource} was created from a string rather by loading the bits from an
     *         URL; {@code false} otherwise
     */
    public boolean isInline() {
        return inline;
    }

    /**
     * Retuns {@code true} if this {@link HeaderSource} was loaded from the URL given in the {@code location} parameter
     * or {@code false} otherwise.
     *
     * @param location
     *            the URL to tell if this {@link HeaderSource} was loaded from it
     * @return {@code true} if this {@link HeaderSource} was loaded from the URL given in the {@code location} parameter
     *         or {@code false} otherwise
     */
    public abstract boolean isFromUrl(URL location);

}
