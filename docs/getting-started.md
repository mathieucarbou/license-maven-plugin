# Getting Started

## Requirements

- **Java 11** or later (use plugin version `4.6` for Java 8)
- **Maven 3.x**

## Plugin Declaration

Add the plugin to your `pom.xml`:

```xml
<plugin>
  <groupId>com.mycila</groupId>
  <artifactId>license-maven-plugin</artifactId>
  <version>5.0.0</version>
  <configuration>
    <properties>
      <owner>Acme Corp</owner>
      <email>legal@acme.com</email>
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
</plugin>
```

## Bind to the Build Lifecycle

To automatically check headers on every build, bind the `check` goal to the `verify` phase:

```xml
<plugin>
  <groupId>com.mycila</groupId>
  <artifactId>license-maven-plugin</artifactId>
  <version>5.0.0</version>
  <configuration>
    <licenseSets>
      <licenseSet>
        <header>com/mycila/maven/plugin/license/templates/APACHE-2.txt</header>
      </licenseSet>
    </licenseSets>
  </configuration>
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

## Quick Start

### 1. Format (add/update headers)

```bash
mvn license:format
```

### 2. Check (verify headers are present)

```bash
mvn license:check
```

### 3. Remove headers

```bash
mvn license:remove
```

### Command-line Overrides

Most configuration options have a `-Dlicense.*` system property equivalent:

```bash
mvn license:check -Dlicense.header=com/mycila/maven/plugin/license/templates/APACHE-2.txt
mvn license:format -Dlicense.skip=true
```
