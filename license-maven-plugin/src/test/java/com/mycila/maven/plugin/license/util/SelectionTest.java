/**
 * Copyright (C) 2008 Mycila (mathieu.carbou@gmail.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mycila.maven.plugin.license.util;

import com.mycila.maven.plugin.license.Default;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.apache.maven.shared.utils.io.DirectoryScanner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static java.util.Arrays.asList;

class SelectionTest {
  private final Log log = new SystemStreamLog();

  @Test
  void test_default_select_all() {
    Selection selection = new Selection(new File("."), new String[0], new String[0], false, log);
    Assertions.assertEquals(0, selection.getExcluded().length);
    Assertions.assertEquals(1, selection.getIncluded().length);
    Assertions.assertTrue(selection.getSelectedFiles().length > 0);
  }

  @Test
  void test_limit_inclusion() {
    Selection selection = new Selection(new File("."), new String[]{"toto"}, new String[]{"tata"}, false, log);
    Assertions.assertEquals(1, selection.getExcluded().length);
    Assertions.assertEquals(1, selection.getIncluded().length);
    Assertions.assertEquals(0, selection.getSelectedFiles().length);
  }

  @Test
  void test_limit_inclusion_and_check_default_excludes() {
    Selection selection = new Selection(new File("."), new String[]{"toto"}, new String[0], true, log);
    Assertions.assertEquals(selection.getExcluded().length, Default.EXCLUDES.length); // default excludes from Scanner and Selection + toto
    Assertions.assertEquals(1, selection.getIncluded().length);
    Assertions.assertEquals(0, selection.getSelectedFiles().length);
    Assertions.assertTrue(Arrays.asList(selection.getExcluded()).containsAll(Arrays.asList(Default.EXCLUDES)));
  }

  @Test
  void test_exclusions_respect_with_fastScan() throws IOException {
    SystemStreamLog log = new SystemStreamLog() {
      @Override
      public boolean isDebugEnabled() {
        return true;
      }
    };
    File root = createAFakeProject(log);
    Selection selection = new Selection(root,
        new String[]{"**" + File.separator + "*.txt"},
        new String[]{"target" + File.separator + "**", "module" + File.separator + "**" + File.separator + "target" + File.separator + "**"}, false,
        log);

    selection.getSelectedFiles(); // triggers scan and scanner build
    String debugMessage = buildDebugMessage(selection.getScanner());
    assertIncludedFilesInFakeProject(selection, debugMessage);
    Assertions.assertEquals(0, selection.getScanner().getExcludedFiles().length, debugMessage);
  }

  private String buildDebugMessage(DirectoryScanner scanner) {
    return "excludedDirs=" + asList(scanner.getExcludedDirectories()) + ",\n" +
        "excludedFiles=" + asList(scanner.getExcludedFiles()) + ",\n" +
        "includedDirsFiles=" + asList(scanner.getIncludedDirectories()) + ",\n" +
        "includedFiles=" + asList(scanner.getIncludedFiles()) + ",\n" +
        "notIncludedDirs=" + asList(scanner.getNotIncludedDirectories()) + ",\n" +
        "notIncludedFiles=" + asList(scanner.getNotIncludedFiles()) + ",\n" +
        "diskFiles=" + listFiles(scanner.getBasedir(), new ArrayList<File>());
  }

  private Collection<File> listFiles(File basedir, Collection<File> files) {
    files.add(basedir);
    for (File f : basedir.listFiles()) {
      if (f.isDirectory()) {
        listFiles(f, files);
      } else {
        files.add(f);
      }
    }
    return files;
  }

  private void assertIncludedFilesInFakeProject(Selection selection, String debugMessage) {
    List<String> selected = new ArrayList<String>(asList(selection.getSelectedFiles()));
    Collections.sort(selected);
    Assertions.assertEquals(asList("included.txt", "module" + File.separator + "src" + File.separator + "main" + File.separator + "java" + File.separator + "not-ignored.txt", "module" + File.separator + "sub" + File.separator + "subsub" + File.separator + "src" + File.separator + "main" + File.separator + "java" + File.separator + "not-ignored.txt"), selected, debugMessage);
  }

  private File createAFakeProject(Log log) throws IOException {
    File temp = new File("target/workdir_" + UUID.randomUUID().toString());
    touch(new File(temp, "included.txt"), log);
    touch(new File(temp, "target/ignored.txt"), log);
    touch(new File(temp, "module/src/main/java/not-ignored.txt"), log);
    touch(new File(temp, "module/target/ignored.txt"), log);
    touch(new File(temp, "module/sub/subsub/src/main/java/not-ignored.txt"), log);
    touch(new File(temp, "module/sub/subsub/target/foo/not-ignored.txt"), log);
    return temp;
  }

  private void touch(File newFile, Log log) throws IOException {
    final File parentFile = newFile.getParentFile();
    if (parentFile != null && !parentFile.isDirectory() && !parentFile.mkdirs()) {
      Assertions.fail("Can't create '" + parentFile + "'");
    }
    final FileWriter w = new FileWriter(newFile);
    w.write("touched");
    w.close();
    if (!newFile.exists()) {
      Assertions.fail("Can't create " + newFile);
    }
    log.debug("Created '" + newFile.getAbsolutePath() + "'");
  }
}
