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
package com.mycila.maven.plugin.license.dependencies;

import java.net.MalformedURLException;
import java.net.URL;
import org.apache.maven.model.License;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class LicenseURLLicensePolicyEnforcerTest extends ArtifactLicensePolicyEnforcerTestBase {
  License license;
  LicensePolicy policy;
  LicenseURLLicensePolicyEnforcer enforcer;

  @Test
  void test_explicitLicenseAllowed() throws MalformedURLException {
    final String description = "An inclusion pattern explicitely matching with an allow rule should be allowed.";
    final String pattern = "https://www.apache.org/licenses/LICENSE-2.0.txt";

    license = getLicense(new URL(pattern));
    policy = new LicensePolicy(LicensePolicy.Rule.APPROVE, LicensePolicy.Type.LICENSE_NAME, pattern);
    enforcer = new LicenseURLLicensePolicyEnforcer(policy);

    Assertions.assertTrue(enforcer.apply(license), description);
  }

  @Test
  void test_nullDeny() {
    final String description = "A missing license attribute should never match an allow rule.";
    final String pattern = "https://www.apache.org/licenses/LICENSE-2.0.txt";

    license = new License();
    policy = new LicensePolicy(LicensePolicy.Rule.APPROVE, LicensePolicy.Type.LICENSE_NAME, pattern);
    enforcer = new LicenseURLLicensePolicyEnforcer(policy);

    Assertions.assertFalse(enforcer.apply(license), description);
  }

  @Test
  void test_defaultDeny() throws MalformedURLException {
    final String description = "An empty pattern should default deny unmatched license URLs.";

    license = getLicense(new URL("https://localhost/my-license"));
    policy = new LicensePolicy(LicensePolicy.Rule.APPROVE, LicensePolicy.Type.LICENSE_NAME, null);
    enforcer = new LicenseURLLicensePolicyEnforcer(policy);

    Assertions.assertFalse(enforcer.apply(license), description);
  }

  @Test
  void test_explicitLicenseUnmatchedAllowed() throws MalformedURLException {
    final String description = "An explicit allow rule should deny unmatched license URLs.";

    license = getLicense(new URL("https://localhost/my-license"));
    policy = new LicensePolicy(LicensePolicy.Rule.APPROVE, LicensePolicy.Type.LICENSE_NAME, "https://www.apache.org/licenses/LICENSE-2.0.txt");
    enforcer = new LicenseURLLicensePolicyEnforcer(policy);

    Assertions.assertFalse(enforcer.apply(license), description);
  }
}
