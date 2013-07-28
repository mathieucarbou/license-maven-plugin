/**
 * Copyright (C) 2008 http://code.google.com/p/maven-license-plugin/
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

package com.google.code.mojo.license.util.resource;

import org.apache.maven.plugin.MojoFailureException;
import static org.testng.Assert.*;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.net.URL;
import java.util.Arrays;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
public final class ResourceFinderTest {

    ResourceFinder finder;

    @BeforeClass
    public void setup() {
        finder = new ResourceFinder(new File("."));
        finder.setCompileClassPath(Arrays.asList("src/test/data/compileCP"));
        finder.setPluginClassPath(getClass().getClassLoader());
    }

    @Test
    public void test_load_absolute_file() throws Exception {
        String path = new File("src/test/data/compileCP/test.txt").getCanonicalPath();
        URL u = finder.findResource(path);
        assertEquals(new File(u.toURI()).getCanonicalPath(), path);
    }

    @Test(expectedExceptions = MojoFailureException.class)
    public void test_load_inexisting() throws Exception {
        finder.findResource("ho ho");
    }

    @Test
    public void test_load_relative_file() throws Exception {
        URL u = finder.findResource("src/test/data/compileCP/test.txt");
        assertTrue(u.getPath().contains("src/test/data/compileCP/test.txt"));
    }

    @Test
    public void test_load_from_compile_CP() throws Exception {
        assertNotNull(finder.findResource("test.txt"));
    }

    @Test
    public void test_load_from_plugin_CP() throws Exception {
        assertNotNull(finder.findResource("/bouh.txt"));
    }

    @Test
    public void test_load_from_URL() throws Exception {
        assertNotNull(finder.findResource("file://" + new File(".").toURI().toURL().getPath() + "/src/test/data/compileCP/test.txt"));
    }
}
