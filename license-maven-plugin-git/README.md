license-maven-plugin-git
========================

It is quite common that legal departments require updating of copyright years in header sections whenever the given source file changes. `license-maven-plugin-git` aims at making it easier not only to enforce this requirement but also to fix found inconsistencies.

What is license-maven-plugin-git?
---------------------------------

* `license-maven-plugin-git` can optionaly be used with `license-maven-plugin` to bring in properties (esp. the year of the last change) from git.
* The properties added by `license-maven-plugin-git` can be used in file header templates used by `license-maven-plugin`

Which properties is license-maven-plugin-git adding?
----------------------------------------------------

* `license.git.copyrightLastYear` - the year of the last change of the present file as seen in git history
* `license.git.copyrightYears` - the combination of `project.inceptionYear` and `license.git.copyrightLastYear` delimited by a dash (`-`), or just `project.inceptionYear` if `project.inceptionYear` is eqal to `license.git.copyrightLastYear`


How to use license-maven-plugin-git
-----------------------------------

Just add `license-maven-plugin-git` as a dependency to `license-maven-plugin` in your pom.xml file:
``` xml
...
<properties>
  <!-- This quite certainly out of date at the time when you are reading this -->
  <license-maven-plugin.version>2.9</license-maven-plugin.version>
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
          <artifactId>license-maven-plugin-git</artifactId>
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

With this configuration in place, you can use `license.git.copyrightLastYear` and `license.git.copyrightYears` in your header template. E.g. the beginning of the file `my-header-folder/my-header-template.txt` from the above example might look like this:
``` bash
Copyright ${license.git.copyrightYears} My Company, Inc.
...
```

For combination a project with `inceptionYear` 1999 and file with last change in 2006, this example template would be resolved by `license-maven-plugin` to something like this:
```
Copyright 1999-2006 My Company, Inc.
...
```


Noteworthy
----------

There are situations when `license-maven-plugin-git` produces results you might find counter-intuitive:

* If you have new files or uncommitted changes in your project, `license-maven-plugin-git` assumes that you will commit all available changes and new files in the current year.
  * This condition is not fulfilled if you commit in such a way that the author date in the commit differs from the date of the last run of `mvn license:format`, e.g.
    * comitting with `--amend`
    * or indeed committing in another year
  * In such situations, `mvn license:check` may fail or `mvn license:format` may introduce new changes.
  * Generally, if you feel that `license:check` and/or `license:format` are doing something inconsistent, commit all your changes, run `mvn license:format` and compare the work tree with the HEAD.
* Further note that committing the result of `mvn license:format` may sometimes introduce the first change in the given year, which again may make the subsequent `mvn license:check` fail. In such cases, just run the combination of `mvn license:format` and `git commit` twice.

You may want to consult `license-maven-plugin-git` sources (esp. `com.mycila.maven.plugin.license.git.CopyrightRangeProvider`) to learn about its configuration parameters that are not documented here.
