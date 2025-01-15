/*
 * Copyright (C) 2008-2024 Mycila (mathieu.carbou@gmail.com)
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
package com.mycila.maven.plugin.license;

import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;

public class WorkSpace {

  /**
   * The base directory, in which to search for project files.
   * <p>
   * It will be used as the default value for the base directory of each LicenseSet.
   * This default value can be overridden in each LicenseSet by setting {@link LicenseSet#basedir}.
   */

    @Parameter(property = "license.workspace.basedir", defaultValue = "${project.basedir}", alias = "basedir")
    File basedir;

    @Parameter
    String[] includes = new String[0];

    @Parameter
    String[] excludes = new String[0];
}
