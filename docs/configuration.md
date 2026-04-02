# Configuration

## License Sets

The core concept introduced in version 4.0 is the **License Set**. A license set groups together a header template with its includes, excludes, properties, and comment style configuration. You can define one or more license sets in a single plugin execution.

```xml
<configuration>
  <licenseSets>
    <licenseSet>
      <!-- Header template (see License Templates page) -->
      <header>com/mycila/maven/plugin/license/templates/APACHE-2.txt</header>

      <!-- Properties used in the template -->
      <properties>
        <owner>Acme Corp</owner>
        <year>2024</year>
      </properties>

      <!-- File inclusion/exclusion -->
      <includes>
        <include>src/main/java/**</include>
      </includes>
      <excludes>
        <exclude>**/package-info.java</exclude>
        <exclude>src/test/resources/**</exclude>
      </excludes>
    </licenseSet>
  </licenseSets>
</configuration>
```

### Multiple License Sets

Apply different headers to different parts of the project in a single execution:

```xml
<licenseSets>
  <!-- Apache 2 for main sources -->
  <licenseSet>
    <header>com/mycila/maven/plugin/license/templates/APACHE-2.txt</header>
    <includes>
      <include>src/main/**</include>
    </includes>
  </licenseSet>
  <!-- MIT for test utilities -->
  <licenseSet>
    <header>com/mycila/maven/plugin/license/templates/MIT.txt</header>
    <includes>
      <include>src/test/**</include>
    </includes>
  </licenseSet>
</licenseSets>
```

## LicenseSet Options

| Option | Type | Description |
|---|---|---|
| `header` | String | Path or URL to the header template file |
| `inlineHeader` | String | The header content embedded directly in the POM (wrap in `<![CDATA[...]]>` to avoid XML escaping issues) |
| `multi` | Multi | For [multi-licensing](multi-licensing.md) |
| `validHeaders` | String[] | Additional headers accepted as valid (not reformatted) |
| `headerDefinitions` | String[] | External XML files defining custom comment styles |
| `inlineHeaderStyles` | HeaderStyle[] | Inline comment style definitions |
| `headerSections` | HeaderSection[] | Dynamic header sections for per-file substitution |
| `properties` | Map | Template placeholder values |
| `includes` | String[] | Ant-style patterns for files to include (default: `**`) |
| `excludes` | String[] | Ant-style patterns for files to exclude |
| `keywords` | String[] | Words that must appear in a header to be recognized (default: `copyright`) |
| `useDefaultExcludes` | Boolean | Whether to apply the [default exclusion list](#default-excludes) |
| `basedir` | File | Base directory for file scanning (default: `${project.basedir}`) |

## Default Values for All License Sets

These options are configured once at the plugin level and act as defaults for every `licenseSet`, unless that set overrides them.

| Option | Property | Type | Default | Description |
|---|---|---|---|---|
| `defaultBasedir` | `license.basedir` | `File` | `${project.basedir}` | Default base directory used for file scanning. Alias: `basedir`. |
| `defaultUseDefaultExcludes` | `license.useDefaultExcludes` | `Boolean` | `true` | Default value for `licenseSet.useDefaultExcludes`. Alias: `useDefaultExcludes`. |
| `defaultHeaderDefinitions` | — | `String[]` | — | Default external header-style definition files. Alias: `headerDefinitions`. |
| `defaultInlineHeaderStyles` | — | `HeaderStyle[]` | — | Default inline header-style definitions. Overrides styles loaded from `defaultHeaderDefinitions`. |
| `defaultProperties` | — | `Map<String,String>` | — | Default placeholder values available to header templates. Alias: `properties`. |

## Global Options

These options apply across all license sets and are set at the `<configuration>` level.

### General

| Option | Property | Default | Description |
|---|---|---|---|
| `skip` | `license.skip` | `false` | Skip all plugin execution |
| `aggregate` | `license.aggregate` | `false` | Check headers for all modules from the root module |
| `encoding` | `license.encoding` | project encoding | File encoding |
| `quiet` | `license.quiet` | `false` | Suppress the list of files in output |
| `prohibitLegacyUse` | — | `false` | Fail on deprecated (pre-4.0) configuration syntax |

### Goal-specific

| Option | Property | Default | Applies To | Description |
|---|---|---|---|---|
| `errorMessage` | `license.errorMessage` | `Some files do not have the expected license header. Run license:format to update them.` | `license:check` | Custom error message shown when the check goal fails. |

### Header Matching

| Option | Property | Default | Description |
|---|---|---|---|
| `strictCheck` | `license.strictCheck` | `true` | Require exact header content match |
| `failIfMissing` | `license.failIfMissing` | `true` | Fail the build when headers are missing |
| `failIfUnknown` | `license.failIfUnknown` | `false` | Fail when a file has no known comment style |
| `skipExistingHeaders` | `license.skipExistingHeaders` | `false` | Skip files that already have any valid header |

### File Processing

| Option | Property | Default | Description |
|---|---|---|---|
| `dryRun` | `license.dryRun` | `false` | Output to `.licensed` files instead of modifying originals |
| `useDefaultMapping` | `license.useDefaultMapping` | `true` | Use the [built-in extension-to-comment-style mapping](comment-styles.md) |
| `mapping` | — | — | Add or override extension-to-comment-style mappings |

### Dependency Enforcement

| Option | Property | Default | Description |
|---|---|---|---|
| `dependencyEnforce` | `license.dependencies.enforce` | `false` | Enforce configured dependency license policies |
| `dependencyPolicies` | `license.dependencies.policies` | — | Set of allow/deny license policy rules applied to dependencies |
| `dependencyScopes` | `license.dependencies.scope` | `runtime` | Restrict enforcement to the given dependency scopes |
| `dependencyExceptionMessage` | `license.dependencies.exceptionMessage` | `Some licenses were denied by policy:` | Prefix used in the build error when a dependency is denied |

### Reporting

| Option | Property | Default | Description |
|---|---|---|---|
| `reportLocation` | `license.report.location` | `${project.reporting.outputDirectory}/license-plugin-report.xml` | Output file for the execution report |
| `reportFormat` | `license.report.format` | `xml` | Report format: `xml` or `json` |
| `reportSkipped` | `license.report.skip` | `false` | Skip report generation |

Report statuses are goal-dependent:

- `PRESENT` and `MISSING` are produced by `license:check`
- `ADDED`, `REPLACED`, and `NOOP` are produced by `license:format`
- `REMOVED` and `NOOP` are produced by `license:remove`
- `UNKNOWN` indicates a file type with no recognised comment style mapping

### Concurrency

| Option | Property | Default | Description |
|---|---|---|---|
| `concurrencyFactor` | `license.concurrencyFactor` | `1.5` | Thread count multiplier: `threads = cores × factor` |
| `nThreads` | `license.nThreads` | `0` | Explicit thread count (overrides `concurrencyFactor` when > 0) |

### Shallow Repository Handling

Relevant when using [SCM integration](scm/index.md) that reads commit history (Git, SVN):

| Option | Property | Default | Description |
|---|---|---|---|
| `warnIfShallow` | `license.warnIfShallow` | `true` | Log a warning when a shallow clone is detected |
| `skipOnShallow` | `license.skipOnShallow` | `false` | Skip the entire plugin execution on shallow clones |
| `failOnShallow` | `license.failOnShallow` | `false` | Fail the build when a shallow clone is detected |

!!! tip "Recommended for CI / AI Agents"
    Set `license.skipOnShallow=true` in CI pipelines and AI coding agent workflows that use shallow clones to prevent spurious header rewrites.

## Default Excludes

When `useDefaultExcludes` is `true` (the default), the following categories of files are automatically excluded:

- Version control directories: `.git/`, `.svn/`, `.hg/`, etc.
- IDE files: `.idea/`, `.eclipse/`, `.settings/`, etc.
- Build output: `target/`, `build/`, `.gradle/`
- Runtime directories: `node_modules/`, `.angular/`, `.kotlin/`
- License and notice files: `LICENSE`, `NOTICE`, `LICENSE.txt`, etc.
- Common resource files: `*.md`, `*.txt`, `*.json`, `*.png`, `*.ico`, etc.

The [full default exclude list](https://github.com/mathieucarbou/license-maven-plugin/blob/master/license-maven-plugin/src/main/java/com/mycila/maven/plugin/license/Default.java) is defined in `Default.java`.

## Legacy Configuration (Deprecated)

Prior to version 4.0, configurations were specified directly without `<licenseSets>`. This syntax is still supported but deprecated:

```xml
<!-- DEPRECATED — use licenseSets instead -->
<configuration>
  <header>com/mycila/maven/plugin/license/templates/APACHE-2.txt</header>
  <properties>
    <owner>Acme Corp</owner>
  </properties>
  <excludes>
    <exclude>**/README*</exclude>
  </excludes>
</configuration>
```

To fail the build when deprecated configuration is used, set `prohibitLegacyUse`:

```xml
<configuration>
  <prohibitLegacyUse>true</prohibitLegacyUse>
</configuration>
```
