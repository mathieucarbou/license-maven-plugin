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

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;
import java.util.TimeZone;

/**
 *
 * @author <a href="mailto:ppalaga@redhat.com">Peter Palaga</a>
 */
public class GitPropertiesProvider {
    
    private GitLookup gitLookup;
    public static final String COPYRIGHT_LAST_YEAR_MAX_COMMITS_LOOKUP_KEY = "license.git.copyrightLastYearMaxCommitsLookup";
    public static final String COPYRIGHT_LAST_YEAR_SOURCE_KEY = "license.git.copyrightLastYearSource";
    public static final String COPYRIGHT_LAST_YEAR_TIME_ZONE_KEY = "license.git.copyrightLastYearTimeZone";
    
    public GitPropertiesProvider(){};

    /**
     * Lazily initializes #gitLookup assuming that all subsequent calls to this method will be related to the same
     * git repository.
     *
     * @param file
     * @return
     * @throws IOException
     */
    GitLookup getGitLookup(File file, Properties props) throws IOException {
        if (gitLookup == null) {
            synchronized (this) {
                if (gitLookup == null) {
                    String dateSourceString = props.getProperty(COPYRIGHT_LAST_YEAR_SOURCE_KEY,
                            GitLookup.DateSource.AUTHOR.name());
                    GitLookup.DateSource dateSource = GitLookup.DateSource.valueOf(dateSourceString.toUpperCase(Locale.US));
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
                                    + COPYRIGHT_LAST_YEAR_SOURCE_KEY + " = " + GitLookup.DateSource.AUTHOR.name()
                                    + " because git author name already contrains time zone information.");
                        }
                        timeZone = null;
                        break;
                    default:
                        throw new IllegalStateException("Unexpected " + GitLookup.DateSource.class.getName() + " " + dateSource);
                    }
                    gitLookup = new GitLookup(file, dateSource, timeZone, checkCommitsCount);
                }
            }
        }
        return gitLookup;
    }
}
