# License Maven Plugin

[![Java CI](https://github.com/mathieucarbou/license-maven-plugin/actions/workflows/ci.yaml/badge.svg)](https://github.com/mathieucarbou/license-maven-plugin/actions/workflows/ci.yaml)
[![Maven Central](https://img.shields.io/maven-central/v/com.mycila/license-maven-plugin.svg)](https://repo1.maven.org/maven2/com/mycila/license-maven-plugin/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)

The **official and original** Maven plugin to manage license headers in your source files.

## Overview

When developing an open-source or enterprise project, every source file typically needs a license header. This plugin automates the entire lifecycle of those headers:

- **`license:check`** — verify that all source files have a valid header (fails the build if any are missing)
- **`license:format`** — add missing headers; update existing ones to match the current template
- **`license:remove`** — remove headers that were previously added by the plugin

Additional features:

- Built-in templates for 30+ open-source licenses (Apache 2, MIT, GPL, LGPL, EPL, …)
- Variable substitution with `${year}`, `${owner}`, POM properties, and custom placeholders
- Flexible file type mappings — support any extension with the right comment style
- Multi-licensing support — apply two or more headers to the same file
- Dependency license enforcement — fail the build when dependencies violate your policies
- SCM integration — derive copyright years automatically from Git, Subversion, or filesystem metadata

## Documentation

Full documentation: **[https://mathieu.carbou.me/license-maven-plugin/](https://mathieu.carbou.me/license-maven-plugin/)**

## Quick Setup

Add the plugin to your `pom.xml`. Replace `5.0.0` with the [latest release](https://repo1.maven.org/maven2/com/mycila/license-maven-plugin/):

```xml
<plugin>
  <groupId>com.mycila</groupId>
  <artifactId>license-maven-plugin</artifactId>
  <version>5.0.0</version>
  <configuration>
    <properties>
      <owner>Your Name or Organisation</owner>
      <email>you@example.com</email>
    </properties>
    <licenseSets>
      <licenseSet>
        <header>com/mycila/maven/plugin/license/templates/APACHE-2.txt</header>
        <excludes>
          <exclude>**/README*</exclude>
          <exclude>src/test/resources/**</exclude>
          <exclude>src/main/resources/**</exclude>
        </excludes>
      </licenseSet>
    </licenseSets>
  </configuration>
  <dependencies>
    <!-- Optional: automatically derive copyright years from Git history -->
    <dependency>
      <groupId>com.mycila</groupId>
      <artifactId>license-maven-plugin-git</artifactId>
      <version>5.0.0</version>
    </dependency>
  </dependencies>
  <executions>
    <execution>
      <id>check-license</id>
      <phase>verify</phase>
      <goals>
        <goal>check</goal>
      </goals>
    </execution>
  </executions>
</plugin>
```

Then run:

```bash
# Add/update headers
mvn license:format

# Check headers (also runs automatically on mvn verify)
mvn license:check
```

## Java Version Compatibility

| Plugin version | Minimum Java |
|---|---|
| `>= 5.0.0` | Java 11 |
| `4.x` (last: `4.6`) | Java 8 |

Version `6.0.0` and later will require Java 17.

## License

[Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0)
