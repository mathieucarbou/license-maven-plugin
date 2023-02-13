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

import static com.soebes.itf.extension.assertj.MavenITAssertions.assertThat;

import org.junit.jupiter.api.DisplayName;

import com.soebes.itf.jupiter.extension.MavenGoal;
import com.soebes.itf.jupiter.extension.MavenJupiterExtension;
import com.soebes.itf.jupiter.extension.MavenTest;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;

/**
 * The <a href="https://khmarbaise.github.io/maven-it-extension/itf-documentation/usersguide/usersguide.html">Maven Integration Testing Framework</a>
 * is used here. The main reasons being as follow
 * <p>
 * a) we can verify a few cases that the invoker method make really difficult (For debugging purpose follow this
 * <a href="https://khmarbaise.github.io/maven-it-extension/itf-documentation/usersguide/usersguide.html#_debugging">
 * guide</a>)
 * b) the test harness method requires creating a custom Artifact resolver, as the dependencyGraphBuilder Component will
 * not provide a usable bean, and we would need extensive mocking to override data for specific cases
 * c) it's a lot faster than maven-invoker-plugin
 * d) No snapshot installation should be necessary (the custom extension does all the necessary filtering)
 * <p>
 * A good overview of similar woes <a href="https://khmarbaise.github.io/maven-it-extension/itf-documentation/background/background.html">
 *   https://khmarbaise.github.io/maven-it-extension/itf-documentation/background/background.html</a>.
 *
 * @author Royce Remer
 * @author Michael J. Simons
 */
@MavenJupiterExtension
public class MavenProjectLicensesIT {

  @MavenTest
  @MavenGoal("license:check")
  @DisplayName("A project with enforcement enabled but nothing in scope should find zero dependencies")
  void no_dependencies(MavenExecutionResult result) {
    assertThat(result)
      .isSuccessful()
      .out().info()
      .contains(LicenseMessage.INFO_DEPS_DISCOVERED + ": 0");
  }

  @MavenTest
  @MavenGoal("license:check")
  @DisplayName("A project with enforcement enabled and dependencies in scope under default deny policy should fail.")
  void deny(MavenExecutionResult result) {
    assertThat(result)
      .isFailure()
      .out().error()
      .anyMatch(s -> s.contains(LicenseMessage.WARN_POLICY_DENIED));
  }

  @MavenTest
  @MavenGoal("license:check")
  @DisplayName("A project with allow policy and a single dependency should succeed.")
  void approve(MavenExecutionResult result) {
    assertThat(result)
      .isSuccessful()
      .out().info()
      .contains(LicenseMessage.INFO_DEPS_DISCOVERED + ": 1");
  }
}
