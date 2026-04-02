# Multi-Licensing

Multi-licensing allows you to apply two or more license headers to the same source file. This is common for projects dual-licensed under, for example, GPL and Apache 2.0, or that distribute code under both a commercial and an open-source license.

## Using `<multi>`

Instead of `<header>` or `<inlineHeader>`, use the `<multi>` element inside a license set:

```xml
<licenseSets>
  <licenseSet>
    <multi>
      <header>GPL-2.txt</header>
      <separator>======================================================================</separator>
      <header>com/mycila/maven/plugin/license/templates/APACHE-2.txt</header>
    </multi>
  </licenseSet>
</licenseSets>
```

## `<multi>` Options

| Element | Description |
|---|---|
| `<preamble>` | Optional text placed before the first header |
| `<header>` (multiple) | Path or URL to a header template. Can be repeated. |
| `<inlineHeader>` (multiple) | Inline header text. Can be repeated. |
| `<separator>` (multiple) | Text placed between consecutive headers |

Headers and separators are concatenated in declaration order to form the combined header.

### Single Separator

If one separator is declared, it is inserted between every pair of headers:

```xml
<multi>
  <header>LICENSE-A.txt</header>
  <separator>---</separator>
  <header>LICENSE-B.txt</header>
  <header>LICENSE-C.txt</header>
</multi>
```

Results in: `LICENSE-A` + `---` + `LICENSE-B` + `---` + `LICENSE-C`

### Multiple Separators

If multiple separators are declared, the first separator goes between licenses 1 and 2, the second between 2 and 3, and so on:

```xml
<multi>
  <header>LICENSE-A.txt</header>
  <separator>---first---</separator>
  <header>LICENSE-B.txt</header>
  <separator>---second---</separator>
  <header>LICENSE-C.txt</header>
</multi>
```

## Preamble

Use `<preamble>` to add a human-readable explanation before the license headers:

```xml
<multi>
  <preamble><![CDATA[This product is dual-licensed under both the GPLv2 and Apache 2.0 License.]]></preamble>
  <header>GPL-2.txt</header>
  <separator>======================================================================</separator>
  <header>com/mycila/maven/plugin/license/templates/APACHE-2.txt</header>
</multi>
```

## Full Example

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
        <multi>
          <preamble><![CDATA[Dual-licensed under GPL-2.0 and Apache-2.0.]]></preamble>
          <header>com/mycila/maven/plugin/license/templates/GPL-2.txt</header>
          <separator>======================================================================</separator>
          <header>com/mycila/maven/plugin/license/templates/APACHE-2.txt</header>
        </multi>
        <excludes>
          <exclude>**/README*</exclude>
          <exclude>src/test/resources/**</exclude>
        </excludes>
      </licenseSet>
    </licenseSets>
  </configuration>
</plugin>
```

## Mixing Header and InlineHeader

You can mix file-based headers with inline headers:

```xml
<multi>
  <inlineHeader><![CDATA[
Copyright (C) ${year} ${owner}. All rights reserved.
Contact legal@acme.com for commercial licensing terms.
]]></inlineHeader>
  <separator>---</separator>
  <inlineHeader><![CDATA[
Also licensed under the MIT License.
Copyright (C) ${year} ${owner}
]]></inlineHeader>
</multi>
```
