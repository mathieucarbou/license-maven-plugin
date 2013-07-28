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

package com.google.code.mojo.license;

import org.apache.maven.monitor.logging.DefaultLog;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.testing.stubs.MavenProjectStub;
import org.testng.annotations.Test;

import java.io.File;
import java.util.HashMap;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
public final class MappingMojoTest {
    @Test
    public void test_mapping() throws Exception {
        LicenseCheckMojo check = new LicenseCheckMojo();
        MockedLog logger = new MockedLog();
        check.setLog(new DefaultLog(logger));
        check.basedir = new File("src/test/resources/check");
        check.header = "header.txt";
        check.project = new MavenProjectStub();
        check.useDefaultMapping = true;
        check.strictCheck = true;

        try {
            check.execute();
            fail();
        } catch (MojoExecutionException e) {
            e.printStackTrace(System.out);
            //assertFalse(logger.getContent().contains("header style: javadoc_style"));
            //assertTrue(logger.getContent().contains("header style: text"));
            assertEquals("Some files do not have the expected license header", e.getMessage());
        }

        logger.clear();
        check.mapping = new HashMap<String, String>() {{
            put("txt", "javadoc_style");
        }};

        try {
            check.execute();
            fail();
        } catch (MojoExecutionException e) {
            assertTrue(logger.getContent().contains("header style: javadoc_style"));
            assertFalse(logger.getContent().contains("header style: text"));
            assertEquals("Some files do not have the expected license header", e.getMessage());
        }
    }
}