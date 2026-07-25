/*
 * Copyright (C) 2008-2025 Mycila (mathieu.carbou@gmail.com)
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

import java.io.File;

final class LicenseSets {

  private LicenseSets() {}

  static Builder header(String header) {
    Builder builder = new Builder();
    builder.licenseSet.header = header;
    return builder;
  }

  static Builder inlineHeader(String inlineHeader) {
    Builder builder = new Builder();
    builder.licenseSet.inlineHeader = inlineHeader;
    return builder;
  }

  static Builder multi(Multi multi) {
    Builder builder = new Builder();
    builder.licenseSet.multi = multi;
    return builder;
  }

  static final class Builder {

    private final LicenseSet licenseSet = new LicenseSet();

    private Builder() {}

    Builder validHeaders(String... validHeaders) {
      licenseSet.validHeaders = validHeaders;
      return this;
    }

    Builder includes(String... includes) {
      licenseSet.includes = includes;
      return this;
    }

    Builder excludes(String... excludes) {
      licenseSet.excludes = excludes;
      return this;
    }

    Builder basedir(File basedir) {
      licenseSet.basedir = basedir;
      return this;
    }

    LicenseSet[] build() {
      return new LicenseSet[]{licenseSet};
    }
  }
}
