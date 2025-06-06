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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class LicensePolicyTest {

  @Test
  void test_toString() {
    final String description = "The printable toString of a LicensePolicy should be useful/legible.";

    final LicensePolicy policy = new LicensePolicy(LicensePolicy.Rule.APPROVE, LicensePolicy.Type.LICENSE_NAME, "example");
    final String expected = "LICENSE_NAME:APPROVE:example";

    Assertions.assertEquals(expected, policy.toString(), description);
  }

  @Test
  void test_equals() {
    final String description = "Two policies with identical fields should be equal.";

    final LicensePolicy policyOne = new LicensePolicy(LicensePolicy.Rule.APPROVE, LicensePolicy.Type.LICENSE_NAME, "example");
    final LicensePolicy policyTwo = new LicensePolicy(LicensePolicy.Rule.APPROVE, LicensePolicy.Type.LICENSE_NAME, "example");

    Assertions.assertEquals(policyOne, policyTwo, description);
  }
}
