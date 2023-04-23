license-maven-plugin-fs
========================

It is quite common that legal departments require updating of copyright years in header sections whenever the given source file changes. `license-maven-plugin-fs` aims at making it easier not only to enforce this requirement but also to fix found inconsistencies.

What is license-maven-plugin-fs?
---------------------------------

* `license-maven-plugin-fs` can optionally be used with `license-maven-plugin` to bring in properties (esp. the year of the last change) from the filesystem.
* The properties added by `license-maven-plugin-fs` can be used in file header templates used by `license-maven-plugin`

Which properties is license-maven-plugin-fs adding?
----------------------------------------------------

* `license.fs.copyrightLastYear` - the year of the last change of the present file, seen as last modification time in the filesystem.
* `license.fs.copyrightYears` - the combination of `project.inceptionYear` and `license.fs.copyrightLastYear` delimited by a dash (`-`), or just `project.inceptionYear` if `project.inceptionYear` is equal to `license.fs.copyrightLastYear`

How to use license-maven-plugin-fs
-----------------------------------

Just add `license-maven-plugin-fs` as a dependency to `license-maven-plugin` in your pom.xml file:

``` xml
...
<properties>
  <!-- This quite certainly out of date at the time when you are reading this -->
  <license-maven-plugin.version>4.2.rc3</license-maven-plugin.version>
</properties>
...
<build>
  </plugins>
     ...

    <plugin>
      <groupId>com.mycila</groupId>
      <artifactId>license-maven-plugin</artifactId>
      <version>${license-maven-plugin.version}</version>
      <configuration>
        <header>my-header-folder/my-header-template.txt</header>
       ...
      </configuration>
      <dependencies>
        <dependency>
          <groupId>com.mycila</groupId>
          <artifactId>license-maven-plugin-fs</artifactId>
          <!-- make sure you use the same version as license-maven-plugin -->
          <version>${license-maven-plugin.version}</version>
        </dependency>
      </dependencies>
      <executions>
        ...
      </executions>
    </plugin>
  </plugins>
</build>
```

With this configuration in place, you can use `license.fs.copyrightLastYear` and `license.fs.copyrightYears` in your header template. E.g. the beginning of the file `my-header-folder/my-header-template.txt` from the above example might look like this:

``` bash
Copyright ${license.fs.copyrightYears} My Company, Inc.
...
```

For combination a project with `inceptionYear` 1999 and file with last change in 2006, this example template would be resolved by `license-maven-plugin` to something like this:

```
Copyright 1999-2006 My Company, Inc.
...
```
