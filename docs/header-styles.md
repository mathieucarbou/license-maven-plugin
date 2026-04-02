# Custom Header Styles

The plugin allows you to define entirely new comment styles — either in external XML files or inline in the POM. This is useful when working with file types that use a comment syntax not covered by the [built-in styles](comment-styles.md).

## Inline Header Styles

Define new styles directly in `pom.xml` using `<defaultInlineHeaderStyles>`:

```xml
<configuration>
  <mapping>
    <txt>SMILEY_STYLE</txt>
  </mapping>
  <defaultInlineHeaderStyles>
    <defaultInlineHeaderStyle>
      <name>SMILEY_STYLE</name>
      <firstLine>:(</firstLine>
      <beforeEachLine> ( </beforeEachLine>
      <endLine>:(</endLine>
      <firstLineDetectionPattern>\:\(</firstLineDetectionPattern>
      <lastLineDetectionPattern>\:\(</lastLineDetectionPattern>
      <allowBlankLines>false</allowBlankLines>
      <multiLine>false</multiLine>
    </defaultInlineHeaderStyle>
  </defaultInlineHeaderStyles>
</configuration>
```

Alternatively, inline styles can be scoped to a single license set via `<inlineHeaderStyles>`:

```xml
<licenseSets>
  <licenseSet>
    <header>com/mycila/maven/plugin/license/templates/APACHE-2.txt</header>
    <inlineHeaderStyles>
      <inlineHeaderStyle>
        <name>MY_STYLE</name>
        <!-- ... -->
      </inlineHeaderStyle>
    </inlineHeaderStyles>
  </licenseSet>
</licenseSets>
```

## External Header Definition Files

For reusable definitions across projects, define styles in an XML file and reference it:

```xml
<configuration>
  <headerDefinitions>
    <headerDefinition>src/etc/my-styles.xml</headerDefinition>
  </headerDefinitions>
  <mapping>
    <cs>CSREGION_STYLE</cs>
  </mapping>
</configuration>
```

Or at the license set level:

```xml
<licenseSet>
  <headerDefinitions>
    <headerDefinition>src/etc/my-styles.xml</headerDefinition>
  </headerDefinitions>
</licenseSet>
```

The definition file is an XML file containing one or more style definitions:

```xml
<?xml version="1.0" encoding="ISO-8859-1"?>
<additionalHeaders>
  <csregion_style>
    <firstLine>#region LicenseEOL/**</firstLine>
    <beforeEachLine> * </beforeEachLine>
    <endLine> */EOL#endregion</endLine>
    <firstLineDetectionPattern>#region.*^EOL/\*\*.*$</firstLineDetectionPattern>
    <lastLineDetectionPattern>\*/EOL#endregion"</lastLineDetectionPattern>
    <allowBlankLines>true</allowBlankLines>
    <multiLine>true</multiLine>
  </csregion_style>
</additionalHeaders>
```

## Style Definition Fields

| Field | Required | Description |
|---|---|---|
| `firstLine` | Yes | The opening line of the comment block |
| `endLine` | Yes | The closing line of the comment block |
| `beforeEachLine` | No | Characters prepended to each header content line |
| `afterEachLine` | No | Characters appended to each header content line |
| `skipLine` | No | Regex for a line at the top of the file that must be skipped (e.g., `<?xml ...>`) |
| `firstLineDetectionPattern` | Yes | Regex to detect the start of an existing header |
| `lastLineDetectionPattern` | Yes | Regex to detect the end of an existing header |
| `allowBlankLines` | No | Whether blank lines are permitted inside the header block (default: `false`) |
| `multiLine` | Yes | `true` for block comments (`/* */`), `false` for line comments (`# ...`) |
| `padLines` | No | For non-multiLine styles: pad each line so all `afterEachLine` markers align (default: `false`) |

### `EOL` Token

Use `EOL` in `firstLine` and `endLine` to represent the correct platform line separator. For example, `<!--EOL` produces `<!--\n` before the first content line.

## Example: C# Region Block

Header definition (`csregion_style.xml`):

```xml
<?xml version="1.0" encoding="ISO-8859-1"?>
<additionalHeaders>
  <csregion_style>
    <firstLine>#region LicenseEOL/**</firstLine>
    <beforeEachLine> * </beforeEachLine>
    <endLine> */EOL#endregion</endLine>
    <firstLineDetectionPattern>#region.*^EOL/\*\*.*$</firstLineDetectionPattern>
    <lastLineDetectionPattern>\*/EOL#endregion"</lastLineDetectionPattern>
    <allowBlankLines>true</allowBlankLines>
    <multiLine>true</multiLine>
  </csregion_style>
</additionalHeaders>
```

POM configuration:

```xml
<configuration>
  <headerDefinitions>
    <headerDefinition>csregion_style.xml</headerDefinition>
  </headerDefinitions>
  <mapping>
    <cs>CSREGION_STYLE</cs>
  </mapping>
</configuration>
```

Resulting header in `.cs` files:

```csharp
#region License
/**
 * Copyright (C) 2024 Acme Corp
 *
 * Licensed under the Apache License, Version 2.0
 */
#endregion
```

## Example: Built-in Javadoc Style (Reference)

```xml
<?xml version="1.0" encoding="ISO-8859-1"?>
<additionalHeaders>
  <javadoc_style>
    <firstLine>/**</firstLine>
    <beforeEachLine> * </beforeEachLine>
    <endLine> */</endLine>
    <afterEachLine></afterEachLine>
    <firstLineDetectionPattern>(\s|\t)*/\*.*$</firstLineDetectionPattern>
    <lastLineDetectionPattern>.*\*/(\s|\t)*$</lastLineDetectionPattern>
    <allowBlankLines>false</allowBlankLines>
    <multiLine>true</multiLine>
    <padLines>false</padLines>
  </javadoc_style>
</additionalHeaders>
```

## Example: Built-in XML Style (Reference)

```xml
<?xml version="1.0" encoding="ISO-8859-1"?>
<additionalHeaders>
  <xml_style>
    <firstLine><![CDATA[<!--\n]]></firstLine>
    <beforeEachLine>    </beforeEachLine>
    <endLine><![CDATA[-->]]></endLine>
    <afterEachLine></afterEachLine>
    <skipLine><![CDATA[^<\?xml.*>$]]></skipLine>
    <firstLineDetectionPattern><![CDATA[(\s|\t)*<!--.*$]]></firstLineDetectionPattern>
    <lastLineDetectionPattern><![CDATA[.*-->(\s|\t)*$]]></lastLineDetectionPattern>
    <allowBlankLines>false</allowBlankLines>
    <multiLine>true</multiLine>
    <padLines>false</padLines>
  </xml_style>
</additionalHeaders>
```

All built-in styles can be browsed in the [test resources on GitHub](https://github.com/mathieucarbou/license-maven-plugin/tree/master/license-maven-plugin/src/test/resources/styles).
