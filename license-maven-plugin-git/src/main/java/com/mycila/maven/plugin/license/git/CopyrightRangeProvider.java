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
package com.mycila.maven.plugin.license.git;

import com.mycila.maven.plugin.license.AbstractLicenseMojo;
import com.mycila.maven.plugin.license.PropertiesProvider;
import com.mycila.maven.plugin.license.document.Document;
import com.mycila.maven.plugin.license.util.Fn;
import com.mycila.maven.plugin.license.util.LazyMap;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Map;

/**
 * An implementation of {@link PropertiesProvider} that adds {@value #COPYRIGHT_LAST_YEAR_KEY} and
 * {@value #COPYRIGHT_YEARS_KEY} values - see {@link #adjustProperties(AbstractLicenseMojo, Map,
 * Document)}.
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
    gitLookup = GitLookup.create(mojo.workspace.basedir, currentProperties);

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
    var inceptionYear = Fn.memoize(() -> {
      String year = properties.get(INCEPTION_YEAR_KEY);
      if (year == null) {
        throw new RuntimeException("'" + INCEPTION_YEAR_KEY + "' must have a value for file " + document.getFile().getAbsolutePath());
      }
      try {
        return Integer.parseInt(year);
      } catch (NumberFormatException e1) {
        throw new RuntimeException("'" + INCEPTION_YEAR_KEY + "' must be an integer ; found = " + year + " file: " + document.getFile().getAbsolutePath());
      }
    });

    var copyrightEnd = Fn.memoize(() -> {
      try {
        return gitLookup.getYearOfLastChange(document.getFile());
      } catch (IOException e) {
        throw new UncheckedIOException(document.getFile().getAbsolutePath(), e);
      } catch (GitAPIException e) {
        throw new RuntimeException(document.getFile().getAbsolutePath(), e);
      }
    });

    var copyrightStart = Fn.memoize(() -> {
      try {
        return gitLookup.getYearOfCreation(document.getFile());
      } catch (IOException e) {
        throw new UncheckedIOException(document.getFile().getAbsolutePath(), e);
      }
    });

    LazyMap<String, String> result = new LazyMap<>(4);
    result.putSupplier(COPYRIGHT_CREATION_YEAR_KEY, Fn.memoize(() -> {
      return copyrightStart.get().toString();
    }));
    result.putSupplier(COPYRIGHT_LAST_YEAR_KEY, Fn.memoize(() -> {
      return copyrightEnd.get().toString();
    }));
    result.putSupplier(COPYRIGHT_YEARS_KEY, Fn.memoize(() -> {
      final String copyrightYears;
      if (inceptionYear.get() >= copyrightEnd.get()) {
        copyrightYears = "" + inceptionYear.get();
      } else {
        copyrightYears = inceptionYear.get() + "-" + copyrightEnd.get();
      }
      return copyrightYears;
    }));
    result.putSupplier(COPYRIGHT_EXISTENCE_YEARS_KEY, Fn.memoize(() -> {
      final String copyrightExistenceYears;
      if (copyrightStart.get() >= copyrightEnd.get()) {
        copyrightExistenceYears = Integer.toString(copyrightStart.get());
      } else {
        copyrightExistenceYears = copyrightStart.get() + "-" + copyrightEnd.get();
      }
      return copyrightExistenceYears;
    }));
    return result;
  }
}
