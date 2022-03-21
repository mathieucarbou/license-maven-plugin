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

import org.apache.maven.artifact.Artifact;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ArtifactLicensePolicyEnforcerTest extends ArtifactLicensePolicyEnforcerTestBase {
  Artifact artifact;
  LicensePolicy policy;
  ArtifactLicensePolicyEnforcer enforcer;

  @Test
  void test_explicitPatternAllowed() {
    final String description = "An artifact inclusion pattern explicitely matching with an allow rule should be allowed.";
    final String pattern = "com.example:example:jar:1.0.0";

    artifact = getArtifact(pattern);
    policy = new LicensePolicy(LicensePolicy.Rule.APPROVE, LicensePolicy.Type.ARTIFACT_PATTERN, pattern);
    enforcer = new ArtifactLicensePolicyEnforcer(policy);

    Assertions.assertTrue(enforcer.apply(artifact), description);
  }

  @Test
  void test_explicitPatternUnmatchedAllowed() {
    final String description = "An artifact inclusion pattern explicitely matching with an allow rule should deny non-matching artifacts.";
    final String pattern = "com.example:example:jar:1.0.0";

    artifact = getArtifact("com.example.subpackage:other-artifact:jar:1.0.0");
    policy = new LicensePolicy(LicensePolicy.Rule.APPROVE, LicensePolicy.Type.ARTIFACT_PATTERN, pattern);
    enforcer = new ArtifactLicensePolicyEnforcer(policy);

    Assertions.assertFalse(enforcer.apply(artifact), description);
  }

  @Test
  void test_explicitPatternUnmatchedDenied() {
    final String description = "An artifact inclusion pattern deny rule should allow unmatched artifacts.";

    artifact = getArtifact("com.example:example:jar:1.0.0");
    policy = new LicensePolicy(LicensePolicy.Rule.DENY, LicensePolicy.Type.ARTIFACT_PATTERN, "com.example:example:jar:0.0.1");
    enforcer = new ArtifactLicensePolicyEnforcer(policy);

    Assertions.assertTrue(enforcer.apply(artifact), description);
  }

  @Test
  void test_explicitPatternDenied() {
    final String description = "An artifact inclusion pattern explicitely matching with a deny rule should be denied.";
    final String pattern = "com.example:example:jar:1.0.0";

    artifact = getArtifact(pattern);
    policy = new LicensePolicy(LicensePolicy.Rule.DENY, LicensePolicy.Type.ARTIFACT_PATTERN, pattern);
    enforcer = new ArtifactLicensePolicyEnforcer(policy);

    Assertions.assertFalse(enforcer.apply(artifact), description);
  }

  @Test
  void test_greedyPatternAllowed() {
    final String description = "A greedy artifact inclusion pattern matching with an allow rule should be allowed.";
    final String gav = "com.example:example:jar:1.0.0";
    final String pattern = "com.example*";

    artifact = getArtifact(gav);
    policy = new LicensePolicy(LicensePolicy.Rule.APPROVE, LicensePolicy.Type.ARTIFACT_PATTERN, pattern);
    enforcer = new ArtifactLicensePolicyEnforcer(policy);

    Assertions.assertTrue(enforcer.apply(artifact), description);
  }

  @Test
  void test_greedyDeny() {
    final String description = "Greedy patterns with a deny rule should deny everything.";
    final String gav = "com.example:example:jar:1.0.0";

    artifact = getArtifact(gav);
    policy = new LicensePolicy(LicensePolicy.Rule.DENY, LicensePolicy.Type.ARTIFACT_PATTERN, null);
    enforcer = new ArtifactLicensePolicyEnforcer(policy);

    Assertions.assertFalse(enforcer.apply(artifact), description);
  }
}