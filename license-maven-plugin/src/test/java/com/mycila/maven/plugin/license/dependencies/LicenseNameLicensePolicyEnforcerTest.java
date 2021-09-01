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
package com.mycila.maven.plugin.license.dependencies;

import static org.junit.Assert.assertEquals;

import org.apache.maven.model.License;
import org.junit.Test;

public final class LicenseNameLicensePolicyEnforcerTest extends ArtifactLicensePolicyEnforcerTestBase {
	License license;
	LicensePolicy policy;
	LicenseNameLicensePolicyEnforcer enforcer;

	@Test
	public void test_explicitLicenseAllowed() {
		final String description = "An inclusion pattern explicitely matching with an allow rule should be allowed.";
		final String pattern = "MIT";

		license = getLicense(pattern);
		policy = new LicensePolicy(LicensePolicy.Rule.APPROVE, LicensePolicy.Type.LICENSE_NAME, pattern);
		enforcer = new LicenseNameLicensePolicyEnforcer(policy);

		assertEquals(description, true, enforcer.apply(license));
	}
	
	@Test
	public void test_nullDeny() {
		final String description = "A missing license attribute should never match an allow rule.";
		final String pattern = "MIT";

		license = new License();
		policy = new LicensePolicy(LicensePolicy.Rule.APPROVE, LicensePolicy.Type.LICENSE_NAME, pattern);
		enforcer = new LicenseNameLicensePolicyEnforcer(policy);

		assertEquals(description, false, enforcer.apply(license));
	}

	@Test
	public void test_defaultDeny() {
		final String description = "An empty pattern should default deny unmatched license names.";

		license = getLicense("MIT");
		policy = new LicensePolicy(LicensePolicy.Rule.APPROVE, LicensePolicy.Type.LICENSE_NAME, null);
		enforcer = new LicenseNameLicensePolicyEnforcer(policy);

		assertEquals(description, false, enforcer.apply(license));
	}

	@Test
	public void test_explicitLicenseUnmatchedAllowed() {
		final String description = "An explicit allow rule should deny unmatched license names.";

		license = getLicense("something like an MIT license");
		policy = new LicensePolicy(LicensePolicy.Rule.APPROVE, LicensePolicy.Type.LICENSE_NAME, "MIT");
		enforcer = new LicenseNameLicensePolicyEnforcer(policy);

		assertEquals(description, false, enforcer.apply(license));
	}
}