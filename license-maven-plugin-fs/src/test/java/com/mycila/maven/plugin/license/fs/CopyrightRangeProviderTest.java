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
package com.mycila.maven.plugin.license.fs;

import com.mycila.maven.plugin.license.LicenseCheckMojo;
import com.mycila.maven.plugin.license.document.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

class CopyrightRangeProviderTest {
  private static Path fsRepoRoot;

  @TempDir
  static File tempFolder;

  @Test
  void copyrightRange() {
    Map<String, String> props = new HashMap<>();
    final LicenseCheckMojo mojo = new LicenseCheckMojo();
    mojo.defaultBasedir = fsRepoRoot.toFile();
    try (CopyrightRangeProvider provider = new CopyrightRangeProvider()) {
      provider.init(mojo, props);

      assertRange(provider, "dir1/file1.txt", "2023", "1999-2023");
      assertRange(provider, "dir1/file2.txt", "1999", "1999");

      /* The last change of file3.txt is in 1999
       * but the inception year is 2000
       * and we do not want the range to go back (2000-1999)
       * so in this case we expect just 2000
       * However for existence years always report the actual year regardless
       * of the inception year so expect 1999 for that */
      assertRange(provider, "dir2/file3.txt", "2000", "1999", "2000");
    }
  }

  private void assertRange(CopyrightRangeProvider provider, String path, String copyrightEnd, String copyrightRange) {
    assertRange(provider, path, "1999", copyrightEnd, copyrightRange);
  }

  private void assertRange(CopyrightRangeProvider provider, String path, String inceptionYear, String copyrightEnd, String copyrightRange) {
    Map<String, String> props = new HashMap<>();
    props.put(CopyrightRangeProvider.INCEPTION_YEAR_KEY, inceptionYear);

    Document document = newDocument(path);
    Map<String, String> actual = provider.adjustProperties(new LicenseCheckMojo(), props, document);

    HashMap<String, String> expected = new HashMap<String, String>();
    expected.put(CopyrightRangeProvider.COPYRIGHT_LAST_YEAR_KEY, copyrightEnd);
    expected.put(CopyrightRangeProvider.COPYRIGHT_YEARS_KEY, copyrightRange);
    Assertions.assertEquals(expected, actual, "for file '" + path + "': ");
  }

  private static Document newDocument(String relativePath) {
    Path path = Paths.get(fsRepoRoot + File.separator
        + relativePath.replace('/', File.separatorChar));
    return new Document(path.toFile(), null, StandardCharsets.UTF_8, new String[0], null);
  }

  @BeforeAll
  static void beforeClass() throws IOException {
    fsRepoRoot = Paths.get(tempFolder.toPath() + File.separator + "fs-test-repo");

    Files.createDirectories(fsRepoRoot.resolve("dir1"));
    Files.createFile(fsRepoRoot.resolve("dir1/file1.txt"));
    Files.createFile(fsRepoRoot.resolve("dir1/file2.txt"));

    Files.createDirectory(fsRepoRoot.resolve("dir2"));
    Files.createFile(fsRepoRoot.resolve("dir2/file3.txt"));

    Files.setLastModifiedTime(fsRepoRoot.resolve("dir1/file1.txt"), FileTime.from(Instant.parse("2023-06-10T13:13:13.00Z")));
    Files.setLastModifiedTime(fsRepoRoot.resolve("dir1/file2.txt"), FileTime.from(Instant.parse("1999-06-10T13:13:13.00Z")));
    Files.setLastModifiedTime(fsRepoRoot.resolve("dir2/file3.txt"), FileTime.from(Instant.parse("1999-06-10T13:13:13.00Z")));
  }

}
