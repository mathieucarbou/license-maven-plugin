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
package com.mycila.maven.plugin.license.util.resource;

import org.apache.maven.plugin.MojoFailureException;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * <b>Date:</b> 26-Feb-2008<br>
 * <b>Author:</b> Mathieu Carbou (mathieu.carbou@gmail.com)
 */
public final class ResourceFinder {
  private final Path basedir;
  private CustomClassLoader compileClassPath;
  private CustomClassLoader pluginClassPath;

  public ResourceFinder(final Path basedir) {
    this.basedir = basedir;
  }

  public void setCompileClassPath(List<String> classpath) {
    compileClassPath = new CustomClassLoader();
    if (classpath != null) {
      for (String absolutePath : classpath) {
        compileClassPath.addFolder(absolutePath);
      }
    }
  }

  public void setPluginClassPath(ClassLoader classLoader) {
    pluginClassPath = new CustomClassLoader(classLoader);
  }

  /**
   * Find a resource by searching:
   * 1. In the filesystem, relative to basedir
   * 2. In the filesystem, as an absolute path (or relative to current execution directory)
   * 3. In project classpath
   * 4. In plugin classpath
   * 5. As a URL
   *
   * @param resource The resource to get
   * @return A valid URL
   * @throws MojoFailureException If the resource is not found
   */
  public URL findResource(String resource) throws MojoFailureException {
    URL res = null;

    // first search relatively to the base directory
    try {
      final Path p = basedir.resolve(resource);
      res = toURL(p.toAbsolutePath());
    } catch (final InvalidPathException e) {
      // no-op - can be caused by resource being a URI on windows when Path.resolve is called
    }
    if (res != null) {
      return res;
    }

    // if not found, search for absolute location on file system, or relative to execution dir
    try {
      res = toURL(Paths.get(resource));
    } catch (final InvalidPathException e) {
      // no-op - can be caused by resource being a URI on windows when Paths.get is called
    }
    if (res != null) {
      return res;
    }

    // if not found, try the classpaths
    final String cpResource = resource.startsWith("/") ? resource.substring(1) : resource;

    // tries compile claspath of project
    res = compileClassPath.getResource(cpResource);
    if (res != null) {
      return res;
    }

    // tries this plugin classpath
    res = pluginClassPath.getResource(cpResource);
    if (res != null) {
      return res;
    }

    // otherwise, tries to return a valid URL
    try {
      res = new URL(resource);
      res.openStream().close();
      return res;
    } catch (Exception e) {
      throw new MojoFailureException("Resource " + resource + " not found in file system, classpath or URL: " + e.getMessage(), e);
    }
  }

  private URL toURL(final Path path) {
    if (Files.exists(path) && Files.isReadable(path)) {
      try {
        return path.toUri().toURL();
      } catch (MalformedURLException e) {
      }
    }
    return null;
  }

}
