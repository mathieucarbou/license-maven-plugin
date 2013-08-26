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
package com.mycila.maven.plugin.license;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.testing.stubs.MavenProjectStub;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
public final class AggregateMojoTest {
    @Test
    public void test_modules_ignored() throws Exception {
        MavenProjectStub project = new MavenProjectStub() {
            @Override
            public List<String> getModules() {
                return Arrays.<String>asList("module1", "module2", "module3");
            }
        };
        LicenseCheckMojo check = new LicenseCheckMojo();
        check.basedir = new File("src/test/resources/check/modules");
        check.header = "header.txt";
        check.project = project;
        check.strictCheck = true;
        check.execute();
    }

    @Test
    public void test_modules_scanned() throws Exception {
        MavenProjectStub project = new MavenProjectStub() {
            @Override
            public List<String> getModules() {
                return Arrays.<String>asList("module1", "module2", "module3");
            }
        };
        LicenseCheckMojo check = new LicenseCheckMojo();
        check.project = project;
        check.basedir = new File("src/test/resources/check/modules");
        check.header = "header.txt";
        check.aggregate = true;
        check.strictCheck = true;
        try {
            check.execute();
            fail();
        } catch (MojoExecutionException e) {
            assertEquals("Some files do not have the expected license header", e.getMessage());
        }
    }
}