# Git Integration

`license-maven-plugin-git` extends the plugin with properties derived from the Git history of each file. This is particularly useful for automatically maintaining accurate copyright year ranges in your headers without manual updates.

## Properties Added

| Property | Description |
|---|---|
| `license.git.copyrightLastYear` | Year of the last commit that changed the file |
| `license.git.copyrightYears` | `inceptionYear–lastYear` range, or just `inceptionYear` if they are equal |
| `license.git.copyrightCreationYear` | Year of the first commit of the file |
| `license.git.copyrightExistenceYears` | Range from `copyrightCreationYear` to `copyrightLastYear` |
| `license.git.CreationAuthorName` | Name of the author of the first commit of the file |
| `license.git.CreationAuthorEmail` | Email of the author of the first commit of the file |

!!! note
    `copyrightCreationYear`, `copyrightExistenceYears`, `CreationAuthorName`, and `CreationAuthorEmail` require the **full git history** of each file. The plugin will warn when it detects a shallow repository if these are used.

## Setup

Add `license-maven-plugin-git` as a dependency to the plugin:

```xml
<plugin>
  <groupId>com.mycila</groupId>
  <artifactId>license-maven-plugin</artifactId>
  <version>5.0.0</version>
  <configuration>
    <licenseSets>
      <licenseSet>
        <inlineHeader><![CDATA[
Copyright ${license.git.copyrightYears} ${owner}

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
      <artifactId>license-maven-plugin-git</artifactId>
      <version>5.0.0</version>
    </dependency>
  </dependencies>
</plugin>
```

## Template Usage

Use `${license.git.copyrightYears}` in your inline header or classpath template:

```
Copyright ${license.git.copyrightYears} ${owner}
```

For a project with `inceptionYear` 1999 and a file last changed in 2024, this resolves to:

```
Copyright 1999-2024 Acme Corp
```

If the file was last changed in the same year as `inceptionYear`, no range is shown:

```
Copyright 1999 Acme Corp
```

## Filtering Commits

Exclude specific commits from the last-change calculation (e.g., automated formatting commits) using their SHA-1 hashes:

```xml
<properties>
  <license.git.commitsToIgnore>abc123,def456</license.git.commitsToIgnore>
</properties>
```

!!! note

    `commitsToIgnore` only affects `copyrightLastYear` — not the creation year.

## Limiting History Depth

When you only need `copyrightLastYear` (not the creation-related properties), you can improve performance by limiting how deep in the history the plugin looks:

```xml
<properties>
  <license.git.maxCommitsLookup>1</license.git.maxCommitsLookup>
</properties>
```

A value of `1` gives the best performance but may be inaccurate if the most recent commit pre-dates earlier commits (e.g., after a rebase or squash).

## Uncommitted Changes

When files have uncommitted changes, the plugin assumes they will be committed in the **current year**. This means `copyrightLastYear` may reflect the current year even before you commit. This can occasionally cause `license:check` to fail after a year boundary — run `license:format` and commit again to resolve.

## Shallow Repository Handling

See [SCM Integration — Shallow Clones](index.md#handling-shallow-clones) for the available options (`warnIfShallow`, `skipOnShallow`, `failOnShallow`).
