/**
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
package com.mycila.maven.plugin.license.util.resource;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
final class CustomClassLoader extends URLClassLoader {
    CustomClassLoader() {
        super(new URL[0], null);
    }

    CustomClassLoader(ClassLoader parent) {
        super(new URL[0], parent);
    }

    public void addFolder(String absolutePath) {
        addFolder(new File(absolutePath));
    }

    public void addFolder(File folder) {
        if (folder.isDirectory()) {
            try {
                super.addURL(folder.toURI().toURL());
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException(e.getMessage(), e);
            }
        }
    }
}
