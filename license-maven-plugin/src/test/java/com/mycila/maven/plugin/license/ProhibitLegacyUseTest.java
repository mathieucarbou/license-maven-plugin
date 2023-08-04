/**
 * Copyright (C) 2008 Mycila (mathieu.carbou@gmail.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mycila.maven.plugin.license;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.testing.stubs.MavenProjectStub;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings("deprecation")
public class ProhibitLegacyUseTest {

    @Test
    void test_defaultStubProject() throws Exception {
        LicenseCheckMojo check = new LicenseCheckMojo();
        check.project = new MavenProjectStub();
        check.prohibitLegacyUse = true;
        check.execute();
    }

    @Test
    void test_prohibitLegacyConfigHeader() {
        LicenseCheckMojo check = new LicenseCheckMojo();
        check.project = new MavenProjectStub();
        check.prohibitLegacyUse = true;
        check.legacyConfigHeader = "header";
        assertThatThrownBy(check::execute).satisfies(e -> {
                    assertThat(e).isInstanceOf(MojoExecutionException.class);
                    assertThat(e).hasMessage("Use of legacy parameters has been prohibited by configuration.");
                }
        );
    }

    @Test
    void test_prohibitLegacyConfigInlineHeader() {
        LicenseCheckMojo check = new LicenseCheckMojo();
        check.project = new MavenProjectStub();
        check.prohibitLegacyUse = true;
        check.legacyConfigInlineHeader = "inline header";
        assertThatThrownBy(check::execute).satisfies(e -> {
                    assertThat(e).isInstanceOf(MojoExecutionException.class);
                    assertThat(e).hasMessage("Use of legacy parameters has been prohibited by configuration.");
                }
        );
    }

    @Test
    void test_prohibitLegacyConfigIncludes() {
        LicenseCheckMojo check = new LicenseCheckMojo();
        check.project = new MavenProjectStub();
        check.prohibitLegacyUse = true;
        check.legacyConfigIncludes = new String[]{"include"};
        assertThatThrownBy(check::execute).satisfies(e -> {
                    assertThat(e).isInstanceOf(MojoExecutionException.class);
                    assertThat(e).hasMessage("Use of legacy parameters has been prohibited by configuration.");
                }
        );
    }

    @Test
    void test_prohibitLegacyConfigExcludes() {
        LicenseCheckMojo check = new LicenseCheckMojo();
        check.project = new MavenProjectStub();
        check.prohibitLegacyUse = true;
        check.legacyConfigExcludes = new String[]{"exclude"};
        assertThatThrownBy(check::execute).satisfies(e -> {
                    assertThat(e).isInstanceOf(MojoExecutionException.class);
                    assertThat(e).hasMessage("Use of legacy parameters has been prohibited by configuration.");
                }
        );
    }

    @Test
    void test_prohibitLegacyConfigKeywords() {
        LicenseCheckMojo check = new LicenseCheckMojo();
        check.project = new MavenProjectStub();
        check.prohibitLegacyUse = true;
        check.legacyConfigKeywords = new String[]{"keyword"};
        assertThatThrownBy(check::execute).satisfies(e -> {
                    assertThat(e).isInstanceOf(MojoExecutionException.class);
                    assertThat(e).hasMessage("Use of legacy parameters has been prohibited by configuration.");
                }
        );
    }

    @Test
    void test_allowDefaultLegacyConfigKeywords() {
        LicenseCheckMojo check = new LicenseCheckMojo();
        check.project = new MavenProjectStub();
        check.prohibitLegacyUse = true;
        check.legacyConfigKeywords = new String[]{"copyright"};
        assertThatNoException().isThrownBy(check::execute);
    }

    @Test
    void test_prohibitLegacyConfigMulti() {
        LicenseCheckMojo check = new LicenseCheckMojo();
        check.project = new MavenProjectStub();
        check.prohibitLegacyUse = true;
        check.legacyConfigMulti = new Multi();
        assertThatThrownBy(check::execute).satisfies(e -> {
                    assertThat(e).isInstanceOf(MojoExecutionException.class);
                    assertThat(e).hasMessage("Use of legacy parameters has been prohibited by configuration.");
                }
        );
    }

    @Test
    void test_prohibitLegacyConfigValidHeaders() {
        LicenseCheckMojo check = new LicenseCheckMojo();
        check.project = new MavenProjectStub();
        check.prohibitLegacyUse = true;
        check.legacyConfigValidHeaders = new String[]{"valid", "headers"};
        assertThatThrownBy(check::execute).satisfies(e -> {
                    assertThat(e).isInstanceOf(MojoExecutionException.class);
                    assertThat(e).hasMessage("Use of legacy parameters has been prohibited by configuration.");
                }
        );
    }

    @Test
    void test_prohibitLegacyConfigValidHeaderSections() {
        LicenseCheckMojo check = new LicenseCheckMojo();
        check.project = new MavenProjectStub();
        check.prohibitLegacyUse = true;
        check.legacyConfigHeaderSections = new HeaderSection[]{new HeaderSection()};
        assertThatThrownBy(check::execute).satisfies(e -> {
                    assertThat(e).isInstanceOf(MojoExecutionException.class);
                    assertThat(e).hasMessage("Use of legacy parameters has been prohibited by configuration.");
                }
        );
    }
}
