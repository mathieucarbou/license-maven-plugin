# Table of contents

- [Table of contents](#table-of-contents)
- [Maven License Plugin (Official & Original)](#maven-license-plugin-official--original)
  - [Features](#features)
  - [Links](#links)
  - [Contributors](#contributors)
  - [Releases](#releases)
  - [Documentation](#documentation)
    - [Plugin declaration](#plugin-declaration)
    - [Reports](#reports)
    - [Goals](#goals)
    - [Configuration](#configuration)
    - [License templates](#license-templates)
    - [Properties and placeholders](#properties-and-placeholders)
    - [Supported comment types](#supported-comment-types)
    - [Changing header style definitions](#changing-header-style-definitions)
    - [Dependency enforcement](#dependency-enforcement)
  - [Development](#development)
    - [Requirements](#requirements)
    - [Releasing a version](#releasing-a-version)
    - [Generate site from any branch or tag](#generate-site-from-any-branch-or-tag)

# Maven License Plugin (Official & Original)

Basically, when you are developing a project either in open source or in a company, you often need to add at the top of your source files a license to protect your work. I didn't find any maven plugin on Internet to help you maintain these license headers. By maintaining, i mean checking if the header is here or not, generating a report and of course having the possibility to update / reformat missing license headers.

## Features

* Check if header is missing in some source file (`check` goal).
* Add headers if missing (`format` goal).
* Update existing header with the new one (`format` goal).
* Remove existing header (`remove` goal).
* Custom mappings: enables easy support of new file extensions.
* Variable replacement: you can add some variable in your header, such as ${year} or ${owner} and they will be replaced by the corresponding values taken from the pom or system properties.
* Dependency enforcement: optionally fail the build if the dependencies do not meet your license policies

## Links

- __Build Status:__ [![Java CI](https://github.com/mathieucarbou/license-maven-plugin/actions/workflows/ci.yaml/badge.svg)](https://github.com/mathieucarbou/license-maven-plugin/actions/workflows/ci.yaml)
- __Issues:__ https://github.com/mathieucarbou/license-maven-plugin/issues
- __License:__ [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0)

## Contributors

* [@Flamenco](https://github.com/Flamenco)
* [@McFoggy](https://github.com/McFoggy)
* [@adamretter](https://github.com/adamretter)
* [@akomakom](https://github.com/akomakom)
* [@ankon](https://github.com/ankon)
* [@christopherlakey](https://github.com/christopherlakey)
* [@dbwiddis](https://github.com/dbwiddis)
* [@eincs](https://github.com/eincs)
* [@geoff-wasilwa](https://github.com/geoff-wasilwa)
* [@hazendaz](https://github.com/hazendaz)
* [@hgschmie](https://github.com/hgschmie)
* [@jdhoek](https://github.com/jdhoek)
* [@jfim](https://github.com/jfim)
* [@jgerken](https://github.com/jgerken)
* [@liry](https://github.com/liry)
* [@masakimu](https://github.com/masakimu)
* [@mathieucarbou](https://github.com/mathieucarbou)
* [@mcculls](https://github.com/mcculls)
* [@mirabilos](https://github.com/mirabilos)
* [@peterlynch](https://github.com/peterlynch)
* [@ppalaga](https://github.com/ppalaga)
* [@rhuss](https://github.com/rhuss)
* [@rmannibucau](https://github.com/rmannibucau)
* [@rnc](https://github.com/rnc)
* [@rremer](https://github.com/rremer)
* [@sebhoss](https://github.com/sebhoss)
* [@shane-hp](https://github.com/shane-hp)
* [@stain](https://github.com/stain)
* [@vromero](https://github.com/vromero)
* [@yoosiba](https://github.com/yoosiba)
* [@philippn](https://github.com/philippn)

Please let me know if your name is missing!

## Releases

Available in Maven Central Repository: https://repo1.maven.org/maven2/com/mycila/license-maven-plugin/

* [4.2.rc2](https://repo1.maven.org/maven2/com/mycila/license-maven-plugin/4.2.rc2/) (2022-01-10) - see [issues and pull requests](https://github.com/mathieucarbou/license-maven-plugin/milestone/18?closed=1)
* [4.2.rc1](https://repo1.maven.org/maven2/com/mycila/license-maven-plugin/4.2.rc1/) (2021-09-19) - see [issues and pull requests](https://github.com/mathieucarbou/license-maven-plugin/milestone/18?closed=1)
* [4.1](https://repo1.maven.org/maven2/com/mycila/license-maven-plugin/4.1/) (2021-04-23) - see [issues and pull requests](https://github.com/mathieucarbou/license-maven-plugin/milestone/17?closed=1)
* [4.0](https://repo1.maven.org/maven2/com/mycila/license-maven-plugin/4.0/) (2021-03-30) - see [issues and pull requests](https://github.com/mathieucarbou/license-maven-plugin/milestone/16?closed=1)
* [3.0](https://repo1.maven.org/maven2/com/mycila/license-maven-plugin/3.0/) (2016-08-20) - see [issues and pull requests](https://github.com/mathieucarbou/license-maven-plugin/issues?q=milestone%3A3.0)
* [2.11](https://repo1.maven.org/maven2/com/mycila/license-maven-plugin/2.11/) (2015-04-20) - see [issues and pull requests](https://github.com/mathieucarbou/license-maven-plugin/issues?q=milestone%3A2.11)
* [2.10](https://repo1.maven.org/maven2/com/mycila/license-maven-plugin/2.10/) (2015-03-16) - see [issues and pull requests](https://github.com/mathieucarbou/license-maven-plugin/issues?q=milestone%3A2.8)

## Documentation

### Plugin declaration

```xml
<plugin>
  <groupId>com.mycila</groupId>
  <artifactId>license-maven-plugin</artifactId>
  <version>4.1</version>
  <configuration>
    <properties>
      <owner>Mycila</owner>
      <email>mathieu.carbou@gmail.com</email>
    </properties>
    <licenseSets>
      <licenseSet>
        <header>com/mycila/maven/plugin/license/templates/APACHE-2.txt</header>
        <excludes>
          <exclude>**/README</exclude>
          <exclude>src/test/resources/**</exclude>
          <exclude>src/main/resources/**</exclude>
        </excludes>
      </licenseSet>
    </licenseSets>
  </configuration>
</plugin>
```

Note: `inlineHeader` can also be used instead of `header` to specify directly the header content in the POM.

__For [Multi-Licensing](https://en.wikipedia.org/wiki/Multi-licensing)__

If your source code makes use of multi-licensing, then instead of
a `<header>` or `<inlineHeader>` element in the configuration
you can use a `<multi>` element.

The `<multi>` element allows you to specify an optional preamble,
one or more header (or inlineHeader) and separators between them. These
options are concatenated together to produce a header template.

```xml
<plugin>
  <groupId>com.mycila</groupId>
  <artifactId>license-maven-plugin</artifactId>
  <version>4.1</version>
  <configuration>
    <licenseSets>
      <licenseSet>
        <multi>
          <preamble><![CDATA[This product is dual-licensed under both the GPLv2 and Apache 2.0 License.]]></preamble>
          <header>GPL-2.txt</header>
          <separator>======================================================================</separator>
          <header>com/mycila/maven/plugin/license/templates/APACHE-2.txt</header>
        </multi>
        <excludes>
          <exclude>**/README</exclude>
          <exclude>src/test/resources/**</exclude>
          <exclude>src/main/resources/**</exclude>
        </excludes>
      </licenseSet>
    </licenseSets>
    <properties>
      <owner>Mycila</owner>
      <email>mathieu.carbou@gmail.com</email>
    </properties>
  </configuration>
</plugin>
```

### Reports

The detailed Maven Plugin Documentation generated for each build is available here:

__NOTE__: Between versions 4.0 and 3.0 the configuration syntax has been changed. The
plugin now has the concept of *License Sets*, which allow you to work with one or
more license configurations in a single execution of the plugin. In simple terms, a `<licenseSet>`
wraps the previous configuration options for a license. The previous
configuration syntax is still supported but deprecated, and may be removed in future.

- [4.2-SNAPSHOT](https://mycila.carbou.me/license-maven-plugin/reports/4.2-SNAPSHOT/)
- [4.2.rc2](https://mycila.carbou.me/license-maven-plugin/reports/4.2.rc2/license-maven-plugin/plugin-info.html)
- [4.1](https://mycila.carbou.me/license-maven-plugin/reports/4.1/license-maven-plugin/plugin-info.html)
- [4.0](https://mycila.carbou.me/license-maven-plugin/reports/4.0/license-maven-plugin/plugin-info.html)
- [3.0](https://mycila.carbou.me/license-maven-plugin/reports/3.0/plugin-info.html)
- [2.3](https://mycila.carbou.me/license-maven-plugin/reports/2.3/plugin-info.html)
- [2.2](https://mycila.carbou.me/license-maven-plugin/reports/2.2/plugin-info.html)
- [2.0](https://mycila.carbou.me/license-maven-plugin/reports/2.0/plugin-info.html)

__WARNING__: there is good chances the latest version is greater than latest documentation, if nothing has changed concerning the plugin configurations.

### Goals

* `license:check`: verify if some files miss license header. This goal is attached to the verify phase if declared in your pom.xml like above.
* `license:format`: add the license header when missing. If a header is existing, it is updated to the new one.
* `license:remove`: remove existing license header


### Configuration

The table below shows all the available options you can use in the configure section of the plugin. A lot of are also available from the command-line. To use them, simply launch your maven command with a property like `-Dproperty=value` (i.e. `mvn license:check -Dlicense.header=src/etc/header.txt`)

All plugin configuration options are described in the [Detailed Maven documentation](#detailed-maven-documentation) but here are some details.

- `useDefaultExcludes`: The default exclusion list can be found [here](https://github.com/mathieucarbou/license-maven-plugin/blob/master/license-maven-plugin/src/main/java/com/mycila/maven/plugin/license/Default.java)


### License templates

Maven license plugin comes with the following license templates:

- AGPL 3
- APACHE 2
- BSD 2, 3, 4
- Business Source License 1.1
- Commons Clause
- CPAL 1
- GPL 2-ONLY, 2, 3-ONLY, 3
- LGPL 2.1-ONLY, 2.1, 3-ONLY, 3
- MirOS
- MIT
- MPL 1, 2
- Server Side Public License
- UNLICENSE
- WTFPL

You can find those license templates with preconfigured placeholders [here](https://github.com/mathieucarbou/license-maven-plugin/tree/master/license-maven-plugin/src/main/resources/com/mycila/maven/plugin/license/templates)

### Properties and placeholders

Properties which can be used as placeholder comes from:

- Environment variables
- POM properties
    - `project.groupId`
    - `project.artifactId`
    - `project.version`
    - `project.name`
    - `project.description`
    - `project.inceptionYear`
    - `project.url`
    - `project.organization.name`
    - `project.organization.url`
- Per-Document properties
    - `file.name`
- Plugin configuration properties (from `<properties>` tag)
- System properties

Properties are built per-document. You can provide a dependency JAR file to the plugin which contains an implementation of `com.mycila.maven.plugin.license.PropertiesProvider`:

```java
public interface PropertiesProvider {
  Map<String, String> getAdditionalProperties(AbstractLicenseMojo mojo, Properties currentProperties, Document document);
}
```

You have access to the Mojo, the current built properties and the document being checked or formatted. The plugin uses the JDK ServiceLoader mechanism to find all `PropertiesProvider` implementations on the plugin classpath and execute them. Thus, just add the implementation class name in the file `META-INF/services/com.mycila.maven.plugin.license.PropertiesProvider` in your JAR file.


### Supported comment types

The plugin has been designed so that it is very easy to add new supports for new sorts of comment. The plugin currently support these types of comment:

- `JAVADOC_STYLE` (Java-like comments): *.css, *.cs, *.as, *.aj, *.c, *.h, *.cpp

   ```java
   /**
    * My comment
    */
   ```

- `JAVAPKG_STYLE` (like Javadoc, but only for files that are in a Java package, skips the first line): not assigned to a file extension by default (see __Java packages__ below for how to enable it)

   ```java
   package com.example;
   /*-
    * My comment
    */
   ```

- `XML_STYLE` (XML-like comments): *.pom, *.xml, *.xhtml, *.mxml, *.dtd, *.xsd, *.jspx, *.fml, *.xsl, *.html, *.htm, *.kml, *.gsp, *.tld

   ```xml
   <!--
     My comment
   -->
   ```

- `XML_PER_LINE` (alternate XML-like comments)

   ```xml
   <!-- My first comment  -->
   <!-- My second comment -->
   ```

(automatically right-adjusts the closing comment)


- `DOUBLETILDE_STYLE` (APT-like comments): *.apt

   ```
   ~~ My comment
   ```

- `SCRIPT_STYLE` (Property file or shell comments): *.properties, *.sh, *.py, *.rb, *.pl, *.pm, *.yml, *.yaml

   ```bash
   # My comment
   ```

- `HAML_STYLE`: *.haml, *.scaml

   ```haml
   -# My comment
   ```

- `BATCH` (Windows batch comments): *.bat, *.cmd

   ```batch
   @REM My comment
   ```

- `TEXT` (Text like comments): *.txt

   ```
   ====
       My comment
   ====
   ```

(4 spaces, then the lines of the header)

- `DOUBLEDASHES_STYLE` (Sql like comments): *.sql, *.adb, *.ads, *.e

   ```sql
   --
   -- test comment
   --
   ```

- `DYNASCRIPT_STYLE` (JSP like comments): *.jsp

   ```jsp
   <%--
       comment
   --%>
   ```

- `FTL` (FreeMarker like comments): *.ftl

   ```
   <#--
       comment
   -->
   ```

- `FTL_ALT` (FreeMarker Alternative Syntax comments)

   ```
   [#ftl ...]
   [#--
       comment
   --]
   ```

- `SHARPSTAR_STYLE` (Velocity templates comments): *.vm

   ```
   #*
       comment
   *#
   ```

- `SEMICOLON_STYLE` (Assembler like comments): *.asm

   ```
   ;
   ; comment
   ;
   ```

- `BRACESSTAR_STYLE` (Delphi like comments): *.pas

   ```pascal
   {*
    * comment
    *}
   ```

- `APOSTROPHE_STYLE` (VisualBasic like comments): *.bas

   ```basic
   '
   ' comment
   '
   ```

- `EXCLAMATION_STYLE` (Fortran like comments): *.f

   ```fortran
   !
   ! comment
   !
   ```

- `SLASHSTAR_STYLE` (JavaScript like comments): *.js, *.fx, *.java, *.groovy, *.kt, *.scala

   ```javascript
   /*
    * comment
    */
   ```

- `DYNASCRIPT3_STYLE` (Coldfusion like comments): *.cfc, *.cfm

   ```
   <!---
       comment
   --->
   ```

- `PERCENT_STYLE` (Teχ like comments): *.cls, *.sty, *.tex

   ```tex
   % comment
   ```

- `PERCENT3_STYLE` (Erlang like comments): *.erl, *.hrl

   ```erlang
   %%%
   %%% comment
   %%%
   ```

- `EXCLAMATION3_STYLE` (Lisp like comments): *.el

   ```elisp
   !!!
   !!! comment
   !!!
   ```

- `LUA` (Lua like comments): *.lua

   ```lua
   --[[
   comment
   ]]
   ```

- `ASP` (Asp like comments): *.asp

   ```asp
   <%
   ' comment
   %>
   ```

- `PHP` (PHP comments): *.php

   ```php
   /*
    * comment
    */
   ```

(inserted after the <?php> tag)

- `DOUBLESLASH_STYLE` (often used comments style)

   ```php
   //
   // comment
   //
   ```

- `MVEL_STYLE`

   ```mvel
   @comment{
     Copyright (C) ${year} http://code.google.com/p/maven-license-plugin/

     Licensed under the Apache License, Version 2.0 (the "License");
     you may not use this file except in compliance with the License.
     You may obtain a copy of the License at

               http://www.apache.org/licenses/LICENSE-2.0

     Unless required by applicable law or agreed to in writing, software
     distributed under the License is distributed on an "AS IS" BASIS,
     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     See the License for the specific language governing permissions and
     limitations under the License.
    }
   ```

- `ASCIIDOC_STYLE`

   ```asciidoc
   ////
     // Copyright (C) ${year} http://code.google.com/p/maven-license-plugin/
     //
     // Licensed under the Apache License, Version 2.0 (the "License");
     // you may not use this file except in compliance with the License.
     // You may obtain a copy of the License at
     //
     //         http://www.apache.org/licenses/LICENSE-2.0
     //
     // Unless required by applicable law or agreed to in writing, software
     // distributed under the License is distributed on an "AS IS" BASIS,
     // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     // See the License for the specific language governing permissions and
     // limitations under the License.
   ////
   ```

All the styles can be found here: https://github.com/mathieucarbou/license-maven-plugin/tree/master/license-maven-plugin/src/test/resources/styles

__Custom mapping__

The plugin enables you to add any other mapping you want.* I.e., if you are developing a Tapestry web application, you may need to add license header in .jwc files and .application files. since these files are xml files, you only need to add in your project pom the following mapping for the license-maven-plugin:

```xml
<mapping>
  <jwc>XML_STYLE</jwc>
  <application>XML_STYLE</application>
  <apt.vm>DOUBLETILDE_STYLE</apt.vm>
  <vm>SHARPSTAR_STYLE</vm>
  <apt>DOUBLETILDE_STYLE</apt>
</mapping>
```

You can use composed-extensions like *.apt.vm and redefine them, but you will have to nake sure that the mapping of `apt.vm` is _before_ the mapping of the `vm` extension. The order in the mapping section is important: extensions seen first take precedence.

__Java packages__

Another use case for custom mappings is when writing Java code in packages; the licence header should come *after* the package declaration line in this case. Simply add this to the plugin configuration:

```xml
<mapping>
  <java>JAVAPKG_STYLE</java>
</mapping>
```

### Changing header style definitions

In license-maven-plugin, each header style is defined by patterns to detect it and also strings to insert it correctly in files. If we take for example the Javadoc style header definition. It is defined as follow:

```xml
<?xml version="1.0" encoding="ISO-8859-1"?>
<additionalHeaders>
  <javadoc_style>
    <firstLine>/**</firstLine>
    <beforeEachLine> * </beforeEachLine>
    <endLine> */</endLine>
    <afterEachLine></afterEachLine>
    <!--skipLine></skipLine-->
    <firstLineDetectionPattern>(\s|\t)*/\*.*$</firstLineDetectionPattern>
    <lastLineDetectionPattern>.*\*/(\s|\t)*$</lastLineDetectionPattern>
    <allowBlankLines>false</allowBlankLines>
    <multiLine>true</multiLine>
    <padLines>false</padLines>
  </javadoc_style>
</additionalHeaders>
```

And for XML:

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

With the `headerDefinitions` option, you can redefine existing header styles and also add some if we do not support the styles you want yet. You just have to provide a list of `headerDefinition` containing a resource name. Like the header, the resource is searched on the file system, in the classpath of the project, the plugin and also as a URL.

__Full Example__

This page will show you how you can define extended header definitions to fit your needs. The next example will define headers in a _region_ area allowed in C# source files:

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

* The `EOL` string will be replaced with the proper end of line depending the file format your are processing.
* We also have defined the _skipLine_ attribute to skip the region tags (which starts with a '#')
* `allowBlankLines` allows you to define if this header style supports blank lines in it or not. In example, in XML headers, you could have blank lines after the <!-- and before --> because XML delimiters delimit a multiline block. When you work with script style comments like in Ruby, Porperties files, the # character delimit a comment for only one line. So when you create the header, for it to be uniform, you place # on each line. So allowBlankLines will be false.
* `multiline` specifies if your header has tokens to delimit a multiline comment of if the tokens are a one-line comment. I.E.: XML style comments are multiline whereas script style comment where each line starts with # are not multiline

You now have to add this new header definition file to the plugin configuration. It is done as the following in your pom:

```xml
<headerDefinitions>
  <headerDefinition>yourdefinition.xml</headerDefinition>
</headerDefinitions>
```

You now have to add the new mapping for `*.cs` files to use this new header definition. It is done as the following in your pom:

```xml
<mapping>
  <cs>CSREGION_STYLE</cs>
</mapping>
```

And it should generate headers like:

```csharp
    #region License
    /**
     * Copyright (C) 2008 http://code.google.com/p/license-maven-plugin/
     *
     * Licensed under the Apache License, Version 2.0 (the "License");
     * you may not use this file except in compliance with the License.
     * You may obtain a copy of the License at
     *
     *         http://www.apache.org/licenses/LICENSE-2.0
     *
     * Unless required by applicable law or agreed to in writing, software
     * distributed under the License is distributed on an "AS IS" BASIS,
     * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     * See the License for the specific language governing permissions and
     * limitations under the License.
     */
    #endregion
```

__Inline styles__

This is also possible to configure new header style inline within the POM without using external files. This is a useful feature for module inheritance.

The following example will redefine the header file for text files:

```xml
<plugin>
    <groupId>com.mycila</groupId>
    <artifactId>license-maven-plugin</artifactId>
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
</plugin>
```

### Dependency enforcement

This plugin can be configured to break the build when its dependencies do not adhere to a configured license policy. This plugin relies on the accuracy of the `<licenses>` maven property configured in the pom of artifacts your project declares in `<dependencies>`.

There are currently three types of policies which can be enforced:

1. LICENSE_URL - strict match on the URL element of a License
2. LICENSE_NAME - strict match on the name of a License
3. ARTIFACT_PATTERN - regex on a groupdId:artifactId

Rules can be defined in the plugin configuration like so:

```xml
<plugin>
    <groupId>com.mycila</groupId>
    <artifactId>license-maven-plugin</artifactId>
    <configuration>
        <dependencyEnforce>true</dependencyEnforce>
        <dependencyExceptionMessage>A custom error message for how to handle approvals in your organization</dependencyExceptionMessage>
        <dependencyPolicies>
            <dependencyPolicy>
                <type>LICENSE_NAME</type>
                <rule>APPROVE</rule>
                <value>Public Domain</value>
            </dependencyPolicy>
            <dependencyPolicy>
                <type>LICENSE_URL</type>
                <rule>APPROVE</rule>
                <value>https://www.apache.org/licenses/LICENSE-2.0.txt</value>
            </dependencyPolicy>
            <dependencyPolicy>
                <type>ARTIFACT_PATTERN</type>
                <rule>APPROVE</rule>
                <value>com.mycila.*</value>
            </dependencyPolicy>
            <dependencyPolicy>
                <type>ARTIFACT_PATTERN</type>
                <rule>DENY</rule>
                <value>com.example.*</value>
            </dependencyPolicy>
            <dependencyPolicy>
                <type>ARTIFACT_PATTERN</type>
                <rule>ALLOW</rule>
                <value>com.example.subpackage:other-artifact:jar:1.0.0</value>
            </dependencyPolicy>
        </dependencyPolicies>
    </configuration>
</plugin>
```

There is also an implicit default deny artifact pattern policy, so if you enable dependency enforcement and have any dependencies, you must configure a policy. The ordering of the declared dependencyPolicies does not matter, and in aggregate they will be enforced in the following way:

1. defaultPolicy included in the plugin, matching all artifacts with a deny rule
2. APPROVE policies
3. DENY policies

Given the above configuration example, you could state:

* the allow rule for com.example.subpackage:other-artifact:jar:1.0.0 will never do anything, because there is a deny rule for com.example.*
* all com.mycila artifacts will be allowed, regardless of their license
* any other artifact with a license name of 'Public Domain' will be allowed
* any other artifact with a license URL of explicitely 'https://www.apache.org/licenses/LICENSE-2.0.txt' will be allowed
* all other artifacts will fail the build with the following message header: "A custom error message for how to handle approvals in your organization" along with the list of artifacts which violated the policies


## Development

### Requirements

- Java 1.8
- Verify with: `mvn -v` that the right version is used
- Replace my GPG key (EDEA921A) by yours in the following lines

### Releasing a version

Example: `4.0.rc1`

```bash
./mvnw release:prepare -DreleaseVersion=4.0.rc1 -Dtag=license-maven-plugin-4.0.rc1 -DdevelopmentVersion=4.0-SNAPSHOT
./mvnw release:perform -Darguments="-Dgpg.keyname=EDEA921A -Dmaven.site.deploy.skip=true"
```

Example: `4.2.rc2`

```bash
./mvnw release:prepare -DreleaseVersion=4.2.rc2 -Dtag=license-maven-plugin-4.2.rc2 -DdevelopmentVersion=4.2-SNAPSHOT
./mvnw release:perform -Darguments="-Dgpg.keyname=EDEA921A -Dmaven.site.deploy.skip=true"
```

Example: `4.0`

```bash
./mvnw release:prepare -DreleaseVersion=4.0 -Dtag=license-maven-plugin-4.0 -DdevelopmentVersion=4.1-SNAPSHOT
./mvnw release:perform -Darguments="-Dgpg.keyname=EDEA921A -Dmaven.site.deploy.skip=true"
```

Then, go to https://oss.sonatype.org/ to "close and release".
Then you should see a few minutes later the new version at https://repo1.maven.org/maven2/com/mycila/license-maven-plugin/

### Generate site from any branch or tag

```bash
./mvnw verify site -DperformRelease=true -Dgpg.skip=true
```
