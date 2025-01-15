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

import com.mycila.maven.plugin.license.LicenseCheckMojo;
import com.mycila.maven.plugin.license.document.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

class CopyrightAuthorProviderTest {

  private static Path gitRepoRoot;

  @TempDir
  static File tempFolder;

  @Test
  void copyrightAuthor() {
    Map<String, String> props = new HashMap<>();
    final LicenseCheckMojo mojo = new LicenseCheckMojo();
    mojo.legacyDefaultBasedir = gitRepoRoot.toFile();

    try (CopyrightAuthorProvider provider = new CopyrightAuthorProvider()) {
      provider.init(mojo, props);

      String path = "dir1/file1.txt";

      Document document = newDocument(path);
      Map<String, String> actual = provider.adjustProperties(mojo, props, document);

      HashMap<String, String> expected = new HashMap<String, String>();
      expected.put(CopyrightAuthorProvider.COPYRIGHT_CREATION_AUTHOR_NAME_KEY, "Peter Palaga");
      expected.put(CopyrightAuthorProvider.COPYRIGHT_CREATION_AUTHOR_EMAIL_KEY,
          "ppalaga@redhat.com");
      Assertions.assertEquals(expected, actual, "for file '" + path + "': ");

    }
  }

  private static Document newDocument(String relativePath) {
    Path path = Path.of(gitRepoRoot + File.separator
        + relativePath.replace('/', File.separatorChar));
    return new Document(path.toFile(), null, StandardCharsets.UTF_8, new String[0], null);
  }

  @BeforeAll
  static void beforeClass() throws IOException {
    URL url = CopyrightAuthorProviderTest.class.getResource("git-test-repo.zip");
    gitRepoRoot = Path.of(tempFolder.toPath() + File.separator + "git-test-repo");

    GitLookupTest.unzip(url, tempFolder.toPath());
  }

}
