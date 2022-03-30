/*
 * Copyright (C) 2008-2022 Mycila (mathieu.carbou@gmail.com)
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

import com.mycila.maven.plugin.license.AbstractLicenseMojo;
import com.mycila.maven.plugin.license.PropertiesProvider;
import com.mycila.maven.plugin.license.document.Document;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * An implementation of {@link PropertiesProvider} that adds {@value #COPYRIGHT_LAST_YEAR_KEY} and
 * {@value #COPYRIGHT_YEARS_KEY} values - see {@link #adjustProperties(AbstractLicenseMojo, Map,
 * Document)}.
 *
 * @author <a href="mailto:ppalaga@redhat.com">Peter Palaga</a>
 */
public class CopyrightRangeProvider implements PropertiesProvider {

  public static final String COPYRIGHT_LAST_YEAR_KEY = "license.git.copyrightLastYear";
  public static final String COPYRIGHT_CREATION_YEAR_KEY = "license.git.copyrightCreationYear";
  public static final String COPYRIGHT_EXISTENCE_YEARS_KEY = "license.git.copyrightExistenceYears";
  public static final String COPYRIGHT_YEARS_KEY = "license.git.copyrightYears";
  public static final String INCEPTION_YEAR_KEY = "project.inceptionYear";

  private GitLookup gitLookup;

  @Override
  public void init(AbstractLicenseMojo mojo, Map<String, String> currentProperties) {
    gitLookup = GitLookup.create(mojo.defaultBasedir, currentProperties);

    // One-time warning for shallow repo
    if (mojo.warnIfShallow && gitLookup.isShallowRepository()) {
      mojo.warn("Shallow git repository detected. Year property values may not be accurate.");
    }
  }

  @Override
  public void close() {
    if (gitLookup != null) {
      gitLookup.close();
    }
  }

  /**
   * Returns an unmodifiable map containing the following entries, whose values are set based on inspecting git history.
   *
   * <ul>
   * <li>{@value #COPYRIGHT_LAST_YEAR_KEY} key stores the year from the committer date of the last git commit that has
   * modified the supplied {@code document}.</li>
   * <li>{@value #COPYRIGHT_YEARS_KEY} key stores the range from {@value #INCEPTION_YEAR_KEY} value to
   * {@value #COPYRIGHT_LAST_YEAR_KEY} value. If both values a equal, only the {@value #INCEPTION_YEAR_KEY} value is
   * returned; otherwise, the two values are combined using dash, so that the result is e.g. {@code "2000-2010"}.</li>
   * <li>{@value #COPYRIGHT_CREATION_YEAR_KEY} key stores the year from the committer date of the first git commit for
   * the supplied {@code document}.</li>
   * <li>{@value #COPYRIGHT_EXISTENCE_YEARS_KEY} key stores the range from {@value #COPYRIGHT_CREATION_YEAR_KEY} value to
   * {@value #COPYRIGHT_LAST_YEAR_KEY} value.  If both values are equal only the {@value #COPYRIGHT_CREATION_YEAR_KEY} is returned;
   * otherwise, the two values are combined using dash, so that the result is e.g. {@code "2005-2010"}.</li>
   * </ul>
   * The {@value #INCEPTION_YEAR_KEY} value is read from the supplied properties and it must available. Otherwise a
   * {@link RuntimeException} is thrown.
   */
  @Override
  public Map<String, String> adjustProperties(AbstractLicenseMojo mojo,
      Map<String, String> properties, Document document) {
    String inceptionYear = properties.get(INCEPTION_YEAR_KEY);
    if (inceptionYear == null) {
      throw new RuntimeException("'" + INCEPTION_YEAR_KEY + "' must have a value for file "
          + document.getFile().getAbsolutePath());
    }
    final int inceptionYearInt;
    try {
      inceptionYearInt = Integer.parseInt(inceptionYear);
    } catch (NumberFormatException e1) {
      throw new RuntimeException(
          "'" + INCEPTION_YEAR_KEY + "' must be an integer ; found = " + inceptionYear + " file: "
              + document.getFile().getAbsolutePath());
    }
    try {
      Map<String, String> result = new HashMap<>(4);

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

      final String copyrightExistenceYears;
      if (copyrightStart >= copyrightEnd) {
        copyrightExistenceYears = Integer.toString(copyrightStart);
      } else {
        copyrightExistenceYears = copyrightStart + "-" + copyrightEnd;
      }
      result.put(COPYRIGHT_EXISTENCE_YEARS_KEY, copyrightExistenceYears);

      return Collections.unmodifiableMap(result);
    } catch (IOException | GitAPIException e) {
      throw new RuntimeException(
          "CopyrightRangeProvider error on file: " + document.getFile().getAbsolutePath() + ": "
              + e.getMessage(), e);
    }
  }
}
