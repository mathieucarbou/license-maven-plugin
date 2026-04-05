# Goals

The plugin provides four goals: `license:check`, `license:format`, `license:remove`, and `license:help`. For all configuration and parameter details, see [Configuration](configuration.md).

## `license:check`

**Full name**: `com.mycila:license-maven-plugin:5.0.0:check`

Check if the source files of the project have a valid license header.

- Thread-safe and supports parallel builds.
- Default lifecycle binding: `verify`.

```bash
mvn license:check
```

To bind the check to a specific phase in your POM:

```xml
<execution>
  <id>check-license</id>
  <phase>verify</phase>
  <goals>
    <goal>check</goal>
  </goals>
</execution>
```

---

## `license:format`

**Full name**: `com.mycila:license-maven-plugin:5.0.0:format`

Reformat files with a missing header to add it. If a header is already present but differs from the configured template, it is replaced.

- Thread-safe and supports parallel builds.
- No default lifecycle binding.

```bash
mvn license:format
```

---

## `license:remove`

**Full name**: `com.mycila:license-maven-plugin:5.0.0:remove`

Remove the specified header from source files.

- Thread-safe and supports parallel builds.
- No default lifecycle binding.

```bash
mvn license:remove
```

!!! warning
    `license:remove` only removes headers that match the configured header set. It will not remove arbitrary banner comments or headers added by unrelated tools.

---

## `license:help`

Display help information on the plugin goals and parameters.

```bash
mvn license:help -Ddetail=true -Dgoal=check
```
