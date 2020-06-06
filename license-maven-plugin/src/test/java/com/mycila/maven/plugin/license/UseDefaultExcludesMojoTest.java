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

import org.apache.maven.plugin.testing.stubs.MavenProjectStub;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
public final class UseDefaultExcludesMojoTest {
    @Test
    public void test_include_and_fail() throws Exception {
        try {
            LicenseCheckMojo check = new LicenseCheckMojo();
            check.defaultBasedir = new File("src/test/resources/check");
            check.legacyConfigHeader = "header.txt";
            check.project = new MavenProjectStub();
            check.legacyConfigExcludes = new String[]{"doc1.txt"};
            check.defaultUseDefaultExcludes = false;
            check.strictCheck = true;
            check.execute();
        } catch (Exception e) {
            assertEquals("Some files do not have the expected license header", e.getMessage());
        }
    }
    
    @Test
    public void check_defaultExcludes_exclude_Netbeans_Configuration() throws Exception {
        LicenseCheckMojo check = new LicenseCheckMojo();
        check.defaultBasedir = new File("src/test/resources/excludes/issue-68");
        check.legacyConfigHeader = "header.txt";
        check.project = new MavenProjectStub();
        check.defaultUseDefaultExcludes = true;
        check.strictCheck = true;
        check.execute();
    }
    
    @Test
    public void check_withoutDefaultExcludes_Netbeans_Configuration_Is_Reported() {
        try {
            LicenseCheckMojo check = new LicenseCheckMojo();
            check.defaultBasedir = new File("src/test/resources/excludes/issue-68");
            check.legacyConfigHeader = "header.txt";
            check.project = new MavenProjectStub();
            check.defaultUseDefaultExcludes = false;
            check.strictCheck = true;
            check.execute();
        } catch (Exception e) {
            assertEquals("Some files do not have the expected license header", e.getMessage());
        }
    }
}