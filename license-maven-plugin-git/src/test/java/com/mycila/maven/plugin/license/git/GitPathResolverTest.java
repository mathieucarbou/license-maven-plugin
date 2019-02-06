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
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Test;

import com.mycila.maven.plugin.license.git.GitPathResolver;

/**
 * @author <a href="mailto:ppalaga@redhat.com">Peter Palaga</a>
 */
public class GitPathResolverTest {
    @Test
    public void relativize() throws Exception {
        /* *nix-like */
        assertRelativized("/path/to/my/git/repo", '/', "/path/to/my/git/repo/dir/file.txt", "dir/file.txt");
        assertRelativized("/path/to/my/git/repo/", '/', "/path/to/my/git/repo/dir/file.txt", "dir/file.txt");

        assertFail("/path/to/my/git/repo/", '/', "/path/to/other/git/repo/dir/file.txt");

        /* Windows */
        assertRelativized("c:\\path\\to\\my\\repo", '\\', "c:\\path\\to\\my\\repo\\dir\\file.txt", "dir/file.txt");
        assertRelativized("c:\\path\\to\\my\\repo\\", '\\', "c:\\path\\to\\my\\repo\\dir\\file.txt", "dir/file.txt");
        assertRelativized("\\\\path\\to\\my\\repo", '\\', "\\\\path\\to\\my\\repo\\dir\\file.txt", "dir/file.txt");
        assertRelativized("\\\\path\\to\\my\\repo\\", '\\', "\\\\path\\to\\my\\repo\\dir\\file.txt", "dir/file.txt");

        assertFail("c:\\path\\to\\my\\repo\\", '\\', "c:\\path\\to\\other\\repo\\dir\\file.txt");

    }

    private void assertRelativized(String repoRoot, char nativePathSepartor, String absoluteNativePath, String expected) {
        GitPathResolver resolver = new GitPathResolver(repoRoot, nativePathSepartor);
        String actual = resolver.relativize(absoluteNativePath);
        assertEquals(expected, actual);
    }

    private void assertFail(String repoRoot, char nativePathSepartor, String absoluteNativePath) {
        GitPathResolver resolver = new GitPathResolver(repoRoot, nativePathSepartor);
        try {
            resolver.relativize(absoluteNativePath);
            Assert.fail("RuntimeException expected");
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().startsWith("Cannot relativize"));
        }
    }

}
