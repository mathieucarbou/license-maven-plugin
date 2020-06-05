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
package com.mycila.maven.plugin.license;

import org.apache.maven.plugins.annotations.Parameter;

public class Multi {

    public static final String DEFAULT_SEPARATOR =
            "---------------------------------------------------------------------";

    /**
     * Preamble text which if present is placed before the first header.
     */
    @Parameter
    String preamble;

    /**
     * Location of each header. It can be a relative path, absolute path,
     * classpath resource, any URL. The plugin first check if the name specified
     * is a relative file, then an absolute file, then in the classpath. If not
     * found, it tries to construct a URL from the location.
     */
    @Parameter(alias = "header")
    String[] headers;

    /**
     * Header, as text, directly in pom file. Using a CDATA section is strongly recommended.
     */
    @Parameter(alias = "inlineHeader")
    String[] inlineHeaders;

    /**
     * One of more separators between the headers.
     * If there is only one separator it is placed between each header.
     * If there are multiple separators, then the first separator is placed
     * between the first and second license, the second separator is placed
     * between the second and third license, and so on...
     */
    @Parameter(alias = "separator")
    String[] separators;

    public String getPreamble() {
        return preamble;
    }

    public void setPreamble(final String preamble) {
        this.preamble = preamble;
    }

    public String[] getHeaders() {
        return headers;
    }

    public void setHeaders(final String[] headers) {
        this.headers = headers;
    }

    public String[] getInlineHeaders() {
        return inlineHeaders;
    }

    public void setInlineHeaders(final String[] inlineHeaders) {
        this.inlineHeaders = inlineHeaders;
    }

    public String[] getSeparators() {
        return separators;
    }

    public void setSeparators(final String[] separators) {
        this.separators = separators;
    }
}
