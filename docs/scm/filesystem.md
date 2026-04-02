# Filesystem Integration

`license-maven-plugin-fs` extends the plugin with properties derived from the **filesystem modification time** of each file. This is the simplest SCM integration — it requires no repository access and works in any environment.

## Properties Added

| Property | Description |
|---|---|
| `license.fs.copyrightLastYear` | Year of the last filesystem modification time of the file |
| `license.fs.copyrightYears` | `inceptionYear–lastYear` range, or just `inceptionYear` if they are equal |

## Setup

Add `license-maven-plugin-fs` as a dependency to the plugin:

```xml
<plugin>
  <groupId>com.mycila</groupId>
  <artifactId>license-maven-plugin</artifactId>
  <version>5.0.0</version>
  <configuration>
    <licenseSets>
      <licenseSet>
        <inlineHeader><![CDATA[
Copyright ${license.fs.copyrightYears} ${owner}

Licensed under the Apache License, Version 2.0
]]></inlineHeader>
        <properties>
          <owner>Acme Corp</owner>
        </properties>
      </licenseSet>
    </licenseSets>
  </configuration>
  <dependencies>
    <dependency>
      <groupId>com.mycila</groupId>
      <artifactId>license-maven-plugin-fs</artifactId>
      <version>5.0.0</version>
    </dependency>
  </dependencies>
</plugin>
```

## Template Usage

```
Copyright ${license.fs.copyrightYears} Acme Corp
```

For a project with `inceptionYear` 1999 and a file last modified in 2024:

```
Copyright 1999-2024 Acme Corp
```

## When to Use This Integration

The filesystem integration is a good choice when:

- Your project is not under version control
- You do not need author information
- You want a simple, dependency-free approach to year ranges

For projects under Git or SVN, the [Git](git.md) or [SVN](svn.md) integration provides more accurate and meaningful year values tied to commit history rather than filesystem timestamps (which can be affected by file copies, checkouts, etc.).
