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
package com.mycila.maven.plugin.license.git;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;

import com.mycila.maven.plugin.license.AbstractLicenseMojo;
import com.mycila.maven.plugin.license.PropertiesProvider;
import com.mycila.maven.plugin.license.document.Document;
import com.mycila.maven.plugin.license.git.GitLookup.DateSource;

/**
 * An implementation of {@link PropertiesProvider} that adds {@value #COPYRIGHT_LAST_YEAR_KEY} and
 * {@value #COPYRIGHT_YEARS_KEY} values - see
 * {@link #getAdditionalProperties(AbstractLicenseMojo, Properties, Document)}.
 *
 * @author <a href="mailto:ppalaga@redhat.com">Peter Palaga</a>
 */
public class CopyrightRangeProvider implements PropertiesProvider {

    public static final String COPYRIGHT_LAST_YEAR_KEY = "license.git.copyrightLastYear";
    public static final String COPYRIGHT_LAST_YEAR_MAX_COMMITS_LOOKUP_KEY = "license.git.copyrightLastYearMaxCommitsLookup";
    public static final String COPYRIGHT_LAST_YEAR_SOURCE_KEY = "license.git.copyrightLastYearSource";
    public static final String COPYRIGHT_LAST_YEAR_TIME_ZONE_KEY = "license.git.copyrightLastYearTimeZone";
    public static final String COPYRIGHT_YEARS_KEY = "license.git.copyrightYears";
    public static final String INCEPTION_YEAR_KEY = "project.inceptionYear";

    private volatile GitLookup gitLookup;

    public CopyrightRangeProvider() {
        super();
    }

    /**
     * Returns an unmodifiable map containing two entries {@value #COPYRIGHT_LAST_YEAR_KEY} and
     * {@value #COPYRIGHT_YEARS_KEY} whose values are set based on inspecting git history.
     * <ul>
     * <li>{@value #COPYRIGHT_LAST_YEAR_KEY} key stores the year from the commiter date of the last git commit that has
     * modified the supplied {@code document}.
     * <li>{@value #COPYRIGHT_YEARS_KEY} key stores the range from {@value #INCEPTION_YEAR_KEY} value to
     * {@value #COPYRIGHT_LAST_YEAR_KEY} value. If both values a equal, only the {@value #INCEPTION_YEAR_KEY} value is
     * returned; otherwise, the two values are combined using dash, so that the result is e.g. {@code "2000 - 2010"}.
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
            Map<String, String> result = new HashMap<String, String>(3);
            int copyrightEnd = getGitLookup(document.getFile(), properties).getYearOfLastChange(document.getFile());
            result.put(COPYRIGHT_LAST_YEAR_KEY, Integer.toString(copyrightEnd));
            final String copyrightYears;
            if (inceptionYearInt >= copyrightEnd) {
                copyrightYears = inceptionYear;
            } else {
                copyrightYears = inceptionYear + "-" + copyrightEnd;
            }
            result.put(COPYRIGHT_YEARS_KEY, copyrightYears);
            return Collections.unmodifiableMap(result);
        } catch (Exception e) {
            throw new RuntimeException("Could not compute the year of the last git commit for file "
                    + document.getFile().getAbsolutePath(), e);
        }
    }

    /**
     * Lazily initializes #gitLookup assuming that all subsequent calls to this method will be related to the same
     * git repository.
     *
     * @param file
     * @return
     * @throws IOException
     */
    private GitLookup getGitLookup(File file, Properties props) throws IOException {
        if (gitLookup == null) {
            synchronized (this) {
                if (gitLookup == null) {
                    String dateSourceString = props.getProperty(COPYRIGHT_LAST_YEAR_SOURCE_KEY,
                            DateSource.AUTHOR.name());
                    DateSource dateSource = DateSource.valueOf(dateSourceString.toUpperCase(Locale.US));
                    String checkCommitsCountString = props.getProperty(COPYRIGHT_LAST_YEAR_MAX_COMMITS_LOOKUP_KEY);
                    int checkCommitsCount = GitLookup.DEFAULT_COMMITS_COUNT;
                    if (checkCommitsCountString != null) {
                        checkCommitsCountString = checkCommitsCountString.trim();
                        checkCommitsCount = Integer.parseInt(checkCommitsCountString);
                    }
                    final TimeZone timeZone;
                    String tzString = props.getProperty(COPYRIGHT_LAST_YEAR_TIME_ZONE_KEY);
                    switch (dateSource) {
                    case COMMITER:
                        timeZone = tzString == null ? GitLookup.DEFAULT_ZONE : TimeZone.getTimeZone(tzString);
                        break;
                    case AUTHOR:
                        if (tzString != null) {
                            throw new RuntimeException(COPYRIGHT_LAST_YEAR_TIME_ZONE_KEY + " must not be set with "
                                    + COPYRIGHT_LAST_YEAR_SOURCE_KEY + " = " + DateSource.AUTHOR.name()
                                    + " because git author name already contrains time zone information.");
                        }
                        timeZone = null;
                        break;
                    default:
                        throw new IllegalStateException("Unexpected " + DateSource.class.getName() + " " + dateSource);
                    }
                    gitLookup = new GitLookup(file, dateSource, timeZone, checkCommitsCount);
                }
            }
        }
        return gitLookup;
    }

}
