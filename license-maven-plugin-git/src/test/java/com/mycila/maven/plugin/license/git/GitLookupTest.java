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

import com.mycila.maven.plugin.license.git.GitLookup.DateSource;
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
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * @author <a href="mailto:ppalaga@redhat.com">Peter Palaga</a>
 */
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
          OutputStream out = null;
          try {
            out = new BufferedOutputStream(new FileOutputStream(unzippedFile.toFile()), 2048);
            int len;
            while ((len = zipInputStream.read(buffer)) != -1) {
              out.write(buffer, 0, len);
            }
          } finally {
            if (out != null) {
              out.close();
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
    try {
      GitLookup.create(gitRepoRoot.toFile(), buildProps(DateSource.AUTHOR, "GMT", "10"));
      Assertions.fail("RuntimeException expected");
    } catch (RuntimeException e) {
      if (e.getMessage().contains(
          "license.git.copyrightLastYearTimeZone must not be set with license.git.copyrightLastYearSource = AUTHOR because git author name already contains time zone information.")) {
        /* expected */
      } else {
        throw e;
      }
    }

    /* null is GMT */
    GitLookup nullTzLookup = GitLookup.create(gitRepoRoot.toFile(),
        buildProps(DateSource.COMMITER, null, "10"));
    assertLastChange(nullTzLookup, "dir1/file3.txt", 2010);

    /* explicit GMT */
    GitLookup gmtLookup = GitLookup.create(gitRepoRoot.toFile(),
        buildProps(DateSource.COMMITER, "GMT", "10"));
    assertLastChange(gmtLookup, "dir1/file3.txt", 2010);

    /*
     * explicit non-GMT zome. Note that the relevant commit's (GMT) time stamp is 2010-12-31T23:30:00 which yealds
     * 2011 in the CET (+01:00) time zone
     */
    GitLookup cetLookup = GitLookup.create(gitRepoRoot.toFile(),
        buildProps(DateSource.COMMITER, "CET", "10"));
    assertLastChange(cetLookup, "dir1/file3.txt", 2011);

  }

  private Map<String, String> buildProps(DateSource ds, String tz, String history) {
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
    return props;
  }

  private GitLookup newAuthorLookup() {
    return GitLookup.create(gitRepoRoot.toFile(), buildProps(DateSource.AUTHOR, null, "10"));
  }

  private GitLookup newCommitterLookup() {
    return GitLookup.create(gitRepoRoot.toFile(), buildProps(DateSource.COMMITER, null, "10"));
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
