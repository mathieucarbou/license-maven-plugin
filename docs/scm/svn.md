# Subversion Integration

`license-maven-plugin-svn` extends the plugin with properties derived from the Subversion (SVN) metadata of each file. This is useful for automatically maintaining accurate copyright year information for projects hosted on SVN.

## Properties Added

| Property | Description |
|---|---|
| `license.svn.lastchange.year` | Year of the last commit (4 digits) |
| `license.svn.lastchange.timestamp` | Timestamp of the last commit (`yyyyMMdd-HH:mm:ss`) |
| `license.svn.lastchange.date` | Date of the last commit (`yyyyMMdd`) |
| `license.svn.lastchange.revision` | SVN revision number of the last change |
| `license.svn.years.range` | `inceptionYear–lastChangeYear` range, or just `lastChangeYear` if equal or `inceptionYear` is not set |

## Setup

Add `license-maven-plugin-svn` as a dependency to the plugin:

```xml
<plugin>
  <groupId>com.mycila</groupId>
  <artifactId>license-maven-plugin</artifactId>
  <version>5.0.0</version>
  <configuration>
    <licenseSets>
      <licenseSet>
        <inlineHeader><![CDATA[
Copyright ${license.svn.years.range} ${owner}

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
      <artifactId>license-maven-plugin-svn</artifactId>
      <version>5.0.0</version>
    </dependency>
  </dependencies>
</plugin>
```

## Template Usage

```
Copyright ${license.svn.years.range} Acme Corp
```

For a project with `inceptionYear` 2010 and a file last changed in 2024:

```
Copyright 2010-2024 Acme Corp
```

## Authentication

For secured SVN repositories, configure authentication via Maven's `settings.xml` and reference the server ID in the plugin properties.

### `settings.xml`

```xml
<servers>
  <server>
    <id>my-svn-server</id>
    <username>bob</username>
    <password>s3cr3t</password>
  </server>
</servers>
```

### Plugin Configuration

```xml
<plugin>
  <groupId>com.mycila</groupId>
  <artifactId>license-maven-plugin</artifactId>
  <version>5.0.0</version>
  <configuration>
    <licenseSets>
      <licenseSet>
        <inlineHeader><![CDATA[
Copyright ${license.svn.years.range} ${owner}

Licensed under the Apache License, Version 2.0
]]></inlineHeader>
        <properties>
          <owner>Acme Corp</owner>
          <license.svn.serverId>my-svn-server</license.svn.serverId>
        </properties>
      </licenseSet>
    </licenseSets>
  </configuration>
  <dependencies>
    <dependency>
      <groupId>com.mycila</groupId>
      <artifactId>license-maven-plugin-svn</artifactId>
      <version>5.0.0</version>
    </dependency>
  </dependencies>
</plugin>
```
