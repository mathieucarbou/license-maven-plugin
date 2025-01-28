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

import org.apache.maven.model.License;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class LicensePolicyEnforcerResultTest extends ArtifactLicensePolicyEnforcerTestBase {

  @Test
  void test_toString() {
    final String description = "The printable toString of a LicensePolicy should be useful/legible.";

    final License license = getLicense("my license");
    license.setUrl("https://localhost/mylicense");

    final LicensePolicy policy = new LicensePolicy(LicensePolicy.Rule.DENY, LicensePolicy.Type.LICENSE_URL, license.getUrl());
    final LicensePolicyEnforcerResult result = new LicensePolicyEnforcerResult(policy, license, getArtifact("com.example:foobar:jar:1.2.3"), LicensePolicy.Rule.DENY);
    final String expected = "com.example:foobar:jar:1.2.3:runtime [my license] LICENSE_URL:DENY:https://localhost/mylicense";

    Assertions.assertEquals(expected, result.toString(), description);
  }

  @Test
  void test_equals() {
    final String description = "To results with identical fields should be equal.";

    final License license = getLicense("");

    final LicensePolicy policy = new LicensePolicy(LicensePolicy.Rule.DENY, LicensePolicy.Type.LICENSE_NAME, license.getName());
    final LicensePolicyEnforcerResult resultOne = new LicensePolicyEnforcerResult(policy, license, getArtifact("com.example:foobar:jar:1.2.3"), LicensePolicy.Rule.DENY);
    final LicensePolicyEnforcerResult resultTwo = new LicensePolicyEnforcerResult(policy, license, getArtifact("com.example:foobar:jar:1.2.3"), LicensePolicy.Rule.DENY);

    Assertions.assertEquals(resultOne, resultTwo, description);
  }
}
