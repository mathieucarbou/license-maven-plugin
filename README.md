**Table of Contents**

- [Maven License Plugin](#maven-license-plugin)
	- [Maven Repository](#maven-repository)
	- [Documentation](#documentation)

# Maven License Plugin #

This small project enables to export your classes easily through JMX.

__Issues:__ https://github.com/mycila/maven-license-plugin/issues

[![Build Status](https://travis-ci.org/mycila/maven-license-plugin.png?branch=master)](https://travis-ci.org/mycila/maven-license-plugin)
[![githalytics.com alpha](https://cruel-carlota.pagodabox.com/fbed748c90019f0149e7fbf0947525f9 "githalytics.com")](http://githalytics.com/mycila/maven-license-plugin)

## Maven Repository ##

 - __Releases__ 
 - 

Available in Maven Central Repository: http://repo1.maven.org/maven2/com/mycila/maven-license-plugin/

 - __Snapshots__
 
Available in OSS Repository:  https://oss.sonatype.org/content/repositories/snapshots/com/mycila/maven-license-plugin/

__Maven dependency__

    <dependency>
        <groupId>com.mycila</groupId>
        <artifactId>maven-license-plugin</artifactId>
        <version>X.Y.ga</version>
    </dependency>

## Documentation ##


= Introduction =

<strong><font color='red'>2010-12-12 - Version 1.9.0has been released.</font> See [ReleaseNotes release notes]</strong>

*maven-license-plugin* is a [http://maven.apache.org/ Maven 2] plugin that help you managing license headers in source files.

Basically, when you are developing a project either in open source or in a company, you often need to add at the top of your source files a license to protect your work.

I didn't find any maven plugin on Internet to help you maintain these license headers. By maintaining, i mean checking if the header is here or not, generating a report and of course having the possibility to update / reformat missing license headers.

I only found [http://mojo.codehaus.org/rat-maven-plugin/ RAT Maven plugin] but this plugin only does a check.

<wiki:gadget url="http://www.ohloh.net/p/14921/widgets/project_partner_badge.xml" border="0" height="53"/>

= Features =

  * *Check*: check if header is missing in some source file
  * *Reformat*: add headers if missing
  * *Remove*: can remove existing header
  * *Update*: update existing header with a new one
  * *Custom mappings*: enables easy support of new file extensions
  * *Variable replacement:* You can add some variable in your header, such as ${year}, ${owner} and they will be replaced by the corresponding values taken from the pom or system properties.

= Be up to date ! =

For the most recent version, see the [ReleaseNotes Release Notes].

You can also subscribe to the google group mailing lists or RSS feeds to get the latest announcements, issues updates, svn updates, ...
See [MailingLists the mailing lists section].

= How to use and configure the plugin =

The usage of the plugin is very simple. It is described in detail in *[HowTo the How To page]*.

To configure the plugin, see *[Configuration the configuration reference guide]*.

You can also have a check on the maven website of this plugin at http://mathieu.carbou.free.fr/p/maven-license-plugin/plugin-info.html

= Supported formats =

All listed [SupportedFormats there]

= variable replacement in header =

You can define some variable in your header, and they will be replaced when the header file will be read. The values of the properties are taken first from the command line (java system properties), then from the plugin properties, then from the system properties.

See [Configuration the configuration reference guide] to see how to use it.

= Getting help =

  * Maven 2 project website, with plugin description: http://mathieu.carbou.free.fr/p/maven-license-plugin/plugin-info.html
  * My website: http://mathieu.carbou.free.fr/
  * Issue tab of this project
  * Email...

== Other interesting projects ==

 * Mycila : http://code.mycila.com/


<wiki:gadget url="http://www.ohloh.net/p/14921/widgets/project_languages.xml" width="360" height="200" border="0"/>
<wiki:gadget url="http://www.ohloh.net/p/14921/widgets/project_cocomo.xml" width="340" height="270" border="0"/>
<wiki:gadget url="http://www.ohloh.net/p/14921/widgets/project_basic_stats.xml" width="340" height="270" border="0"/>


==================================

#summary maven-license-plugin configuration reference guide
<wiki:toc max_depth="2" /> 
= Plugin info on maven website = 

See http://mathieu.carbou.free.fr/p/maven-license-plugin/plugin-info.html

= Available goals =

  * *[http://mathieu.carbou.free.fr/p/maven-license-plugin/check-mojo.html license:check]*: verify if some files miss license header
  * *[http://mathieu.carbou.free.fr/p/maven-license-plugin/format-mojo.html license:format]*: add the license header when missing. If a header is existing, it is updated to the new one.
  * *[http://mathieu.carbou.free.fr/p/maven-license-plugin/remove-mojo.html license:remove]*: remove existing license header

= maven-license-plugin configuration options =

A lot of configuration options can be passed to the command line as parameters to override those in the POM. This is described below.

Here is an example of a full declaration of the plugin with all its options

{{{
<build>
    <plugins>
        <plugin>
            <groupId>com.mycila.maven-license-plugin</groupId>
            <artifactId>maven-license-plugin</artifactId>
            <configuration>
                <basedir>${basedir}</basedir>
                <header>${basedir}/src/etc/header.txt</header>
                <validHeaders>
                    <validHeader>/otherSupportedHeader.txt</validHeader>
                    <validHeader>http://www.company.com/yetAnotherSupportedHeader.txt</validHeader>
                </validHeaders>
                <quiet>false</quiet>
                <failIfMissing>true</failIfMissing>
                <aggregate>false</aggregate>
                <includes>
                    <include>src/**</include>
                    <include>**/test/**</include>
                </includes>
                <excludes>
                    <exclude>target/**</exclude>
                    <exclude>.clover/**</exclude>
                </excludes>
                <useDefaultExcludes>true</useDefaultExcludes>
                <mapping>
                    <jwc>XML_STYLE</jwc>
                    <application>XML_STYLE</application>
                    <myFileExtension>JAVADOC_STYLE</myFileExtension>
                </mapping>
                <useDefaultMapping>true</useDefaultMapping>
                <properties>
                    <year>${project.inceptionYear}</year>
                    <email>my@email.com</email>
                </properties>
                <encoding>UTF-8</encoding>
                <headerDefinitions>
                    <headerDefinition>def1.xml</headerDefinition>
                    <headerDefinition>def2.xml</headerDefinition>
                </headerDefinitions>
            </configuration>
            <executions>
                <execution>
                    <goals>
                        <goal>check</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
}}}

The table below shows all the available options you can use in the configure section of the plugin. A lot of are also available from the command-line. To use them, simply launch your maven command with a property like `-Dproperty=value` (i.e. `mvn license:check -Dlicense.header=src/etc/header.txt`)

|| *option* || *status* || *default* || *command-line* || *description* ||
|| *_basedir_* || optional || ${basedir} || -Dlicense.basedir=folder || Specify the folder as the base directory for the search ||
|| *_header_* || *required* || || -Dlicense.header=path to header file || Location of the header. It can be a relative path, absolute path, classpath resource, any URL. The plugin first check if the name specified is a relative file, then an absolute file, then in the claspath. If not found, it tries to construct a URL from the location. An example of the content for Apache 2 license is [http://code.google.com/p/maven-license-plugin/wiki/HowTo here] ||
|| *_quiet_* || optional || false || -Dlicense.quiet=true || If you do not want to see the list of file having a missing header, you can add the quiet flag that will shorten the output ||
|| *_failIfMissing_* || optional || true || -Dlicense.failIfMissing=false || You can set this flag to false if you do not want the build to fail when some headers are missing. This flag is only available with goal `check` ||
|| *_aggregate_* || optional || false|| -Dlicense.aggregate=false || You can set this flag to true if you want to check the headers for all modules of your project. Only used for multi-modules projects, to check for example the header licenses in the parent pom for all sub modules ||
|| *_includes_* || optional || `**` (all) || || By default all files are included. You can reduce the set of included files by adding include tags with patterns ||
|| *_excludes_* || optional || empty || || You can exclude some files in your project from the check by specifying a list of pattern ||
|| *_useDefaultExcludes_* || optional || true || -Dlicense.useDefaultExcludes=false || Specify if you want to use default exclusions besides the files you have excluded. Default exclusion removes CVS and SVN folders, IDE descriptors and so on. The default value of these exclusions is shown below this table ||
|| *_mapping_* || optional || empty || || This section is very useful when you want to customize the supported extensions. Is your project is using file extensions not supported by default by this plugin, you can add a mapping to attach the extension to an existing type of comment. The tag name is the new extension name to support, and the value is the name of the comment type to use. The list of the comment type names is listed on the page  [http://code.google.com/p/maven-license-plugin/ here] and below the table ||
|| *_useDefaultMapping_* || optional || true || -Dlicense.useDefaultMapping=false || Specify if you want to use or not the default extension mapping for supported file extensions. If you do not use it, you will have to add mappings manual for each extension you want to use. See more details below ||
|| *_properties_* || optional || empty || || You can set here some properties that you want to use when reading the header file. You can use in your header file some properties like ${year}, ${owner} or whatever you want for the name. They will be replaced when the header file is read by those you specified in the command line, in the POM and in system environment. ||
|| *_encoding_* || optional || ${file.encoding} || -Dlicense.encoding=encoding || Specify the encoding of your files. Default to current system encoding ||
|| *_headerDefinitions_* || optional || empty ||  || Enables to redefine the header detection patterns and the header templates ||
|| *_dryRun_* || optional || false || -Dlicense.dryRun=true || If dryRun is enabled, calls to license:format and license:remove will not overwrite the existing file but instead write the result to a new file with the same name but ending with `.licensed` ||
|| *_keywords_* || optional || copyright || || Specify the list of keywords to use to detect a header. A header must include all keywords to be valid. By default, the word 'copyright' is used. Detection is done case insensitive. ||
|| *_skip_* || optional || false || -Dlicense.skip=true || Skip the plugin execution if true ||
|| *_skipExistingHeaders_* || optional || false || -Dlicense.skipExistingHeaders=true || Skip the formatting of the files which already contains a detected header ||
|| *_validHeaders_* || optional || ||  || Specifies additional header files to use when checking for the presence of a valid header in your sources. When using format goal, this property will be used to detect all valid headers that don't need formatting. When using remove goal, this property will be used to detect all valid headers that also must be removed. ||
|| *_strictCheck_* || optional || false || -Dlicense.strictCheck=true || Since version 1.8.0, maven-license-plugin supports strict checking of licenses. This will be the default behavior for all future majors releases. Set to true if you need a strict check against the headers. By default, the existence of a header is verified by taking the top portion of a file and checking if it contains the headers text, not considering special characters (spaces, tabs, ...). We highly recommend to set this option to true. It is by default set to false for backward compatibility. ||
|| *_concurrencyFactor_* || optional || 1.5 || -Dlicense.concurrencyFactor=2 || Maven license plugin uses concurrency to check license headers. This factor is used to control the number of threads used to check. The rule is: `<nThreads> = <number of cores> *  concurrencyFactor` ||

= Default excludes =

Patterns that are excluded by default when using `useDefaultExcludes` (default to true):

{{{
// Miscellaneous typical temporary files
"**/*~",
"**/#*#",
"**/.#*",
"**/%*%",
"**/._*",
"**/.repository/**",

// CVS
"**/CVS",
"**/CVS/**",
"**/.cvsignore",

// RCS
"**/RCS",
"**/RCS/**",

// SCCS
"**/SCCS",
"**/SCCS/**",

// Visual SourceSafe
"**/vssver.scc",

// Subversion
"**/.svn",
"**/.svn/**",

// Arch
"**/.arch-ids",
"**/.arch-ids/**",

//Bazaar
"**/.bzr",
"**/.bzr/**",

//SurroundSCM
"**/.MySCMServerInfo",

// Mac
"**/.DS_Store",

// Serena Dimensions Version 10
"**/.metadata",
"**/.metadata/**",

// Mercurial
"**/.hg",
"**/.hg/**",

// git
"**/.git",
"**/.git/**",

// BitKeeper
"**/BitKeeper",
"**/BitKeeper/**",
"**/ChangeSet",
"**/ChangeSet/**",

// darcs
"**/_darcs",
"**/_darcs/**",
"**/.darcsrepo",
"**/.darcsrepo/**",
"**/-darcs-backup*",
"**/.darcs-temp-mail",

// maven project's temporary files
"**/target/**",
"**/test-output/**",
"**/release.properties",
"**/pom.xml",
"**/dependency-reduced-pom.xml",

// code coverage tools
"**/cobertura.ser",
"**/.clover/**",

// eclipse project files
"**/.classpath",
"**/.project",
"**/.settings/**",

// IDEA projet files
"**/*.iml", "**/*.ipr", "**/*.iws",

// descriptors
"**/MANIFEST.MF",

// binary files - images
"**/*.jpg",
"**/*.png",
"**/*.gif",
"**/*.ico",
"**/*.bmp",
"**/*.tiff",
"**/*.tif",
"**/*.cr2",
"**/*.xcf",

// binary files - programs
"**/*.class",
"**/*.exe",

// checksum files
"**/*.md5",
"**/*.sha1",

// binary files - archives
"**/*.jar",
"**/*.zip",
"**/*.rar",
"**/*.tar",
"**/*.tar.gz",
"**/*.tar.bz2",
"**/*.gz",

// binary files - documents
"**/*.xls",

// ServiceLoader files
"**/META-INF/services/**"
}}}

= Supported comment types =

There are all described in [http://code.google.com/p/maven-license-plugin/ the Home page]. The list contains:

`java, xml, properties, apt, batch, text, sql, jsp, ftl, ...`

= Default mappings =

The default mapping is built using the file extension and the style of comment to use. By default, the mapping between supported extensions and comment type contains the supported extensions [http://code.google.com/p/maven-license-plugin/wiki/SupportedFormats here]. You can customize the default mapping by providing your own one:

{{{
<mapping>
    <java>JAVADOC_STYLE</java>
    <groovy>JAVADOC_STYLE</groovy>
    <js>JAVADOC_STYLE</js>
    <css>JAVADOC_STYLE</css>
    <xml>XML_STYLE</xml>
    <dtd>XML_STYLE</dtd>
    <xsd>XML_STYLE</xsd>
    <html>XML_STYLE</html>
    <htm>XML_STYLE</htm>
    <xsl>XML_STYLE</xsl>
    <fml>XML_STYLE</fml>
    <apt>DOUBLETILDE_STYLE</apt>
    <properties>SCRIPT_STYLE</properties>
    <sh>SCRIPT_STYLE</sh>
    <txt>TEXT</txt>
    <bat>BATCH</bat>
    <cmd>BATCH</cmd>
    <sql>DOUBLEDASHES_STYLE</sql>
    <jsp>DYNASCRIPT_STYLE</jsp>
    <ftl>FTL</ftl>
    <xhtml>XML_STYLE</xhtml>
    <vm>SHARPSTAR_STYLE</vm>
    <jspx>XML_STYLE</jspx>
</mapping>
}}}

= Variable replacement =

If you have a header that contains variable like this one:

{{{
Copyright (C) ${year} ${user.name} <${email}>

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
}}}

The plugin will try to replace them using the properties supplied in the command line, then those supplied in the POM, and then those from the system environment.

To test, you can launch the check goal in debug mode like this:

`mvn -X license:check -Dyear=2010`

Outputs for me:

{{{
Copyright (C) 2010 kha <my@email.com>
}}}

Notice that the year 2008 specified in the POM above has been overridden by the java property. And since the user.name is already a java system property, you don't need to specify it.

= Debugging, controlling the output =

The plugin let you control the output by the *`license.quiet`* property.

But to see if the plugin is working well, if your header is parsed correctly with properties, you may need to activate the Maven debug flag *-X* to see the plugin debug output.

Example: `mvn -X license:check`

= Changing header style definitions =

In maven-license-plugin, each header style is defined by patterns to detect it and also strings to insert it correctly in files. If we take for example the Javadoc style header definition. It is defined as follow:

{{{
<?xml version="1.0" encoding="ISO-8859-1"?>
<additionalHeaders>
    <javadoc_style>
        <firstLine>/**</firstLine>
        <beforeEachLine> * </beforeEachLine>
        <endLine> */</endLine>
        <!--skipLine></skipLine-->
        <firstLineDetectionPattern>(\s|\t)*/\*.*$</firstLineDetectionPattern>
        <lastLineDetectionPattern>.*\*/(\s|\t)*$</lastLineDetectionPattern>
        <allowBlankLines>false</allowBlankLines>
        <isMultiline>true</isMultiline>
    </javadoc_style>
</additionalHeaders>
}}}

And for XML:

{{{
<?xml version="1.0" encoding="ISO-8859-1"?>
<additionalHeaders>
    <javadoc_style>
        <firstLine><![CDATA[<!--\n]]></firstLine>
        <beforeEachLine>    </beforeEachLine>
        <endLine><![CDATA[-->]]></endLine>
        <skipLine><![CDATA[^<\?xml.*>$]]></skipLine>
        <firstLineDetectionPattern><![CDATA[(\s|\t)*<!--.*$]]></firstLineDetectionPattern>
        <lastLineDetectionPattern><![CDATA[.*-->(\s|\t)*$]]></lastLineDetectionPattern>
        <allowBlankLines>false</allowBlankLines>
        <isMultiline>true</isMultiline>
    </javadoc_style>
</additionalHeaders>
}}}

With the *headerDefinitions* option, you can redefine existing header styles and also add some if we do not support the styles you want yet. You just have to provide a list of *headerDefinition* containing a resource name. Like the header, the resource is searched on the file system, in the classpath of the project, the plugin and also as a URL.

See [AdvancedHeaders Advanced Headers configuration] for more information

= Working with multi-module projects =

here is an example of configuration you can have in a parent pom, when working in a multimodule project:

{{{
<plugin>
    <inherited>false</inherited>
    <groupId>com.mycila.maven-license-plugin</groupId>
    <artifactId>maven-license-plugin</artifactId>
    <version>1.4.0</version>
    <configuration>
        <header>${basedir}/etc/header.txt</header>
        <failIfMissing>true</failIfMissing>
        <aggregate>true</aggregate>
        <properties>
            <owner>Mathieu Carbou</owner>
            <year>${project.inceptionYear}</year>
            <email>mathieu.carbou@gmail.com</email>
        </properties>
        <excludes>
            <exclude>LICENSE.txt</exclude>
            <exclude>**/src/test/resources/**</exclude>
            <exclude>**/src/test/data/**</exclude>
        </excludes>
    </configuration>
    <executions>
        <execution>
            <id>check-headers</id>
            <phase>verify</phase>
            <goals>
                <goal>check</goal>
            </goals>
        </execution>
    </executions>
</plugin>
}}}


==================================


#summary Supported formats

= Supported comments =

The plugin has been designed so that it is very easy to add new supports for new sorts of comment.

The plugin currently support these types of comment:

  * *JAVADOC_STYLE* (Java-like comments)

{{{
/**
 * My comment
 */
}}}

  * *XML_STYLE* (XML-like comments)

{{{
<!--
    My comment
-->
}}}

  * *DOUBLETILDE_STYLE* (APT-like comments)

{{{
~~ My comment
}}}

  * *SCRIPT_STYLE* (Property file or shell comments)

{{{
# My comment
}}}

  * *HAML_STYLE*

{{{
-# My comment
}}}

  * *BATCH* (Windows batch comments)

{{{
@REM My comment
}}}

  * *TEXT* (Text like comments)

{{{
====
    My comment
====
}}}

(4 spaces, then the lines of the header)

  * *DOUBLEDASHES_STYLE* (Sql like comments)

{{{
--
-- test comment
--
}}}

  * *DYNASCRIPT_STYLE* (JSP like comments)

{{{
<%--
    comment
--%>
}}}

  * *FTL* (FreeMarker like comments)

{{{
<#--
    comment
-->
}}}

  * *FTL_ALT* (FreeMarker Alternative Syntax comments)

{{{
[#ftl ...]
[#--
    comment
--]
}}}

  * *SHARPSTAR_STYLE* (Velocity templates comments)

{{{
#*
    comment
*##
}}}

  * *SEMICOLON_STYLE* (Assembler like comments)

{{{
;
; comment
;
}}}

  * *BRACESSTAR_STYLE* (Delphi like comments)

{{{
{*
 * comment
 *}
}}}

  * *APOSTROPHE_STYLE* (VisualBasic like comments)

{{{
'
' comment
'
}}}

  * *EXCLAMATION_STYLE* (Fortran like comments)

{{{
!
! comment
!
}}}

  * *SLASHSTAR_STYLE* (JavaScript like comments)

{{{
/*
 * comment
 */
}}}

  * *DYNASCRIPT3_STYLE* (Coldfusion like comments)

{{{
<!---
    comment
--->
}}}

  * *PERCENT3_STYLE* (Erlang like comments)

{{{
%%%
%%% comment
%%%
}}}

  * *EXCLAMATION3_STYLE* (Lisp like comments)

{{{
!!!
!!! comment
!!!
}}}

  * *LUA* (Lua like comments)

{{{
--[[
comment
]]
}}}

  * *ASP* (Asp like comments)

{{{
<%
' comment
%>
}}}

  * *PHP* (PHP comments)

{{{
/*
 * comment
 */
}}}
(inserted after the <?php> tag)




  * *DOUBLESLASH_STYLE* (often used comments style)

{{{
//
// comment
//
}}}

= Supported file extensions =

From the different supported comment types, we can add any mapping we want. By default, the plugin includes the mappings below between the extension of the file and the comment type above:

|| *File extension* || *Comment type* ||
|| java || JAVADOC_STYLE||
|| groovy || JAVADOC_STYLE||
|| js || SLASHSTAR_STYLE||
|| css || JAVADOC_STYLE||
|| cs || JAVADOC_STYLE||
|| as || JAVADOC_STYLE||
|| aj || JAVADOC_STYLE||
|| c || JAVADOC_STYLE||
|| h || JAVADOC_STYLE||
|| cpp || JAVADOC_STYLE||
|| pom || XML_STYLE||
|| xml || XML_STYLE||
|| xhtml || XML_STYLE||
|| mxml || XML_STYLE||
|| dtd || XML_STYLE||
|| xsd || XML_STYLE||
|| jspx || XML_STYLE||
|| fml || XML_STYLE||
|| xsl || XML_STYLE||
|| html || XML_STYLE||
|| htm || XML_STYLE||
|| apt || DOUBLETILDE_STYLE||
|| properties || SCRIPT_STYLE||
|| sh || SCRIPT_STYLE||
|| py || SCRIPT_STYLE||
|| rb || SCRIPT_STYLE||
|| pl || SCRIPT_STYLE||
|| pm || SCRIPT_STYLE||
|| txt || TEXT||
|| bat || BATCH||
|| cmd || BATCH||
|| sql || DOUBLEDASHES_STYLE||
|| adb || DOUBLEDASHES_STYLE||
|| ads || DOUBLEDASHES_STYLE||
|| e || DOUBLEDASHES_STYLE||
|| jsp || DYNASCRIPT_STYLE||
|| asp || ASP||
|| php || PHP||
|| vm || SHARPSTAR_STYLE||
|| ftl || FTL||
|| asm || SEMICOLON_STYLE||
|| cfc || DYNASCRIPT3_STYLE||
|| cfm || DYNASCRIPT3_STYLE||
|| pas || BRACESSTAR_STYLE||
|| erl || PERCENT3_STYLE||
|| hrl || PERCENT3_STYLE||
|| f || EXCLAMATION_STYLE||
|| el || EXCLAMATION3_STYLE||
|| lua || LUA||
|| bas || APOSTROPHE_STYLE||
|| kml || XML_STYLE||
|| gsp || XML_STYLE||
|| haml || HAML_STYLE||
|| scaml || HAML_STYLE||
|| tld || XML_STYLE||


*The plugin enables you to add any other mapping you want.* I.e., if you are developing a Tapestry web application, you may need to add license header in .jwc files and .application files. since these files are xml files, you only need to add in your project pom the following mapping for the maven-license-plugin:

{{{
<mapping>
    <jwc>XML_STYLE</jwc>
    <application>XML_STYLE</application>
</mapping>
}}}



==============================================================


#summary Give an example of an additional header definition for .Net regions.

= Introduction =

This page will show you how you can define extended header definitions to feet your needs. The next example will define headers in a _region_ area allowed in C# source files:


{{{
<?xml version="1.0" encoding="ISO-8859-1"?>
<additionalHeaders>
    <csregion_style>
        <firstLine>#region LicenseEOL/**</firstLine>
        <beforeEachLine> * </beforeEachLine>
        <endLine> */EOL#endregion</endLine>
        <firstLineDetectionPattern>#region.*^EOL/\*\*.*$</firstLineDetectionPattern>
        <lastLineDetectionPattern>\*/EOL#endregion"</lastLineDetectionPattern>
        <allowBlankLines>true</allowBlankLines>
        <isMultiline>true</isMultiline>
    </csregion_style>
</additionalHeaders>
}}}

 * The _EOL_ string will be replaced with the proper end of line depending the file format your are processing.
 * We also have defined the _skipLine_ attribute to skip the region tags (which starts with a '#')
 * _allowBlankLines_ allows you to define if this header style supports blank lines in it or not. In example, in XML headers, you could have blank lines after the <!-- and before --> because XML delimiters delimit a multiline block. When you work with script style comments like in Ruby, Porperties files, the # character delimit a comment for only one line. So when you create the header, for it to be uniform, you place # on each line. So allowBlankLines will be false.
 * _isMultiline_ specifies if your header has tokens to delimit a multiline comment of if the tokens are a one-line comment. I.E.: XML style comments are multiline whereas script style comment where each line starts with # are not multiline

You now have to add this new header definition file to the plugin configuration. It is done as the following in your pom:

{{{
<headerDefinitions>
   <headerDefinition>yourdefinition.xml</headerDefinition>
</headerDefinitions>
}}}


You now have to add the new mapping for `*.cs` files to use this new header definition. It is done as the following in your pom:

{{{
<mapping>
    <cs>CSREGION_STYLE</cs>
</mapping>
}}}

If you need more information on the pom configuration, just read the [http://code.google.com/p/maven-license-plugin/w/edit/Configuration Configuration page].

And it should generate headers like:
{{{
#region License
/**
 * Copyright (C) 2008 http://code.google.com/p/maven-license-plugin/
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
}}}



=============================================



#summary How to use maven-license-plugin
<wiki:toc max_depth="2" /> 
= maven-license-plugin configuration reference =

See [Configuration the maven-license-plugin configuration reference wiki page] to have for information about all the possible configuration options of this plugin !

= STEP 1: Set Maven 2 repository =

*maven-license-plugin is available in Maven Central Repo at http://repo1.maven.org/maven2/com/mycila/maven-license-plugin/ *

*to get the really latest releases and snapshots*

Since it can take some weeks before releases are uploaded in the central repo by the maven team, i provide a maven repository that you can use to get the plugin.

Add in your POM or settings.xml file the plugin repository mc-repo here. See explanations at http://code.google.com/p/mc-repo/wiki/HowToUse.

You should only need to add this:

{{{
<pluginRepository>
    <id>mc-release</id>
    <name>Local Maven repository of releases</name>
    <url>http://mc-repo.googlecode.com/svn/maven2/releases</url>
    <snapshots>
        <enabled>false</enabled>
    </snapshots>
    <releases>
        <enabled>true</enabled>
    </releases>
</pluginRepository>
}}}

and for snapshots

{{{
<pluginRepository>
    <id>mc-release</id>
    <name>Local Maven repository of releases</name>
    <url>http://mc-repo.googlecode.com/svn/maven2/snapshots</url>
    <snapshots>
        <enabled>true</enabled>
    </snapshots>
    <releases>
        <enabled>false</enabled>
    </releases>
</pluginRepository>
}}}

= STEP 2: Create a header file =

The plugin needs that you put a file on your project that contain the header, license or whatever text you want to see on the top of your source files. In the examples below, the file *header.txt* has the following content:

{{{
Copyright (C) ${year} ${user.name} <${email}>

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
}}}

You can also provide for the header any valid URL or a resource from the classpath

== Be careful about line end ! ==

On Windows, lines end with CRLF (\r\n) whereas on Unix they end with LF (\n). If you apply a header file saved in Windows format to your files developed on Unix, you will end up with files having line ends like in Unix, except for the header part: you will have \r\n. The side effect is that in some Unix editors, you could see ^M characters at line ends for the header part.

The solution is very simple: you just have to convert your header with dos2unix. You can also convert all your project files like this:

 `for i in ```find src -type f | grep -v .svn | grep -v java```; do dos2unix $i; done`

 - Thanks to [http://code.google.com/p/maven-license-plugin/issues/detail?id=26 Erik Drolshammer's report]

= STEP 3: Check for missing headers (goal: check) =

To launch a check, simply add the plugin to your POM and issue:

*`mvn license:check -Dyear=2008 -Demail=myemail@company.com`*

{{{
<build>
    <plugins>
        <plugin>
            <groupId>com.mycila.maven-license-plugin</groupId>
            <artifactId>maven-license-plugin</artifactId>
            <configuration>
                <header>src/etc/header.txt</header>
            </configuration>
        </plugin>
    </plugins>
</build>
}}}

You can also automatically bind the check to the verify phase if you want a build to fail when missing headers are found. You just have to include the following declaration to check for missing headers, and then issue:

*`mvn verify -Dyear=2008 -Demail=myemail@company.com`*

{{{
<build>
    <plugins>
        <plugin>
            <groupId>com.mycila.maven-license-plugin</groupId>
            <artifactId>maven-license-plugin</artifactId>
            <configuration>
                <header>src/etc/header.txt</header>
            </configuration>
            <executions>
                <execution>
                    <goals>
                        <goal>check</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
}}}

If you want to bind to another phase, i.e. the test phase, you just have to declare the phase in the execution tag like this:

{{{
<build>
    <plugins>
        <plugin>
            <groupId>com.mycila.maven-license-plugin</groupId>
            <artifactId>maven-license-plugin</artifactId>
            <configuration>
                <header>src/etc/header.txt</header>
            </configuration>
            <executions>
                <execution>
                    <phase>test</phase>
                    <goals>
                        <goal>check</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
}}}

With this, you will then check the headers after the tests by doing *`mvn test  -Dyear=2008 -Demail=myemail@company.com`*

*Note:* The properties in the header and in the command line are optional. If you prefer, you can set directly the values in the header and not use properties. Also, you would prefer configuring the plugin for your needs to set properties in the maven POM. See [Configuration the maven-license-plugin configuration guide] to see all the possibilities of this plugin.

= STEP 4: Add missing headers (goal: format) =

This goal enables you to add or update existing license headers to put the one in the header file you have specified.

To launch it: *`mvn license:format -Dyear=2008 -Demail=myemail@company.com`* (having the configuration below)

{{{
<build>
    <plugins>
        <plugin>
            <groupId>com.mycila.maven-license-plugin</groupId>
            <artifactId>maven-license-plugin</artifactId>
            <configuration>
                <header>src/etc/header.txt</header>
            </configuration>
        </plugin>
    </plugins>
</build>
}}}

