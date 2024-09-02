/*
 * Copyright (C) 2008-2024 Mycila (mathieu.carbou@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mycila.maven.plugin.license.fs;

import com.mycila.maven.plugin.license.AbstractLicenseMojo;
import com.mycila.maven.plugin.license.PropertiesProvider;
import com.mycila.maven.plugin.license.document.Document;

import java.io.File;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static java.time.ZoneOffset.UTC;

/**
 * An implementation of {@link PropertiesProvider} that adds {@value #COPYRIGHT_LAST_YEAR_KEY} and
 * {@value #COPYRIGHT_YEARS_KEY} values - see {@link #adjustProperties(AbstractLicenseMojo, Map,
 * Document)}.
 */
public class CopyrightRangeProvider implements PropertiesProvider {

  public static final String COPYRIGHT_LAST_YEAR_KEY = "license.fs.copyrightLastYear";
  public static final String COPYRIGHT_YEARS_KEY = "license.fs.copyrightYears";
  public static final String INCEPTION_YEAR_KEY = "project.inceptionYear";

  /**
   * Returns an unmodifiable map containing the following entries, whose values are set based on inspecting the filesystem.
   *
   * <ul>
   * <li>{@value #COPYRIGHT_LAST_YEAR_KEY} key stores the year from the file's last modification timestamp .</li>
   * <li>{@value #COPYRIGHT_YEARS_KEY} key stores the range from {@value #INCEPTION_YEAR_KEY} value to
   * {@value #COPYRIGHT_LAST_YEAR_KEY} value. If both values a equal, only the {@value #INCEPTION_YEAR_KEY} value is
   * returned; otherwise, the two values are combined using dash, so that the result is e.g. {@code "2000-2010"}.</li>
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

      int copyrightEnd = getYearOfLastChange(document.getFile());
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
      throw new RuntimeException(
          "CopyrightRangeProvider error on file: " + document.getFile().getAbsolutePath() + ": "
              + e.getMessage(), e);
    }
  }

  private static int getYearOfLastChange(File file) {
    return Instant.ofEpochMilli(file.lastModified()).atOffset(UTC).getYear();
  }

}
