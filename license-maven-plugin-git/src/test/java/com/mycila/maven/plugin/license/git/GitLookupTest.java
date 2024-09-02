/*
 * Copyright (C) 2008-2023 Mycila (mathieu.carbou@gmail.com)
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

import com.mycila.maven.plugin.license.git.GitLookup.DateSource;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

class GitLookupTest {

  private static Path gitRepoRoot;

  @TempDir
  static File tempFolder;

  @BeforeAll
  static void beforeClass() throws IOException {
    URL url = GitLookupTest.class.getResource("git-test-repo.zip");
    gitRepoRoot = Paths.get(tempFolder.toPath() + File.separator + "git-test-repo");
    unzip(url, tempFolder.toPath());
  }

  static void unzip(URL url, Path unzipDestination) throws IOException {
    ZipInputStream zipInputStream = null;
    try {
      zipInputStream = new ZipInputStream(new BufferedInputStream(url.openStream()));
      ZipEntry entry;
      byte[] buffer = new byte[2048];
      while ((entry = zipInputStream.getNextEntry()) != null) {

        String fileName = entry.getName();
        Path unzippedFile = Paths.get(unzipDestination.toAbsolutePath() + File.separator + fileName);
        if (entry.isDirectory()) {
          unzippedFile.toFile().mkdirs();
        } else {
          unzippedFile.toFile().getParentFile().mkdirs();
          try (OutputStream out = new BufferedOutputStream(new FileOutputStream(unzippedFile.toFile()), 2048)) {
            int len;
            while ((len = zipInputStream.read(buffer)) != -1) {
              out.write(buffer, 0, len);
            }
          }
        }
      }
    } finally {
      if (zipInputStream != null) {
        zipInputStream.close();
      }
    }
  }

  @Test
  void modified() throws GitAPIException, IOException {
    assertLastChange(newAuthorLookup(), "dir1/file1.txt", 2006);
    assertLastChange(newCommitterLookup(), "dir1/file1.txt", 2006);

    assertCreation(newAuthorLookup(), "dir1/file1.txt", 2000);
    assertCreation(newCommitterLookup(), "dir1/file1.txt", 2000);
  }

  @Test
  void justCreated() throws GitAPIException, IOException {
    assertLastChange(newAuthorLookup(), "dir2/file2.txt", 2007);
    assertLastChange(newCommitterLookup(), "dir2/file2.txt", 2007);

    assertCreation(newAuthorLookup(), "dir2/file2.txt", 2007);
    assertCreation(newCommitterLookup(), "dir2/file2.txt", 2007);
  }

  @Test
  void moved() throws GitAPIException, IOException {
    assertLastChange(newAuthorLookup(), "dir1/file3.txt", 2009);
    assertLastChange(newCommitterLookup(), "dir1/file3.txt", 2010);

    // In this case the file moved and its creation data could not be tracked
    assertCreation(newAuthorLookup(), "dir1/file3.txt", 2009);
    assertCreation(newCommitterLookup(), "dir1/file3.txt", 2010);
  }

  @Test
  void newUnstaged() throws GitAPIException, IOException {
    int currentYear = getCurrentGmtYear();
    assertLastChange(newAuthorLookup(), "dir1/file5.txt", currentYear);
    assertLastChange(newCommitterLookup(), "dir1/file5.txt", currentYear);

    assertCreation(newAuthorLookup(), "dir1/file5.txt", currentYear);
    assertCreation(newCommitterLookup(), "dir1/file5.txt", currentYear);
  }

  @Test
  void newStaged() throws GitAPIException, IOException {
    int currentYear = getCurrentGmtYear();
    assertLastChange(newAuthorLookup(), "dir1/file6.txt", currentYear);
    assertLastChange(newCommitterLookup(), "dir1/file6.txt", currentYear);

    assertCreation(newAuthorLookup(), "dir1/file6.txt", currentYear);
    assertCreation(newCommitterLookup(), "dir1/file6.txt", currentYear);
  }

  /**
   * @return
   */
  private int getCurrentGmtYear() {
    Calendar result = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
    result.setTimeInMillis(System.currentTimeMillis());
    return result.get(Calendar.YEAR);
  }

  @Test
  void reuseProvider() throws GitAPIException, IOException {
    GitLookup provider = newAuthorLookup();
    assertLastChange(provider, "dir1/file1.txt", 2006);
    assertLastChange(provider, "dir2/file2.txt", 2007);
    assertLastChange(provider, "dir1/file3.txt", 2009);
  }

  @Test
  void timezone() throws GitAPIException, IOException {
    // do not fail if a tz is./mv  set, it will just be unused
    // it allows parent poms to pre-defined properties and let sub modules use them if needed
    GitLookup.create(gitRepoRoot.toFile(), buildProps(DateSource.AUTHOR, "GMT", "10", null));

    /* null is GMT */
    GitLookup nullTzLookup = GitLookup.create(gitRepoRoot.toFile(),
        buildProps(DateSource.COMMITER, null, "10", null));
    assertLastChange(nullTzLookup, "dir1/file3.txt", 2010);

    /* explicit GMT */
    GitLookup gmtLookup = GitLookup.create(gitRepoRoot.toFile(),
        buildProps(DateSource.COMMITER, "GMT", "10", null));
    assertLastChange(gmtLookup, "dir1/file3.txt", 2010);

    /*
     * explicit non-GMT zome. Note that the relevant commit's (GMT) time stamp is 2010-12-31T23:30:00 which yealds
     * 2011 in the CET (+01:00) time zone
     */
    GitLookup cetLookup = GitLookup.create(gitRepoRoot.toFile(),
        buildProps(DateSource.COMMITER, "CET", "10", null));
    assertLastChange(cetLookup, "dir1/file3.txt", 2011);

  }

  private Map<String, String> buildProps(DateSource ds, String tz, String history, String commitsToIgnoreCSV) {
    Map<String, String> props = new HashMap<>();
    if (history != null) {
      props.put(GitLookup.MAX_COMMITS_LOOKUP_KEY, history);
    }
    if (tz != null) {
      props.put(GitLookup.COPYRIGHT_LAST_YEAR_TIME_ZONE_KEY, tz);
    }
    if (ds != null) {
      props.put(GitLookup.COPYRIGHT_LAST_YEAR_SOURCE_KEY, ds.name());
    }
    if (commitsToIgnoreCSV != null) {
      props.put(GitLookup.COMMITS_TO_IGNORE_KEY, commitsToIgnoreCSV);
    }
    return props;
  }

  private GitLookup newAuthorLookup() {
    return GitLookup.create(gitRepoRoot.toFile(), buildProps(DateSource.AUTHOR, null, "10", null));
  }

  private GitLookup newCommitterLookup() {
    return GitLookup.create(gitRepoRoot.toFile(), buildProps(DateSource.COMMITER, null, "10", null));
  }

  @Test
  void ignoreCommitsInLastChange() throws GitAPIException, IOException {
    assertLastChange(newAuthorLookup("95d52919cbe340dc271cf1f5ec68cf36705bd3a3"), "dir1/file1.txt", 2004);
    assertLastChange(newCommitterLookup("95d52919cbe340dc271cf1f5ec68cf36705bd3a3"), "dir1/file1.txt", 2004);
  }

  @Test
  void doNotIgnoreCommitsInCreation() throws GitAPIException, IOException {
    assertCreation(newAuthorLookup("53b44baedc5a378f9b665da12f298e1003793219"), "dir1/file1.txt", 2000);
    assertCreation(newCommitterLookup("53b44baedc5a378f9b665da12f298e1003793219"), "dir1/file1.txt", 2000);
  }

  private GitLookup newAuthorLookup(String... commitsToIgnore) throws IOException {
    String commitsToIgnoreCSV = String.join(",", commitsToIgnore);
    return GitLookup.create(gitRepoRoot.toFile(), buildProps(DateSource.AUTHOR, null, "10", commitsToIgnoreCSV));
  }

  private GitLookup newCommitterLookup(String... commitsToIgnore) throws IOException {
    String commitsToIgnoreCSV = String.join(",", commitsToIgnore);
    return GitLookup.create(gitRepoRoot.toFile(), buildProps(DateSource.COMMITER, null, "10", commitsToIgnoreCSV));
  }

  private void assertLastChange(GitLookup provider, String relativePath, int expected) throws
      GitAPIException, IOException {
    int actual = provider.getYearOfLastChange(Paths.get(gitRepoRoot + File.separator
        + relativePath.replace('/', File.separatorChar)).toFile());
    Assertions.assertEquals(expected, actual);
  }

  private void assertCreation(GitLookup provider, String relativePath, int expected) throws
      GitAPIException, IOException {
    int actual = provider.getYearOfCreation(Paths.get(gitRepoRoot + File.separator
        + relativePath.replace('/', File.separatorChar)).toFile());
    Assertions.assertEquals(expected, actual);
  }

}