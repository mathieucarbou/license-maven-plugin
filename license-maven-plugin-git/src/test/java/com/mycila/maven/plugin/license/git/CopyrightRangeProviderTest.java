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
public class CopyrightRangeProviderTest {

    private static File gitRepoRoot;
    private static TemporaryFolder tempFolder;

    @Test
    public void copyrightRange() {
        CopyrightRangeProvider provider = new CopyrightRangeProvider();

        assertRange(provider, "dir1/file1.txt", "2000", "2006", "1999-2006");
        assertRange(provider, "dir2/file2.txt", "2007", "2007", "1999-2007");
        assertRange(provider, "dir1/file3.txt", "2009", "2009", "1999-2009");
        assertRange(provider, "dir2/file4.txt", "1999", "1999", "1999");

        /* The last change of file4.txt in git history is in 1999
         * but the inception year is 2000
         * and we do not want the range to go back (2000-1999)
         * so in this case we expect just 2000 */
        assertRange(provider, "dir2/file4.txt", "2000", "1999", "1999", "2000");

    }

    private void assertRange(CopyrightRangeProvider provider, String path, String copyrightStart, String copyrightEnd, String copyrightRange) {
        assertRange(provider, path, "1999", copyrightStart, copyrightEnd, copyrightRange);
    }

    private void assertRange(CopyrightRangeProvider provider, String path, String inceptionYear,
            String copyrightStart, String copyrightEnd, String copyrightRange) {
        Properties props = new Properties();
        props.put(CopyrightRangeProvider.INCEPTION_YEAR_KEY, inceptionYear);

        Document document = newDocument(path);
        Map<String, String> actual = provider.getAdditionalProperties(null, props, document);

        HashMap<String, String> expected = new HashMap<String, String>();
        expected.put(CopyrightRangeProvider.COPYRIGHT_CREATION_YEAR_KEY, copyrightStart);
        expected.put(CopyrightRangeProvider.COPYRIGHT_LAST_YEAR_KEY, copyrightEnd);
        expected.put(CopyrightRangeProvider.COPYRIGHT_YEARS_KEY, copyrightRange);
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
