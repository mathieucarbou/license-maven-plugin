# SCM Integration

The plugin supports integrating with source control management (SCM) systems to automatically derive per-file properties such as the year of the last change or the original author. These properties can then be used as placeholders in your license header templates.

## How It Works

SCM integration is implemented as [`PropertiesProvider`](../properties.md#custom-propertiesprovider) extensions. Each integration module is an optional dependency that you add to the plugin. Once on the classpath, it automatically provides additional properties for each file being processed.

## Available Integrations

| Module | Source | Key Properties |
|---|---|---|
| [Git](git.md) | Git commit history | `license.git.copyrightLastYear`, `license.git.copyrightYears`, author info |
| [Subversion](svn.md) | SVN metadata | `license.svn.lastchange.year`, `license.svn.years.range` |
| [Filesystem](filesystem.md) | File modification time | `license.fs.copyrightLastYear`, `license.fs.copyrightYears` |

## Adding an Integration

Add the desired integration module as a `<dependency>` of the plugin (not the project):

```xml
<plugin>
  <groupId>com.mycila</groupId>
  <artifactId>license-maven-plugin</artifactId>
  <version>5.0.0</version>
  <dependencies>
    <dependency>
      <groupId>com.mycila</groupId>
      <artifactId>license-maven-plugin-git</artifactId>
      <version>5.0.0</version>
    </dependency>
  </dependencies>
</plugin>
```

## Handling Shallow Clones

When using Git or SVN integration, accurate property values (especially copyright years) require access to the full commit history. Shallow clones (common in CI environments with `git clone --depth=1`) may produce incorrect results.

The plugin provides three configuration options to handle this:

| Option | Property | Default | Behaviour |
|---|---|---|---|
| `warnIfShallow` | `license.warnIfShallow` | `true` | Log a warning when a shallow clone is detected |
| `skipOnShallow` | `license.skipOnShallow` | `false` | Skip the entire plugin execution silently |
| `failOnShallow` | `license.failOnShallow` | `false` | Fail the build with an error |

When both `skipOnShallow` and `failOnShallow` are `true`, `skipOnShallow` takes precedence.

!!! tip "Recommended for CI / AI Agents"
    Set `license.skipOnShallow=true` in pipelines that use shallow clones to avoid widespread header rewrites from inaccurate year values.
