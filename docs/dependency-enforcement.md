# Dependency Enforcement

The plugin can inspect the declared dependencies of your project and fail the build when any dependency uses a license that does not comply with your policies. This helps enforce legal requirements automatically during the build.

## Enabling Enforcement

```xml
<configuration>
  <dependencyEnforce>true</dependencyEnforce>
  <dependencyExceptionMessage>
    Contact legal@acme.com to request approval for new licenses.
  </dependencyExceptionMessage>
  <dependencyPolicies>
    <dependencyPolicy>
      <type>LICENSE_URL</type>
      <rule>APPROVE</rule>
      <value>https://www.apache.org/licenses/LICENSE-2.0.txt</value>
    </dependencyPolicy>
    <dependencyPolicy>
      <type>LICENSE_NAME</type>
      <rule>APPROVE</rule>
      <value>Public Domain</value>
    </dependencyPolicy>
    <dependencyPolicy>
      <type>ARTIFACT_PATTERN</type>
      <rule>APPROVE</rule>
      <value>com.mycila.*</value>
    </dependencyPolicy>
  </dependencyPolicies>
</configuration>
```

## Configuration Options

See the [Dependency Enforcement](configuration.md#dependency-enforcement) section in the Configuration reference for all available options and their defaults.

## Policy Rules

Each `<dependencyPolicy>` has three fields:

| Field | Values | Description |
|---|---|---|
| `type` | `LICENSE_URL`, `LICENSE_NAME`, `ARTIFACT_PATTERN` | What to match against |
| `rule` | `APPROVE`, `DENY` | Whether matching artifacts are allowed or denied |
| `value` | String | The value to match (exact string or regex for `ARTIFACT_PATTERN`) |

### Policy Types

| Type | Match Target | Match Strategy |
|---|---|---|
| `LICENSE_URL` | The `<url>` element of the dependency's declared license | Exact string match |
| `LICENSE_NAME` | The `<name>` element of the dependency's declared license | Exact string match |
| `ARTIFACT_PATTERN` | `groupId:artifactId:type:version` coordinate | Regex match |

## Enforcement Order

Policies are evaluated in the following order regardless of their declaration order:

1. **Implicit default DENY** — all artifacts are denied unless explicitly approved
2. **APPROVE rules** — artifacts matching any approve rule are allowed
3. **DENY rules** — artifacts matching any deny rule are denied (takes precedence over APPROVE)

!!! warning "DENY overrides APPROVE"
    If an artifact matches both an APPROVE and a DENY rule, the DENY wins.
    For example: a global `APPROVE` for `com.example.*` combined with a `DENY` for `com.example.bad:library` will deny `com.example.bad:library`.

## Full Example

```xml
<configuration>
  <dependencyEnforce>true</dependencyEnforce>
  <dependencyScopes>
    <dependencyScope>compile</dependencyScope>
    <dependencyScope>runtime</dependencyScope>
  </dependencyScopes>
  <dependencyExceptionMessage>
    This artifact uses an unapproved license. Contact legal@acme.com.
  </dependencyExceptionMessage>
  <dependencyPolicies>
    <!-- Allow all Apache 2.0 licensed artifacts -->
    <dependencyPolicy>
      <type>LICENSE_URL</type>
      <rule>APPROVE</rule>
      <value>https://www.apache.org/licenses/LICENSE-2.0.txt</value>
    </dependencyPolicy>
    <!-- Allow public domain artifacts by license name -->
    <dependencyPolicy>
      <type>LICENSE_NAME</type>
      <rule>APPROVE</rule>
      <value>Public Domain</value>
    </dependencyPolicy>
    <!-- Allow all com.mycila artifacts regardless of license -->
    <dependencyPolicy>
      <type>ARTIFACT_PATTERN</type>
      <rule>APPROVE</rule>
      <value>com.mycila.*</value>
    </dependencyPolicy>
    <!-- Deny a specific vendor entirely -->
    <dependencyPolicy>
      <type>ARTIFACT_PATTERN</type>
      <rule>DENY</rule>
      <value>com.example:restricted-lib:.*</value>
    </dependencyPolicy>
  </dependencyPolicies>
</configuration>
```

## Accuracy Note

The enforcement relies on the accuracy of the `<licenses>` section declared in the POM of each dependency artifact. If a dependency does not declare its licenses in its POM, the plugin cannot detect them. Keep this limitation in mind and supplement with manual reviews for critical dependencies.
