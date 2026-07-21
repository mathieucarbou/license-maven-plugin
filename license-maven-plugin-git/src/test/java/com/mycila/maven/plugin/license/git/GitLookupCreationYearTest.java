/*
 * Copyright (C) 2008-2025 Mycila (mathieu.carbou@gmail.com)
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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import java.io.File;

/**
 * Regression test for the copyright creation year computation on the real license-maven-plugin
 * repository.
 * \u003cp\u003e
 * {@code AbstractLicenseMojo.java} was created in 2013, then renamed twice:
 * \u003col\u003e
 * \u003cli\u003e2013: {@code src/main/java/com/google/code/mojo/license/AbstractLicenseMojo.java}
 *     \u0026rarr; {@code src/main/java/com/mycila/maven/plugin/license/AbstractLicenseMojo.java} (R095)\u003c/li\u003e
 * \u003cli\u003e2015: {@code src/main/java/com/mycila/maven/plugin/license/AbstractLicenseMojo.java}
 *     \u0026rarr; {@code license-maven-plugin/src/main/java/com/mycila/maven/plugin/license/AbstractLicenseMojo.java} (R100)\u003c/li\u003e
 * \u003c/ol\u003e
 * The 2015 rename happens just after a merge commit. The previous implementation based solely on
 * {@link org.eclipse.jgit.revwalk.FollowFilter} could not traverse the merge and stopped at 2015,
 * reporting {@code copyrightCreationYear=2015} instead of 2013.
 * \u003cp\u003e
 * This test verifies that {@link GitLookup#getYearOfCreation(File)} follows renames across merges
 * and returns the actual creation year (2013), matching {@code git log --follow --diff-filter=A}.
 */
class GitLookupCreationYearTest {

  @Test
  void creationYearFollowsRenamesAcrossMerges() throws Exception {
    File repoRoot = new File(System.getProperty("user.dir")).getParentFile();
    File target = new File(repoRoot,
        "license-maven-plugin/src/main/java/com/mycila/maven/plugin/license/AbstractLicenseMojo.java");

    // Skip on shallow clones (e.g. GitHub Actions uses fetch-depth=1 by default).
    // This test requires the full git history to verify rename-following across merges.
    try (GitLookup lookup = GitLookup.create(repoRoot, new java.util.HashMap<>())) {
      Assumptions.assumeFalse(lookup.isShallowRepository(),
          "Skipped on shallow clones (CI uses fetch-depth=1); run locally with full history to verify rename-following");

      int lastChange = lookup.getYearOfLastChange(target);
      int creation = lookup.getYearOfCreation(target);

      // The file was created in 2013 (commit e63e7db2, "first commit") and has been
      // modified up to the current year. The previous implementation reported 2015
      // (the rename after the merge) instead of 2013.
      Assertions.assertEquals(2013, creation,
          "copyrightCreationYear should be 2013 (first commit), not the 2015 rename after the merge");
      Assertions.assertTrue(lastChange >= 2024,
          "copyrightLastYear should be a recent year, got " + lastChange);
    }
  }
}