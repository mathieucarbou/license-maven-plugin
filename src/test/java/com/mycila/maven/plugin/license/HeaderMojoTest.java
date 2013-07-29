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

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
public final class HeaderMojoTest {

    @Test
    public void test_create() throws Exception {
        MavenProjectStub project = new MavenProjectStub();
        LicenseCheckMojo check = new LicenseCheckMojo();
        check.basedir = new File("src/main/resources/check");
        check.project = project;
        check.strictCheck = true;
        check.execute();
    }

    @Test
    public void test_load_header_from_relative_file() throws Exception {
        MavenProjectStub project = new MavenProjectStub();
        LicenseCheckMojo check = new LicenseCheckMojo();
        check.basedir = new File("src/test/resources/check");
        check.header = "header.txt";
        check.project = project;
        check.failIfMissing = false;
        check.strictCheck = true;
        check.execute();
    }

    @Test
    public void test_load_header_from_absolute_file() throws Exception {
        MavenProjectStub project = new MavenProjectStub();
        LicenseCheckMojo check = new LicenseCheckMojo();
        check.basedir = new File("src/test/resources/check");
        check.header = "src/test/resources/check/header.txt";
        check.project = project;
        check.failIfMissing = false;
        check.strictCheck = true;
        check.execute();
    }

    @Test
    public void test_load_header_from_project_classpath() throws Exception {
        MavenProjectStub project = new MavenProjectStub();
        project.addCompileSourceRoot("src/test/resources/check/cp");
        LicenseCheckMojo check = new LicenseCheckMojo();
        check.basedir = new File("src/test/resources/check");
        check.header = "header-in-cp.txt";
        check.project = project;
        check.failIfMissing = false;
        check.strictCheck = true;
        check.execute();
    }

    @Test
    public void test_load_header_from_plugin_classpath() throws Exception {
        MavenProjectStub project = new MavenProjectStub();
        LicenseCheckMojo check = new LicenseCheckMojo();
        check.basedir = new File("src/test/resources/check");
        check.header = "test-header1.txt";
        check.project = project;
        check.failIfMissing = false;
        check.strictCheck = true;
        check.execute();
    }

}
