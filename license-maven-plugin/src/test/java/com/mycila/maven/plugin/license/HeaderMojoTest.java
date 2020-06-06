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

import com.mycila.maven.plugin.license.util.FileUtils;

import java.io.File;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
public final class HeaderMojoTest {

    @Test
    public void test_create() throws Exception {
        MavenProjectStub project = new MavenProjectStub();
        LicenseCheckMojo check = new LicenseCheckMojo();
        check.defaultBasedir = new File("src/main/resources/check");
        check.project = project;
        check.strictCheck = true;
        check.execute();
    }

    @Test
    public void test_load_header_from_relative_file() throws Exception {
        MavenProjectStub project = new MavenProjectStub();
        LicenseCheckMojo check = new LicenseCheckMojo();
        check.defaultBasedir = new File("src/test/resources/check");
        check.legacyConfigHeader = "header.txt";
        check.project = project;
        check.failIfMissing = false;
        check.strictCheck = true;
        check.execute();
    }

    @Test
    public void test_load_header_from_absolute_file() throws Exception {
        MavenProjectStub project = new MavenProjectStub();
        LicenseCheckMojo check = new LicenseCheckMojo();
        check.defaultBasedir = new File("src/test/resources/check");
        check.legacyConfigHeader = "src/test/resources/check/header.txt";
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
        check.defaultBasedir = new File("src/test/resources/check");
        check.legacyConfigHeader = "header-in-cp.txt";
        check.project = project;
        check.failIfMissing = false;
        check.strictCheck = true;
        check.execute();
    }

    @Test
    public void test_load_header_from_plugin_classpath() throws Exception {
        MavenProjectStub project = new MavenProjectStub();
        LicenseCheckMojo check = new LicenseCheckMojo();
        check.defaultBasedir = new File("src/test/resources/check");
        check.legacyConfigHeader = "test-header1.txt";
        check.project = project;
        check.failIfMissing = false;
        check.strictCheck = true;
        check.execute();
    }

    @Test
    public void test_inlineHeader() throws Exception {
        MavenProjectStub project = new MavenProjectStub();
        LicenseCheckMojo check = new LicenseCheckMojo();
        check.defaultBasedir = new File("src/test/resources/check");
        check.legacyConfigInlineHeader = FileUtils.read(new File("src/test/resources/check/header.txt"), "utf-8");
        check.project = project;
        check.failIfMissing = false;
        check.strictCheck = true;
        check.execute();
    }

    @Test
    public void test_load_multi_headers_from_relative_file() throws Exception {
        final MavenProjectStub project = new MavenProjectStub();
        final LicenseCheckMojo check = new LicenseCheckMojo();
        check.defaultBasedir = new File("src/test/resources/check");
        final Multi multi = new Multi();
        multi.setHeaders(new String[] {"header.txt", "header2.txt"});
        check.legacyConfigMulti = multi;
        check.project = project;
        check.failIfMissing = false;
        check.strictCheck = true;
        check.execute();
    }

    @Test
    public void test_load_multi_headers_from_absolute_file() throws Exception {
        final MavenProjectStub project = new MavenProjectStub();
        final LicenseCheckMojo check = new LicenseCheckMojo();
        check.defaultBasedir = new File("src/test/resources/check");
        final Multi multi = new Multi();
        multi.setHeaders(new String[] {"src/test/resources/check/header.txt", "src/test/resources/check/header2.txt"});
        check.legacyConfigMulti = multi;
        check.project = project;
        check.failIfMissing = false;
        check.strictCheck = true;
        check.execute();
    }

    @Test
    public void test_load_multi_headers_from_project_classpath() throws Exception {
        final MavenProjectStub project = new MavenProjectStub();
        project.addCompileSourceRoot("src/test/resources/check/cp");
        final LicenseCheckMojo check = new LicenseCheckMojo();
        check.defaultBasedir = new File("src/test/resources/check");
        final Multi multi = new Multi();
        multi.setHeaders(new String[] {"header-in-cp.txt", "header-in-cp.txt"});
        check.legacyConfigMulti = multi;
        check.project = project;
        check.failIfMissing = false;
        check.strictCheck = true;
        check.execute();
    }

    @Test
    public void test_load_multi_headers_from_plugin_classpath() throws Exception {
        final MavenProjectStub project = new MavenProjectStub();
        final LicenseCheckMojo check = new LicenseCheckMojo();
        check.defaultBasedir = new File("src/test/resources/check");
        final Multi multi = new Multi();
        multi.setHeaders(new String[] {"test-header1.txt", "test-header2.txt"});
        check.legacyConfigMulti = multi;
        check.project = project;
        check.failIfMissing = false;
        check.strictCheck = true;
        check.execute();
    }

    @Test
    public void test_multi_inlineHeader() throws Exception {
        final MavenProjectStub project = new MavenProjectStub();
        final LicenseCheckMojo check = new LicenseCheckMojo();
        check.defaultBasedir = new File("src/test/resources/check");
        final Multi multi = new Multi();
        multi.setInlineHeaders(new String[] {
                FileUtils.read(new File("src/test/resources/check/header.txt"), "utf-8"),
                FileUtils.read(new File("src/test/resources/check/header2.txt"), "utf-8")
        });
        check.legacyConfigMulti = multi;
        check.project = project;
        check.failIfMissing = false;
        check.strictCheck = true;
        check.execute();
    }


}
