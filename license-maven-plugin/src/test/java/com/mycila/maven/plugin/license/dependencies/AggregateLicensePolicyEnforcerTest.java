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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.maven.artifact.Artifact;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AggregateLicensePolicyEnforcerTest extends ArtifactLicensePolicyEnforcerTestBase {
  LicenseMapData data;
  Set<LicensePolicy> policies;
  AggregateLicensePolicyEnforcer enforcer;
  LicensePolicy defaultPolicy;

  @BeforeEach
  void setUp() {
    data = new LicenseMapData();
    policies = new HashSet<LicensePolicy>();
    defaultPolicy = new DefaultLicensePolicyEnforcer().getPolicy();
  }

  @Test
  void test_defaultDeny() {
    final String description = "All artifacts should be denied by default.";

    data.put("com.example:example:jar:1.0.0", "", defaultPolicy, false);
    enforcer = new AggregateLicensePolicyEnforcer(policies);

    Map<Artifact, LicensePolicyEnforcerResult> expected = data.getExpected();
    Map<Artifact, LicensePolicyEnforcerResult> actual = enforcer.apply(data.getLicenseMap());

    Assertions.assertEquals(expected, actual, description);
  }

  @Test
  void test_allowedLicense() {
    final String description = "Artifacts with matching allowed licenses should be allowed, while unmatching should not.";
    final String license = "MIT";
    final LicensePolicy policy = new LicensePolicy(LicensePolicy.Rule.APPROVE, LicensePolicy.Type.LICENSE_NAME, license);

    data.put("com.example.allowed:boo:jar:1.0.0", license, policy, true);
    data.put("io.example:foobar:jar:2.1.0", license, policy, true);
    data.put("com.example.denied:evil:jar:0.0.1", "Evil license", defaultPolicy, false);
    data.put("com.example.denied:missing:jar:1.0.0", "", defaultPolicy, false);

    policies.add(policy);
    enforcer = new AggregateLicensePolicyEnforcer(policies);

    Assertions.assertEquals(data.getExpected(), enforcer.apply(data.getLicenseMap()), description);
  }

  @Test
  void test_allowedArtifact() {
    final String description = "Artifacts with an approved pattern should be allowed, while unmatching should not.";

    final LicensePolicy licensePolicy = new LicensePolicy(LicensePolicy.Rule.APPROVE, LicensePolicy.Type.LICENSE_NAME, "MIT");
    final LicensePolicy artifactPolicy = new LicensePolicy(LicensePolicy.Rule.APPROVE, LicensePolicy.Type.ARTIFACT_PATTERN, "io.example*");

    data.put("com.example.allowed:boo:jar:1.0.0", "MIT", licensePolicy, true);
    data.put("io.example:foobar:jar:2.1.0", "Apache 2.0", artifactPolicy, true);
    data.put("com.example.denied:evil:jar:0.0.1", "Evil license", defaultPolicy, false);
    data.put("com.example.denied:missing:jar:1.0.0", "", defaultPolicy, false);

    policies.add(licensePolicy);
    policies.add(artifactPolicy);
    enforcer = new AggregateLicensePolicyEnforcer(policies);

    Assertions.assertEquals(data.getExpected(), enforcer.apply(data.getLicenseMap()), description);
  }

  @Test
  void test_multiAllowedLicense() {
    final String description = "Artifacts with matching allowed licenses should be allowed, even if there are multiple policies matching.";

    final LicensePolicy MITPolicy = new LicensePolicy(LicensePolicy.Rule.APPROVE, LicensePolicy.Type.LICENSE_NAME, "MIT");
    final LicensePolicy ASL2Policy = new LicensePolicy(LicensePolicy.Rule.APPROVE, LicensePolicy.Type.LICENSE_NAME, "Apache 2.0");

    data.put("com.example.allowed:boo:jar:1.0.0", "MIT", MITPolicy, true);
    data.put("io.example:foobar:jar:2.1.0", "Apache 2.0", ASL2Policy, true);
    data.put("com.example.denied:evil:jar:0.0.1", "Evil license", defaultPolicy, false);
    data.put("com.example.denied:missing:jar:1.0.0", "", defaultPolicy, false);

    policies.add(MITPolicy);
    policies.add(ASL2Policy);
    enforcer = new AggregateLicensePolicyEnforcer(policies);

    Assertions.assertEquals(data.getExpected(), enforcer.apply(data.getLicenseMap()), description);
  }

  @Test
  void test_explicitDeny() {
    final String description = "Artifacts with matching allowed licenses but matching denied artifact should be denied.";
    final String license = "MIT";
    final String artifact = "com.example.denied:evil:jar:0.0.1";

    final LicensePolicy patternPolicy = new LicensePolicy(LicensePolicy.Rule.DENY, LicensePolicy.Type.ARTIFACT_PATTERN, artifact);
    final LicensePolicy licensePolicy = new LicensePolicy(LicensePolicy.Rule.APPROVE, LicensePolicy.Type.LICENSE_NAME, license);

    data.put("com.example.allowed:boo:jar:1.0.0", license, licensePolicy, true);
    data.put("io.example:foobar:jar:2.1.0", license, licensePolicy, true);
    data.put(artifact, license, patternPolicy, false);
    data.put("com.example.denied:missing:jar:1.0.0", "", defaultPolicy, false);

    policies.add(patternPolicy);
    policies.add(licensePolicy);
    enforcer = new AggregateLicensePolicyEnforcer(policies);

    Assertions.assertEquals(data.getExpected(), enforcer.apply(data.getLicenseMap()), description);
  }

  @Test
  void test_denyOverride() {
    final String description = "Conflicting policies should prefer deny rules.";

    final String license = "MIT";
    final LicensePolicy approvePolicy = new LicensePolicy(LicensePolicy.Rule.APPROVE, LicensePolicy.Type.LICENSE_NAME, license);
    final LicensePolicy denyPolicy = new LicensePolicy(LicensePolicy.Rule.DENY, LicensePolicy.Type.LICENSE_NAME, license);

    data.put("com.example.allowed:boo:jar:1.0.0", license, denyPolicy, false);

    policies.add(approvePolicy);
    policies.add(denyPolicy);
    enforcer = new AggregateLicensePolicyEnforcer(policies);

    Assertions.assertEquals(data.getExpected(), enforcer.apply(data.getLicenseMap()), description);
  }
}
