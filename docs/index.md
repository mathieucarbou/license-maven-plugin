---
hide:
  - toc
---

# License Maven Plugin

[![Java CI](https://github.com/mathieucarbou/license-maven-plugin/actions/workflows/ci.yaml/badge.svg)](https://github.com/mathieucarbou/license-maven-plugin/actions/workflows/ci.yaml)
[![Maven Central](https://img.shields.io/maven-central/v/com.mycila/license-maven-plugin.svg)](https://central.sonatype.com/artifact/com.mycila/license-maven-plugin)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)

The **official and original** Maven plugin to manage license headers in your source files.

- **Check** that all source files contain a valid license header (`license:check`)
- **Add** missing headers automatically (`license:format`)
- **Update** existing headers when your license template changes (`license:format`)
- **Remove** headers previously added by the plugin (`license:remove`)
- **Custom mappings** — support new file types by mapping extensions to comment styles
- **Property placeholders** — `${year}`, `${owner}`, and any POM, environment, or system property
- **SCM integration** — automatically derive copyright year ranges from Git, SVN, or filesystem metadata
- **Multi-licensing** — apply two or more license headers to the same file
- **Dependency enforcement** — fail the build if dependencies use unapproved licenses
