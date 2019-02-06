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

/**
 * A utility to transform native {@link File} paths to the form expected by jGit - i.e. relative to git working tree
 * root directory and delimited by {@code '/'}.
 *
 * @author <a href="mailto:ppalaga@redhat.com">Peter Palaga</a>
 */
public class GitPathResolver {

    /** The path separator expected by jGit. */
    private static final char CANONICAL_PATH_SEPARATOR = '/';

    /** The file path separator used by the current platform, usually {@value File#separatorChar}. */
    private final char nativePathSeparator;

    /** The native path to the root directory of the current git repository working tree. */
    private final String repositoryRootDir;

    public GitPathResolver(String repositoryRootDir) {
        this(repositoryRootDir, File.separatorChar);
    }

    /**
     * Default visibility for testing purposes.
     *
     * @param repositoryRootDir
     * @param nativePathSeparator
     */
    GitPathResolver(String repositoryRootDir, char nativePathSeparator) {
        super();
        this.repositoryRootDir = repositoryRootDir.charAt(repositoryRootDir.length() - 1) == nativePathSeparator ? repositoryRootDir
                : repositoryRootDir + nativePathSeparator;
        this.nativePathSeparator = nativePathSeparator;
    }

    /**
     * With default visibility to be testable.
     *
     * @param absoluteNativePath
     * @return
     */
    String relativize(String absoluteNativePath) {
        if (!absoluteNativePath.startsWith(repositoryRootDir)) {
            throw new RuntimeException("Cannot relativize path '" + absoluteNativePath + "' to directory '"
                    + repositoryRootDir + "'");
        }
        String result = absoluteNativePath.substring(repositoryRootDir.length());
        if (nativePathSeparator != CANONICAL_PATH_SEPARATOR) {
            result = result.replace(nativePathSeparator, CANONICAL_PATH_SEPARATOR);
        }
        return result;
    }

    /**
     * Return a string representing the supplied {@code path} path relative to git working tree root directory and
     * delimited by {@code '/'}.
     *
     * @param path the path to relativize
     * @return the relativized path
     */
    public String relativize(File path) {
        return relativize(path.getAbsolutePath());
    }

}
