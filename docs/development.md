# Development

## Requirements

- **Java 11** or later
- **Maven 3.x**
- GPG key for releases

Verify the active Java version:

```bash
mvn -v
```

## Building the Project

Full build including tests and site:

```bash
./mvnw clean install site
```

Fast build skipping slow checks:

```bash
./mvnw clean install -Dfast
```

## Releasing a Version

### 1. Prepare the Release

```bash
# Example: releasing 5.0.1
./mvnw release:prepare \
  -DreleaseVersion=5.0.1 \
  -Dtag=v5.0.1 \
  -DdevelopmentVersion=5.0.2-SNAPSHOT
```

### 2. Perform the Release

```bash
export MAVEN_GPG_PASSPHRASE=...
./mvnw release:perform -Darguments="-Dgpg.keyname=YOUR_GPG_KEY_ID"
```

If `release:perform` fails, restart it from `target/checkout`:

```bash
cd target/checkout
./mvnw deploy -Dgpg.keyname=YOUR_GPG_KEY_ID -DperformRelease=true
```

### 3. Publish to Maven Central

Go to [https://oss.sonatype.org/](https://oss.sonatype.org/) and **close and release** the staging repository.

The new version will appear on [Maven Central](https://repo1.maven.org/maven2/com/mycila/license-maven-plugin/) within a few minutes.

## Generating the Site

```bash
./mvnw clean install site
```

The generated site (Maven Plugin Reports) will be written to `target/site/`.

## Project Structure

| Module | Description |
|---|---|
| `license-maven-plugin` | Core plugin — goals, configuration, header processing |
| `license-maven-plugin-git` | Optional Git SCM integration |
| `license-maven-plugin-svn` | Optional Subversion SCM integration |
| `license-maven-plugin-fs` | Optional filesystem integration |

## Contributing

Please read [CONTRIBUTING.md](https://github.com/mathieucarbou/license-maven-plugin/blob/master/CONTRIBUTING.md) before submitting pull requests.

Report bugs and request features via [GitHub Issues](https://github.com/mathieucarbou/license-maven-plugin/issues).
