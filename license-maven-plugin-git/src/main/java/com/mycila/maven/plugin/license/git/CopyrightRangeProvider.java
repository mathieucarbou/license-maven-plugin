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
package com.mycila.maven.plugin.license.git;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.mycila.maven.plugin.license.AbstractLicenseMojo;
import com.mycila.maven.plugin.license.PropertiesProvider;
import com.mycila.maven.plugin.license.document.Document;

/**
 * An implementation of {@link PropertiesProvider} that adds {@value #COPYRIGHT_LAST_YEAR_KEY} and
 * {@value #COPYRIGHT_YEARS_KEY} values - see
 * {@link #getAdditionalProperties(AbstractLicenseMojo, Properties, Document)}.
 *
 * @author <a href="mailto:ppalaga@redhat.com">Peter Palaga</a>
 */
public class CopyrightRangeProvider extends GitPropertiesProvider implements PropertiesProvider {

    public static final String COPYRIGHT_LAST_YEAR_KEY = "license.git.copyrightLastYear";
    public static final String COPYRIGHT_CREATION_YEAR_KEY = "license.git.copyrightCreationYear";
    public static final String COPYRIGHT_YEARS_KEY = "license.git.copyrightYears";
    public static final String INCEPTION_YEAR_KEY = "project.inceptionYear";
    

    public CopyrightRangeProvider() {
        super();
    }

    /**
     * Returns an unmodifiable map containing the three entries {@value #COPYRIGHT_LAST_YEAR_KEY}, {@value #COPYRIGHT_YEARS_KEY},
     * and {@value #COPYRIGHT_CREATION_YEAR_KEY}, whose values are set based on inspecting git history.
     *
     * <ul>
     * <li>{@value #COPYRIGHT_LAST_YEAR_KEY} key stores the year from the committer date of the last git commit that has
     * modified the supplied {@code document}.
     * <li>{@value #COPYRIGHT_YEARS_KEY} key stores the range from {@value #INCEPTION_YEAR_KEY} value to
     * {@value #COPYRIGHT_LAST_YEAR_KEY} value. If both values a equal, only the {@value #INCEPTION_YEAR_KEY} value is
     * returned; otherwise, the two values are combined using dash, so that the result is e.g. {@code "2000 - 2010"}.
     * <li>{@value #COPYRIGHT_CREATION_YEAR_KEY} key stores the year from the committer date of the first git commit for
     * the supplied {@code document}.
     * </ul>
     * The {@value #INCEPTION_YEAR_KEY} value is read from the supplied properties and it must available. Otherwise a
     * {@link RuntimeException} is thrown.
     *
     */
    public Map<String, String> getAdditionalProperties(AbstractLicenseMojo mojo, Properties properties,
            Document document) {
        String inceptionYear = properties.getProperty(INCEPTION_YEAR_KEY);
        if (inceptionYear == null) {
            throw new RuntimeException("'"+ INCEPTION_YEAR_KEY +"' must have a value for file "
                    + document.getFile().getAbsolutePath());
        }
        final int inceptionYearInt;
        try {
            inceptionYearInt = Integer.parseInt(inceptionYear);
        } catch (NumberFormatException e1) {
            throw new RuntimeException("'"+ INCEPTION_YEAR_KEY +"' must be an integer ; found = " + inceptionYear +" file: "
                    + document.getFile().getAbsolutePath());
        }
        try {
            Map<String, String> result = new HashMap<String, String>(4);
            GitLookup gitLookup = getGitLookup(document.getFile(), properties);
            int copyrightEnd = gitLookup.getYearOfLastChange(document.getFile());
            result.put(COPYRIGHT_LAST_YEAR_KEY, Integer.toString(copyrightEnd));
            final String copyrightYears;
            if (inceptionYearInt >= copyrightEnd) {
                copyrightYears = inceptionYear;
            } else {
                copyrightYears = inceptionYear + "-" + copyrightEnd;
            }
            result.put(COPYRIGHT_YEARS_KEY, copyrightYears);

            int copyrightStart = gitLookup.getYearOfCreation(document.getFile());
            result.put(COPYRIGHT_CREATION_YEAR_KEY, Integer.toString(copyrightStart));
            return Collections.unmodifiableMap(result);
        } catch (Exception e) {
            throw new RuntimeException("Could not compute the year of the last git commit for file "
                    + document.getFile().getAbsolutePath(), e);
        }
    }
}
