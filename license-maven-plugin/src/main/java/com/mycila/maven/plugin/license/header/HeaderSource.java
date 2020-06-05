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

import java.io.IOException;
import java.net.URL;

import com.mycila.maven.plugin.license.Multi;
import com.mycila.maven.plugin.license.util.FileUtils;
import com.mycila.maven.plugin.license.util.resource.ResourceFinder;
import org.apache.maven.plugin.MojoFailureException;

import static com.mycila.maven.plugin.license.Multi.DEFAULT_SEPARATOR;

/**
 * Provides an access to the license template text.
 *
 * @author <a href="https://github.com/ppalaga">Peter Palaga</a>
 */
public abstract class HeaderSource {

    /**
     * A {@link HeaderSource} built from a license header template literal.
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

    /**
     * A {@link HeaderSource} built from multiple license header template literals.
     */
    public static class MultiLiteralHeaderSource extends HeaderSource {
        public MultiLiteralHeaderSource(final String preamble, final String[] contents, final String[] separators) {
            super(combineHeaders(preamble, contents, separators), true);
        }

        /**
         * @return always {@code false} because this {@link LiteralHeaderSource} was not loaded from any {@link URL}
         */
        @Override
        public boolean isFromUrl(final URL location) {
            return false;
        }

        @Override
        public String toString() {
            return "inline: " + content;
        }

    }

    /**
     * A {@link HeaderSource} loaded from multiple {@link URL}.
     */
    public static class MultiUrlHeaderSource extends HeaderSource {
        private final URL urls[];

        public MultiUrlHeaderSource(final String preamble, final URL[] urls, final String[] separators, final String encoding) throws IOException {
            super(combineHeaders(preamble, FileUtils.read(urls, encoding), separators), false);
            this.urls = urls;
        }

        @Override
        public boolean isFromUrl(final URL location) {
            for (final URL url : urls) {
                if (url.equals(location)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            builder.append('[');
            for (final URL url : urls) {
                if (builder.length() > 1) {
                    builder.append(", ");
                }
                builder.append(url);
            }
            builder.append("] : ").append(content);
            return builder.toString();
        }

    }

    private static String combineHeaders(final String preamble, final String[] headers, final String[] separators) {
        final StringBuilder builder = new StringBuilder();

        // preamble
        if (preamble != null) {
            builder.append(preamble);
            if (!preamble.endsWith("\n")) {
                builder.append('\n');
            }
            builder.append('\n');
        }

        for (int i = 0; i < headers.length; i++) {

            // separator
            String separator = null;
            if (i > 0) {
                if (separators == null) {
                    separator = DEFAULT_SEPARATOR;
                } else if (separators.length == 1) {
                    separator = separators[0];
                } else {
                    separator = separators[i - 1];
                }

                if (builder.charAt(builder.length() - 1) != '\n') {
                    builder.append('\n');
                }
                builder.append('\n');
                builder.append(separator);
                if (!separator.endsWith("\n")) {
                    builder.append('\n');
                }
                builder.append('\n');
            }

            // header
            final String header = headers[i];
            builder.append(header);
        }

        return builder.toString();
    }

    public static HeaderSource of(String headerPath, String encoding, ResourceFinder finder) {
        return of(null, encoding, finder);
    }

    /**
     * Checking the params left to right, returns the first available {@link HeaderSource} that can be created. If
     * {@code inlineHeader} is not {@code null} returns a new {@link LiteralHeaderSource}. Otherwise attempts to create a
     * new {@link UrlHeaderSource} out of {@code headerPath} and {@code encoding}.
     *
     * @param multi container for multi license, or null if single license
     * @param inlineHeader the text of a license header template
     * @param headerPath a path resolvable by the {@code finder}
     * @param encoding the encoding to use when reading {@code headerPath}
     * @param finder the {@link ResourceFinder} to use to resolve {@code headerPath}
     * @return a new {@link HeaderSource}
     */
    public static HeaderSource of(Multi multi, String inlineHeader, String headerPath, String encoding, ResourceFinder finder) {
        if (multi != null) {
            if (multi.getInlineHeaders() != null && multi.getInlineHeaders().length > 0) {
                return new MultiLiteralHeaderSource(multi.getPreamble(), multi.getInlineHeaders(), multi.getSeparators());
            } else if (multi.getHeaders() == null || multi.getHeaders().length == 0) {
                throw new IllegalArgumentException("Either multi/inlineHeader or multi/header path needs to be specified");
            } else {
                final URL[] headerUrls = new URL[multi.getHeaders().length];
                for (int i = 0; i < multi.getHeaders().length; i++) {
                    try {
                        headerPath = multi.getHeaders()[i];
                        final URL headerUrl = finder.findResource(headerPath);
                        headerUrls[i] = headerUrl;
                    } catch (final MojoFailureException e) {
                        throw new IllegalArgumentException(
                                "Cannot read header document " + headerPath + ". Cause: " + e.getMessage(), e);
                    }
                }
                try {
                    return new MultiUrlHeaderSource(multi.getPreamble(), headerUrls, multi.getSeparators(), encoding);
                } catch (final IOException e) {
                    throw new IllegalArgumentException(
                            "Cannot read multi header documents. Cause: " + e.getMessage(), e);
                }
            }
        } else {
            if (inlineHeader != null && !inlineHeader.isEmpty()) {
                return new LiteralHeaderSource(inlineHeader);
            } else if (headerPath == null) {
                throw new IllegalArgumentException("Either inlineHeader or header path needs to be specified");
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
