/*
 * Copyright (C) 2008 Mycila (mathieu.carbou@gmail.com)
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

import static org.junit.Assert.assertEquals;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.mycila.maven.plugin.license.git.GitLookup.DateSource;

/**
 * @author <a href="mailto:ppalaga@redhat.com">Peter Palaga</a>
 */
public class GitLookupTest {

    private static File gitRepoRoot;
    private static TemporaryFolder tempFolder;

    @BeforeClass
    public static void beforeClass() throws IOException {
        tempFolder = new TemporaryFolder();
        tempFolder.create();

        URL url = GitLookupTest.class.getResource("git-test-repo.zip");
        File unzipDestination = tempFolder.getRoot();
        gitRepoRoot = new File(unzipDestination, "git-test-repo");

        unzip(url, unzipDestination);
    }

    static void unzip(URL url, File unzipDestination) throws IOException {
        ZipInputStream zipInputStream = null;
        try {
            zipInputStream = new ZipInputStream(new BufferedInputStream(url.openStream()));
            ZipEntry entry;
            byte[] buffer = new byte[2048];
            while ((entry = zipInputStream.getNextEntry()) != null) {

                String fileName = entry.getName();
                File unzippedFile = new File(unzipDestination.getAbsolutePath() + File.separatorChar + fileName);
                if (entry.isDirectory()) {
                    unzippedFile.mkdirs();
                } else {
                    unzippedFile.getParentFile().mkdirs();
                    OutputStream out = null;
                    try {
                        out = new BufferedOutputStream(new FileOutputStream(unzippedFile), 2048);
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

    @AfterClass
    public static void afterClass() {
        tempFolder.delete();
    }

    @Test
    public void modified() throws GitAPIException, IOException {
        assertLastChange(newAuthorLookup(), "dir1/file1.txt", 2006);
        assertLastChange(newCommitterLookup(), "dir1/file1.txt", 2006);

        assertCreation(newAuthorLookup(), "dir1/file1.txt", 2000);
        assertCreation(newCommitterLookup(), "dir1/file1.txt", 2000);
    }

    @Test
    public void justCreated() throws GitAPIException, IOException {
        assertLastChange(newAuthorLookup(), "dir2/file2.txt", 2007);
        assertLastChange(newCommitterLookup(), "dir2/file2.txt", 2007);

        assertCreation(newAuthorLookup(), "dir2/file2.txt", 2007);
        assertCreation(newCommitterLookup(), "dir2/file2.txt", 2007);
    }

    @Test
    public void moved() throws GitAPIException, IOException {
        assertLastChange(newAuthorLookup(), "dir1/file3.txt", 2009);
        assertLastChange(newCommitterLookup(), "dir1/file3.txt", 2010);

        // In this case the file moved and its creation data could not be tracked
        assertCreation(newAuthorLookup(), "dir1/file3.txt", 2009);
        assertCreation(newCommitterLookup(), "dir1/file3.txt", 2010);
    }

    @Test
    public void newUnstaged() throws GitAPIException, IOException {
        int currentYear = getCurrentGmtYear();
        assertLastChange(newAuthorLookup(), "dir1/file5.txt", currentYear);
        assertLastChange(newCommitterLookup(), "dir1/file5.txt", currentYear);

        assertCreation(newAuthorLookup(), "dir1/file5.txt", currentYear);
        assertCreation(newCommitterLookup(), "dir1/file5.txt", currentYear);
    }

    @Test
    public void newStaged() throws GitAPIException, IOException {
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
    public void reuseProvider() throws GitAPIException, IOException {
        GitLookup provider = newAuthorLookup();
        assertLastChange(provider, "dir1/file1.txt", 2006);
        assertLastChange(provider, "dir2/file2.txt", 2007);
        assertLastChange(provider, "dir1/file3.txt", 2009);
    }

    @Test
    public void timezone() throws GitAPIException, IOException {
        try {
            new GitLookup(gitRepoRoot, DateSource.AUTHOR, TimeZone.getTimeZone("GMT"), 10);
            Assert.fail("RuntimeException expected");
        } catch (RuntimeException e) {
            if (e.getMessage().startsWith("Time zone must be null with dateSource " + DateSource.AUTHOR.name() + "")) {
                /* expected */
            } else {
                throw e;
            }
        }

        /* null is GMT */
        GitLookup nullTzLookup = new GitLookup(gitRepoRoot, DateSource.COMMITER, null, 10);
        assertLastChange(nullTzLookup, "dir1/file3.txt", 2010);

        /* explicit GMT */
        GitLookup gmtLookup = new GitLookup(gitRepoRoot, DateSource.COMMITER, TimeZone.getTimeZone("GMT"), 10);
        assertLastChange(gmtLookup, "dir1/file3.txt", 2010);

        /*
         * explicit non-GMT zome. Note that the relevant commit's (GMT) time stamp is 2010-12-31T23:30:00 which yealds
         * 2011 in the CET (+01:00) time zone
         */
        GitLookup cetLookup = new GitLookup(gitRepoRoot, DateSource.COMMITER, TimeZone.getTimeZone("CET"), 10);
        assertLastChange(cetLookup, "dir1/file3.txt", 2011);

    }

    private GitLookup newAuthorLookup() throws IOException {
        return new GitLookup(gitRepoRoot, DateSource.AUTHOR, null, 10);
    }

    private GitLookup newCommitterLookup() throws IOException {
        return new GitLookup(gitRepoRoot, DateSource.COMMITER, null, 10);
    }

    private void assertLastChange(GitLookup provider, String relativePath, int expected) throws
            GitAPIException, IOException {
        int actual = provider.getYearOfLastChange(new File(gitRepoRoot.getAbsolutePath() + File.separatorChar
                + relativePath.replace('/', File.separatorChar)));
        assertEquals(expected, actual);
    }

    private void assertCreation(GitLookup provider, String relativePath, int expected) throws
            GitAPIException, IOException {
        int actual = provider.getYearOfCreation(new File(gitRepoRoot.getAbsolutePath() + File.separatorChar
                + relativePath.replace('/', File.separatorChar)));
        assertEquals(expected, actual);
    }

}
