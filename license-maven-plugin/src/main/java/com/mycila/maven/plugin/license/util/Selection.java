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
package com.mycila.maven.plugin.license.util;

import com.mycila.maven.plugin.license.Default;
import org.codehaus.plexus.util.DirectoryScanner;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.*;
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
        String[] overrides = buildOverrideInclusions(useDefaultExcludes, included);
        this.included = buildInclusions(included, overrides);
        this.excluded = buildExclusions(useDefaultExcludes, excluded, overrides);
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

    private static String[] buildExclusions(boolean useDefaultExcludes, String[] excludes, String[] overrides) {
        List<String> exclusions = new ArrayList<String>();
        if (useDefaultExcludes) {
            exclusions.addAll(asList(Default.EXCLUDES));
        }
        // Remove from the default exclusion list the patterns that have been explicitly included
        for (String override : overrides) {
            exclusions.remove(override);
        }
        if (excludes != null && excludes.length > 0) {
            exclusions.addAll(asList(excludes));
        }
        return exclusions.toArray(new String[exclusions.size()]);
    }

    private static String[] buildInclusions(String[] includes, String[] overrides) {
        // if we use the default exclusion list, we just remove
        List<String> inclusions = new ArrayList<String>(asList(includes != null && includes.length > 0 ? includes : Default.INCLUDE));
        inclusions.removeAll(asList(overrides));
        if (inclusions.isEmpty()) {
            inclusions.addAll(asList(Default.INCLUDE));
        }
        return inclusions.toArray(new String[inclusions.size()]);
    }

    private static String[] buildOverrideInclusions(boolean useDefaultExcludes, String[] includes) {
        // return the list of patterns that we have explicitly included when using default exclude list
        if (!useDefaultExcludes || includes == null || includes.length == 0) {
            return new String[0];
        }
        List<String> overrides = new ArrayList<String>(asList(Default.EXCLUDES));
        overrides.retainAll(asList(includes));
        return overrides.toArray(new String[0]);
    }

}
