/*
 * Copyright (C) 2008-2023 Mycila (mathieu.carbou@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mycila.maven.plugin.license.dependencies;

/**
 * Expose message text to config and tests.
 */
public interface LicenseMessage {

  /** The warn policy denied. */
  String WARN_POLICY_DENIED = "Some licenses were denied by policy:";

  /** The info license impl. */
  String INFO_LICENSE_IMPL = "Checking licenses in dependencies using";

  /** The info deps discovered. */
  String INFO_DEPS_DISCOVERED = "Discovered dependencies after filtering";
}