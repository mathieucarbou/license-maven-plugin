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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.mycila.maven.plugin.license.document.Document;

/**
 * @author <a href="mailto:ppalaga@redhat.com">Peter Palaga</a>
 */
public class CopyrightAuthorProviderTest {

    private static File gitRepoRoot;
    private static TemporaryFolder tempFolder;

    @Test
    public void copyrightAuthor() {
        CopyrightAuthorProvider provider = new CopyrightAuthorProvider();

        assertAuthor(provider, "dir1/file1.txt", "Peter Palaga", "ppalaga@redhat.com");
    }

    private void assertAuthor(CopyrightAuthorProvider provider, String path, String copyrightAuthorName, String copyrightAuthorEmail) {
        Properties props = new Properties();

        Document document = newDocument(path);
        Map<String, String> actual = provider.getAdditionalProperties(null, props, document);

        HashMap<String, String> expected = new HashMap<String, String>();
        expected.put(CopyrightAuthorProvider.COPYRIGHT_CREATION_AUTHOR_NAME_KEY,  copyrightAuthorName);
        expected.put(CopyrightAuthorProvider.COPYRIGHT_CREATION_AUTHOR_EMAIL_KEY, copyrightAuthorEmail);
        Assert.assertEquals("for file '" + path + "': ", expected, actual);

    }

    private static Document newDocument(String relativePath) {
        File file = new File(gitRepoRoot.getAbsolutePath() + File.separatorChar
                + relativePath.replace('/', File.separatorChar));
        return new Document(file, null, "utf-8", new String[0], null);
    }

    @BeforeClass
    public static void beforeClass() throws IOException {
        tempFolder = new TemporaryFolder();
        tempFolder.create();

        URL url = GitLookupTest.class.getResource("git-test-repo.zip");
        File unzipDestination = tempFolder.getRoot();
        gitRepoRoot = new File(unzipDestination, "git-test-repo");

        GitLookupTest.unzip(url, unzipDestination);
    }

    @AfterClass
    public static void afterClass() {
        tempFolder.delete();
    }

}
