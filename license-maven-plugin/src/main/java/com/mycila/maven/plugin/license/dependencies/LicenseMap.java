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

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.License;

import java.util.Map;
import java.util.Set;

/**
 * To be implemented by different project/build-framework scanners presenting
 * licenses of dependencies.
 *
 * @author Royce Remer
 */
public interface LicenseMap {

  Map<License, Set<Artifact>> getLicenseMap();
}
