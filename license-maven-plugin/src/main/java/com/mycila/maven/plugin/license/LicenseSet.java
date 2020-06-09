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

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class LicenseSet {

    /**
     * The base directory, in which to search for project files.
     */
    @Parameter(property = "license.basedir")
    public File basedir;

    /**
     * Location of the header. It can be a relative path, absolute path,
     * classpath resource, any URL. The plugin first check if the name specified
     * is a relative file, then an absolute file, then in the classpath. If not
     * found, it tries to construct a URL from the location.
     */
    @Parameter(property = "license.header")
    public String header;

    /**
     * Header, as text, directly in pom file. Using a CDATA section is strongly recommended.
     */
    @Parameter(property = "license.inlineHeader")
    public String inlineHeader;

    /**
     * Specifies additional header files to use when checking for the presence
     * of a valid header in your sources.
     * <br>
     * When using format goal, this property will be used to detect all valid
     * headers that don't need formatting.
     * <br>
     * When using remove goal, this property will be used to detect all valid
     * headers that also must be removed.
     */
    @Parameter
    public String[] validHeaders = new String[0];

    /**
     * Alternative to `header`, `inlineHeader`, or `validHeaders`
     * for use when code is multi-licensed.
     * Whilst you could create a concatenated header yourself,
     * a cleaner approach may be to specify more than one header
     * and have them concatenated together by the plugin. This
     * allows you to maintain each distinct license header in
     * its own file and combined them in different ways.
     */
    @Parameter
    public Multi multi;

    /**
     * Allows the use of external header definitions files. These files are
     * properties like files.
     */
    @Parameter
    public String[] headerDefinitions = new String[0];

    /**
     * HeadSections define special regions of a header that allow for dynamic
     * substitution and validation
     */
    @Parameter
    public HeaderSection[] headerSections = new HeaderSection[0];

    /**
     * You can set here some properties that you want to use when reading the
     * header file. You can use in your header file some properties like
     * ${year}, ${owner} or whatever you want for the name. They will be
     * replaced when the header file is read by those you specified in the
     * command line, in the POM and in system environment.
     */
    @Parameter
    public Map<String, String> properties = new HashMap<String, String>();

    /**
     * Specifies files, which are included in the check. By default, all files
     * are included.
     */
    @Parameter
    public String[] includes = new String[0];

    /**
     * Specifies files, which are excluded in the check. By default, only the
     * files matching the default exclude patterns are excluded.
     */
    @Parameter
    public String[] excludes = new String[0];

    /**
     * Specify the list of keywords to use to detect a header. A header must
     * include all keywords to be valid. By default, the word 'copyright' is
     * used. Detection is done case insensitive.
     */
    @Parameter
    public String[] keywords = new String[]{"copyright"};

    /**
     * Specify if you want to use default exclusions besides the files you have
     * excluded. Default exclusions exclude CVS and SVN folders, IDE descriptors
     * and so on.
     */
    @Parameter(property = "license.useDefaultExcludes")
    public Boolean useDefaultExcludes;

}
