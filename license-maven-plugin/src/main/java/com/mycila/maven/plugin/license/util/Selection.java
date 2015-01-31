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

import com.mycila.maven.plugin.license.Default;
import org.codehaus.plexus.util.DirectoryScanner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * <b>Date:</b> 16-Feb-2008<br>
 * <b>Author:</b> Mathieu Carbou (mathieu.carbou@gmail.com)
 */
public final class Selection {

    private final File basedir;
    private final String[] included;
    private final String[] excluded;

    private DirectoryScanner scanner;

    public Selection(File basedir, String[] included, String[] excluded, boolean useDefaultExcludes) {
        this.basedir = basedir;
        this.included = buildInclusions(included);
        this.excluded = buildExclusions(useDefaultExcludes, excluded);
    }

    public String[] getSelectedFiles() {
        scanIfneeded();
        return scanner.getIncludedFiles();
    }

    public File getBasedir() {
        return basedir;
    }

    public String[] getIncluded() {
        return included;
    }

    public String[] getExcluded() {
        return excluded;
    }

    private void scanIfneeded() {
        if (scanner == null) {
            scanner = new DirectoryScanner();
            scanner.setBasedir(basedir);
            scanner.setIncludes(included);
            scanner.setExcludes(excluded);
            scanner.scan();
        }
    }

    private static String[] buildExclusions(boolean useDefaultExcludes, String... excludes) {
        List<String> exclusions = new ArrayList<String>();
        if (useDefaultExcludes)
            exclusions.addAll(asList(Default.EXCLUDES));
        if (excludes != null && excludes.length > 0)
            exclusions.addAll(asList(excludes));
        return exclusions.toArray(new String[exclusions.size()]);
    }

    private static String[] buildInclusions(String... includes) {
        return includes != null && includes.length > 0 ? includes : Default.INCLUDE;
    }

}
