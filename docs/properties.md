# Properties & Placeholders

Header templates support `${property}` placeholders that are resolved at processing time. This lets you write a single header file and have it produce the correct output for each project, year, and source file.

## Built-in Properties

### POM Properties

| Placeholder | Source |
|---|---|
| `${project.groupId}` | POM `<groupId>` |
| `${project.artifactId}` | POM `<artifactId>` |
| `${project.version}` | POM `<version>` |
| `${project.name}` | POM `<name>` |
| `${project.description}` | POM `<description>` |
| `${project.inceptionYear}` | POM `<inceptionYear>` |
| `${project.url}` | POM `<url>` |
| `${project.organization.name}` | POM `<organization><name>` |
| `${project.organization.url}` | POM `<organization><url>` |

### Per-Document Properties

| Placeholder | Description |
|---|---|
| `${file.name}` | Name of the file being processed |

### Environment and System Properties

Any environment variable or system property (e.g., `-Downer=Acme`) is available as a placeholder.

## Defining Custom Properties

Declare your own properties in the `<properties>` block of a license set (or at the top-level `<properties>` for a global default):

```xml
<configuration>
  <properties>
    <owner>Acme Corp</owner>
    <email>legal@acme.com</email>
    <year>2024</year>
  </properties>
  <licenseSets>
    <licenseSet>
      <header>com/mycila/maven/plugin/license/templates/APACHE-2.txt</header>
    </licenseSet>
  </licenseSets>
</configuration>
```

Per-license-set properties override top-level defaults:

```xml
<configuration>
  <!-- defaults -->
  <properties>
    <owner>Acme Corp</owner>
  </properties>
  <licenseSets>
    <licenseSet>
      <header>com/mycila/maven/plugin/license/templates/APACHE-2.txt</header>
    </licenseSet>
    <licenseSet>
      <header>com/mycila/maven/plugin/license/templates/MIT.txt</header>
      <!-- overrides the default owner for this set only -->
      <properties>
        <owner>Third Party Vendor</owner>
      </properties>
    </licenseSet>
  </licenseSets>
</configuration>
```

## Custom PropertiesProvider

For advanced use cases — such as reading properties from a database, API, or custom files — you can implement the `PropertiesProvider` interface and provide it as a plugin dependency.

### Interface

```java
package com.mycila.maven.plugin.license;

import com.mycila.maven.plugin.license.document.Document;
import java.util.Map;

public interface PropertiesProvider extends AutoCloseable {

  default void init(AbstractLicenseMojo mojo, Map<String, String> currentProperties) {
    // Called once before processing starts
  }

  default Map<String, String> adjustProperties(
      AbstractLicenseMojo mojo,
      Map<String, String> currentProperties,
      Document document) {
    // Called per-document; return additional properties to merge
    return Collections.emptyMap();
  }

  @Override
  default void close() {
    // Called after processing completes
  }
}
```

### Registration

Register your implementation using the **Java ServiceLoader** mechanism. Create the file:

```
META-INF/services/com.mycila.maven.plugin.license.PropertiesProvider
```

With your class name as the content:

```
com.example.MyCustomPropertiesProvider
```

### Providing via Plugin Dependency

Package your implementation in a JAR and declare it as a plugin dependency:

```xml
<plugin>
  <groupId>com.mycila</groupId>
  <artifactId>license-maven-plugin</artifactId>
  <version>5.0.0</version>
  <dependencies>
    <dependency>
      <groupId>com.example</groupId>
      <artifactId>my-properties-provider</artifactId>
      <version>1.0.0</version>
    </dependency>
  </dependencies>
</plugin>
```

!!! info "SCM Integration uses PropertiesProvider"
    The [Git](scm/git.md), [Subversion](scm/svn.md), and [Filesystem](scm/filesystem.md) integrations are all implemented as `PropertiesProvider` instances. They are activated by adding the corresponding artifact as a plugin dependency.

## Header Sections

Header sections solve a different problem than `${property}` placeholders. While properties insert the **same value** in every file, header sections allow a region of the header to **vary from file to file** while still passing `license:check`.

A typical use case: copyright years differ per file (the year of the last change), but the rest of the header is identical everywhere.

### How It Works

1. Place a **section key** (a literal string) directly in your header template where the dynamic content should go.
2. Configure a `<headerSection>` in the POM with:
    - `key` — the literal string that appears in the template
    - `defaultValue` — the value inserted when `license:format` writes a **new** header
    - `ensureMatch` — a regex that `license:check` uses to validate the content **already present** in a file at that position
    - `multiLineMatch` — if `true`, the regex is applied to the full multi-line text of the section; if `false` (default), each line is matched individually

### Checking vs. Formatting

- **`license:check`**: The plugin splits the expected header at each section key, finds the static parts in the file's actual header, then extracts the text between them. That extracted text is validated against `ensureMatch`. If it matches, the header is considered valid — even if the value differs from `defaultValue`.
- **`license:format`** (new files only): When a file has no header (or a non-matching one), the plugin inserts a new header with each section key replaced by `defaultValue`. Existing files whose header already passes `ensureMatch` are **not modified** — the current value is preserved.

### Example

```xml
<licenseSet>
  <inlineHeader><![CDATA[
COPYRIGHT_LINE

Licensed under the Apache License, Version 2.0
]]></inlineHeader>
  <headerSections>
    <headerSection>
      <key>COPYRIGHT_LINE</key>
      <defaultValue>Copyright (C) 2024 Acme Corp</defaultValue>
      <ensureMatch>Copyright \(C\) \d{4} .*</ensureMatch>
    </headerSection>
  </headerSections>
</licenseSet>
```

**Result:**

- A file with `Copyright (C) 2019 Acme Corp` passes `license:check` (the regex `\d{4}` matches `2019`) and is **left untouched** by `license:format`.
- A file with `Copyright (C) 201 Acme Corp` **fails** `license:check` (three-digit year does not match `\d{4}`).
- A file with **no header** gets `Copyright (C) 2024 Acme Corp` inserted (the `defaultValue`).

### Multiple Sections

You can use multiple sections in a single template. Each key must be a unique literal string:

```xml
<licenseSet>
  <inlineHeader><![CDATA[
COPYRIGHT_LINE AUTHOR_NAME

Licensed under the Apache License, Version 2.0
]]></inlineHeader>
  <headerSections>
    <headerSection>
      <key>COPYRIGHT_LINE</key>
      <defaultValue>Copyright (C) 2024</defaultValue>
    </headerSection>
    <headerSection>
      <key>AUTHOR_NAME</key>
      <defaultValue>Acme Corp</defaultValue>
      <ensureMatch>\w+.*</ensureMatch>
    </headerSection>
  </headerSections>
</licenseSet>
```

### Multi-line Sections

When a section value spans multiple lines, set `multiLineMatch` to `true` so the regex is applied to the full reconstructed text instead of line by line:

```xml
<headerSection>
  <key>COPYRIGHT_BLOCK</key>
  <defaultValue>Copyright (C) 2024
Name: Acme Corp</defaultValue>
  <ensureMatch>Copyright \(C\) \d{4}\nName: .*</ensureMatch>
  <multiLineMatch>true</multiLineMatch>
</headerSection>
```

### Field Reference

| Field | Type | Default | Description |
|---|---|---|---|
| `key` | String | — | The literal string in the template that marks this section |
| `defaultValue` | String | — | Value inserted when writing a new header |
| `ensureMatch` | String | — | Regex the section value must match during `license:check` (full-string match) |
| `multiLineMatch` | boolean | `false` | Apply `ensureMatch` to the full multi-line text instead of line by line |
