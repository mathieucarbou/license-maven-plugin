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

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Map;
import java.util.function.Supplier;

/**
 * An implementation of {@link PropertiesProvider} that adds {@value
 * #COPYRIGHT_CREATION_AUTHOR_NAME_KEY} and {@value #COPYRIGHT_CREATION_AUTHOR_EMAIL_KEY} values -
 * see {@link #adjustProperties(AbstractLicenseMojo, Map, Document)}.
 */
public class CopyrightAuthorProvider implements PropertiesProvider {

  public static final String COPYRIGHT_CREATION_AUTHOR_NAME_KEY = "license.git.CreationAuthorName";
  public static final String COPYRIGHT_CREATION_AUTHOR_EMAIL_KEY = "license.git.CreationAuthorEmail";

  private GitLookup gitLookup;

  @Override
  public void init(AbstractLicenseMojo mojo, Map<String, String> currentProperties) {
    gitLookup = GitLookup.create(mojo.defaultBasedir, currentProperties);

    // One-time warning for shallow repo
    if (mojo.warnIfShallow && gitLookup.isShallowRepository()) {
      mojo.warn("Shallow git repository detected. Author property values may not be accurate.");
    }
  }

  @Override
  public void close() {
    if (gitLookup != null) {
      gitLookup.close();
    }
  }

  /**
   * Returns an unmodifiable map containing the two entries {@value #COPYRIGHT_CREATION_AUTHOR_NAME_KEY} and {@value #COPYRIGHT_CREATION_AUTHOR_EMAIL_KEY},
   * , whose values are set based on inspecting git history.
   *
   * <ul>
   * <li>{@value #COPYRIGHT_CREATION_AUTHOR_NAME_KEY} key stores the author name of the first git commit.
   * <li>{@value #COPYRIGHT_CREATION_AUTHOR_EMAIL_KEY} key stores the author's email address of the first git commit.
   * </ul>
   */
  @Override
  public Map<String, Supplier<String>> adjustLazyProperties(AbstractLicenseMojo mojo, Map<String, String> properties, Document document) {

    return Map.of(
        COPYRIGHT_CREATION_AUTHOR_NAME_KEY, () -> {
          try {
            return gitLookup.getAuthorNameOfCreation(document.getFile());
          } catch (IOException e) {
            throw new UncheckedIOException(
                "CopyrightAuthorProvider error on file: " + document.getFile().getAbsolutePath() + ": " + e.getMessage(), e);
          }
        }, COPYRIGHT_CREATION_AUTHOR_EMAIL_KEY, () -> {
          try {
            return gitLookup.getAuthorEmailOfCreation(document.getFile());
          } catch (IOException e) {
            throw new UncheckedIOException(
                "CopyrightAuthorProvider error on file: " + document.getFile().getAbsolutePath() + ": " + e.getMessage(), e);
          }
        });
  }
}
