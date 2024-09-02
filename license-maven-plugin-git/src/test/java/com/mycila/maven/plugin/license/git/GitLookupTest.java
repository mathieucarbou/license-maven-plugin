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
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

class GitLookupTest {

  private static Path gitRepoRoot;

  @TempDir
  static Path tempFolder;

  @BeforeAll
  static void beforeClass() throws IOException {
    URL url = GitLookupTest.class.getResource("git-test-repo.zip");
    gitRepoRoot = tempFolder.resolve("git-test-repo");
    unzip(url, tempFolder);
  }

  static void unzip(URL url, Path unzipDestination) throws IOException {
    try (ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(url.openStream()))) {
      ZipEntry entry;
      byte[] buffer = new byte[2048];
      while ((entry = zipInputStream.getNextEntry()) != null) {

        String fileName = entry.getName();
        Path unzippedFile = unzipDestination.resolve(fileName);
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
    }
  }

  @Test
  void modified() throws GitAPIException, IOException {
    try (GitLookup authorProvider = newAuthorLookup();
        GitLookup committerProvider = newCommitterLookup()) {

      assertLastChange(authorProvider, "dir1/file1.txt", 2006);
      assertLastChange(committerProvider, "dir1/file1.txt", 2006);

      assertCreation(authorProvider, "dir1/file1.txt", 2000);
      assertCreation(committerProvider, "dir1/file1.txt", 2000);
    }
  }

  @Test
  void justCreated() throws GitAPIException, IOException {
    try (GitLookup authorProvider = newAuthorLookup();
        GitLookup committerProvider = newCommitterLookup()) {

      assertLastChange(authorProvider, "dir2/file2.txt", 2007);
      assertLastChange(committerProvider, "dir2/file2.txt", 2007);

      assertCreation(authorProvider, "dir2/file2.txt", 2007);
      assertCreation(committerProvider, "dir2/file2.txt", 2007);
    }
  }

  @Test
  void moved() throws GitAPIException, IOException {
    try (GitLookup authorProvider = newAuthorLookup();
        GitLookup committerProvider = newCommitterLookup()) {

      assertLastChange(authorProvider, "dir1/file3.txt", 2009);
      assertLastChange(committerProvider, "dir1/file3.txt", 2010);

      // In this case the file moved and its creation data could not be tracked
      assertCreation(authorProvider, "dir1/file3.txt", 2009);
      assertCreation(committerProvider, "dir1/file3.txt", 2010);
    }
  }

  @Test
  void newUnstaged() throws GitAPIException, IOException {
    int currentYear = getCurrentGmtYear();
    try (GitLookup authorProvider = newAuthorLookup();
        GitLookup committerProvider = newCommitterLookup()) {

      assertLastChange(authorProvider, "dir1/file5.txt", currentYear);
      assertLastChange(committerProvider, "dir1/file5.txt", currentYear);

      assertCreation(authorProvider, "dir1/file5.txt", currentYear);
      assertCreation(committerProvider, "dir1/file5.txt", currentYear);
    }
  }

  @Test
  void newStaged() throws GitAPIException, IOException {
    int currentYear = getCurrentGmtYear();
    try (GitLookup authorProvider = newAuthorLookup();
        GitLookup committerProvider = newCommitterLookup()) {

      assertLastChange(authorProvider, "dir1/file6.txt", currentYear);
      assertLastChange(committerProvider, "dir1/file6.txt", currentYear);

      assertCreation(authorProvider, "dir1/file6.txt", currentYear);
      assertCreation(committerProvider, "dir1/file6.txt", currentYear);
    }
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
    try (GitLookup provider = newAuthorLookup()) {
      assertLastChange(provider, "dir1/file1.txt", 2006);
      assertLastChange(provider, "dir2/file2.txt", 2007);
      assertLastChange(provider, "dir1/file3.txt", 2009);
    }
  }

  @Test
  void timezone() throws GitAPIException, IOException {
    // do not fail if a tz is./mv  set, it will just be unused
    // it allows parent poms to pre-defined properties and let sub modules use them if needed
    try (GitLookup gitLookup = GitLookup.create(gitRepoRoot.toFile(), buildProps(DateSource.AUTHOR, "GMT", "10", null))) {
      // do nothing
    }

    /* null is GMT */
    try (GitLookup nullTzLookup = GitLookup.create(gitRepoRoot.toFile(),
        buildProps(DateSource.COMMITER, null, "10", null))) {
      assertLastChange(nullTzLookup, "dir1/file3.txt", 2010);
    }

    /* explicit GMT */
    try (GitLookup gmtLookup = GitLookup.create(gitRepoRoot.toFile(),
        buildProps(DateSource.COMMITER, "GMT", "10", null))) {
      assertLastChange(gmtLookup, "dir1/file3.txt", 2010);
    }

    /*
     * explicit non-GMT zome. Note that the relevant commit's (GMT) time stamp is 2010-12-31T23:30:00 which yealds
     * 2011 in the CET (+01:00) time zone
     */
    try (GitLookup cetLookup = GitLookup.create(gitRepoRoot.toFile(),
        buildProps(DateSource.COMMITER, "CET", "10", null))) {
      assertLastChange(cetLookup, "dir1/file3.txt", 2011);
    }

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

  // Make sure to close after call
  private GitLookup newAuthorLookup() {
    return GitLookup.create(gitRepoRoot.toFile(), buildProps(DateSource.AUTHOR, null, "10", null));
  }

  // Make sure to close after call
  private GitLookup newCommitterLookup() {
    return GitLookup.create(gitRepoRoot.toFile(), buildProps(DateSource.COMMITER, null, "10", null));
  }

  @Test
  void ignoreCommitsInLastChange() throws GitAPIException, IOException {
    try (GitLookup authorProvider = newAuthorLookup("95d52919cbe340dc271cf1f5ec68cf36705bd3a3")) {

      assertLastChange(authorProvider, "dir1/file1.txt", 2004);
      assertLastChange(newCommitterLookup("95d52919cbe340dc271cf1f5ec68cf36705bd3a3"), "dir1/file1.txt", 2004);
    }
  }

  @Test
  void doNotIgnoreCommitsInCreation() throws GitAPIException, IOException {
    try (GitLookup authorProvider = newAuthorLookup("53b44baedc5a378f9b665da12f298e1003793219");
        GitLookup committerProvider = newCommitterLookup("53b44baedc5a378f9b665da12f298e1003793219")) {

      assertCreation(authorProvider, "dir1/file1.txt", 2000);
      assertCreation(committerProvider, "dir1/file1.txt", 2000);
    }
  }

  // Make sure to close after call
  private GitLookup newAuthorLookup(String... commitsToIgnore) throws IOException {
    String commitsToIgnoreCSV = String.join(",", commitsToIgnore);
    return GitLookup.create(gitRepoRoot.toFile(), buildProps(DateSource.AUTHOR, null, "10", commitsToIgnoreCSV));
  }

  // Make sure to close after call
  private GitLookup newCommitterLookup(String... commitsToIgnore) throws IOException {
    String commitsToIgnoreCSV = String.join(",", commitsToIgnore);
    return GitLookup.create(gitRepoRoot.toFile(), buildProps(DateSource.COMMITER, null, "10", commitsToIgnoreCSV));
  }

  private void assertLastChange(GitLookup provider, String relativePath, int expected) throws
      GitAPIException, IOException {
    int actual = provider.getYearOfLastChange(gitRepoRoot.resolve(relativePath.replace('/', File.separatorChar)).toFile());
    Assertions.assertEquals(expected, actual);
  }

  private void assertCreation(GitLookup provider, String relativePath, int expected) throws
      GitAPIException, IOException {
    int actual = provider.getYearOfCreation(gitRepoRoot.resolve(relativePath.replace('/', File.separatorChar)).toFile());
    Assertions.assertEquals(expected, actual);
  }

}
