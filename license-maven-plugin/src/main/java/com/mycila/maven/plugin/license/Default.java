/*
 * Copyright (C) 2008-2023 Mycila (mathieu.carbou@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mycila.maven.plugin.license;

public final class Default {

  private Default() {
    // Prevent Instantiation
  }

  public static final String[] INCLUDE = new String[]{"**"};

  public static final String[] EXCLUDES = {
      // Miscellaneous typical temporary files
      "**/*~",
      "**/#*#",
      "**/.#*",
      "**/%*%",
      "**/._*",
      "**/.repository/**",
      "**/*.lck",

      // Checkstyle
      "**/*.checkstyle",

      // PMD
      "**/*.pmd",
      "**/*.pmdruleset.xml",

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

      // Bazaar
      "**/.bzr",
      "**/.bzr/**",

      // SurroundSCM
      "**/.MySCMServerInfo",

      // Mac
      "**/.DS_Store",

      // Serena Dimensions Version 10
      "**/.metadata",
      "**/.metadata/**",

      // Mercurial
      "**/.hg",
      "**/.hg/**",
      "**/.hgignore",

      // git
      "**/.git",
      "**/.git/**",
      "**/.gitattributes",
      "**/.gitignore",
      "**/.gitkeep",
      "**/.gitmodules",

      // GitHub
      "**/.github",
      "**/.github/**",

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
      "**/dependency-reduced-pom.xml",
      "**/release-pom.xml",
      "**/pom.xml.releaseBackup",
      "**/pom.xml.versionsBackup",

      // Node
      "**/node/**",
      "**/node_modules/**",

      // code coverage tools
      "**/cobertura.ser",
      "**/.clover/**",
      "**/jacoco.exec",

      // eclipse project files
      "**/.classpath",
      "**/.project",
      "**/.settings/**",

      // IDEA projet files
      "**/*.iml",
      "**/*.ipr",
      "**/*.iws",
      "**/.idea/**",

      // Netbeans
      "**/nb-configuration.xml",

      // Hibernate Validator Annotation Processor
      "**/.factorypath",

      // descriptors
      "**/MANIFEST.MF",

      // License files
      "**/LICENSE",
      "**/LICENSE_HEADER",

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
      "**/*.dll",
      "**/*.so",

      // checksum files
      "**/*.md5",
      "**/*.sha1",
      "**/*.sha256",
      "**/*.sha512",

      // Security files
      "**/*.asc",
      "**/*.cer",
      "**/*.der",
      "**/*.jks",
      "**/*.keytab",
      "**/*.lic",
      "**/*.p12",
      "**/*.pub",

      // binary files - archives
      "**/*.ear",
      "**/*.jar",
      "**/*.war",
      "**/*.zip",
      "**/*.rar",
      "**/*.tar",
      "**/*.tar.gz",
      "**/*.tar.bz2",
      "**/*.tar.bz3",
      "**/*.tar.xz",
      "**/*.gz",
      "**/*.7z",

      // ServiceLoader files
      "**/META-INF/services/**",

      // Markdown files
      "**/*.md",

      // Office documents
      "**/*.xls",
      "**/*.doc",
      "**/*.odt",
      "**/*.ods",
      "**/*.pdf",

      // Travis
      "**/.travis.yml",

      // AppVeyor
      "**/.appveyor.yml",
      "**/appveyor.yml",

      // CircleCI
      "**/.circleci",
      "**/.circleci/**",

      // SourceHut
      "**/.build.yml",

      // Maven 3.3+ configs
      "**/jvm.config",
      "**/maven.config",

      // Wrappers
      "**/gradlew",
      "**/gradlew.bat",
      "**/gradle-wrapper.properties",
      "**/mvnw",
      "**/mvnw.cmd",
      "**/maven-wrapper.properties",
      "**/MavenWrapperDownloader.java",

      // flash
      "**/*.swf",

      // json files
      "**/*.json",

      // fonts
      "**/*.svg",
      "**/*.eot",
      "**/*.otf",
      "**/*.ttf",
      "**/*.woff",
      "**/*.woff2",

      // logs
      "**/*.log",

      // office documents
      "**/*.xlsx",
      "**/*.docx",
      "**/*.ppt",
      "**/*.pptx",

      // String Template
      "**/*.st",
      "**/*.stg",

      // Explicit Folder to Entirely Ignore
      "**/unlicensed/**",

      // EditorConfig
      "**/.editorconfig"
  };

}
