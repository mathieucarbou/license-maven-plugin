# license-maven-plugin-svn

It is quite common that legal departments require updating of copyright years in header sections whenever the given source file changes. `license-maven-plugin-svn` aims at making it easier not only to enforce this requirement but also to fix found inconsistencies for projects hosted on Subversion
SCM.

## What is license-maven-plugin-svn?

- `license-maven-plugin-svn` is an extension plugin for `license-maven-plugin` to bring additional information extracted from Subversion metadata of your project files.
- The properties added by `license-maven-plugin-svn` can then be used in file header templates used by `license-maven-plugin`

## Which properties is license-maven-plugin-svn adding?

- `license.svn.lastchange.year`: the year of the last change (year of last commit) on 4 digits
- `license.svn.lastchange.timestamp`: the last change timestamp (timestamp of last commit) using [SimpleDateFormat](https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html) `yyyyMMdd-HH:mm:ss`
- `license.svn.lastchange.date`: the last change date (date of last commit) using [SimpleDateFormat](https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html) `yyyyMMdd`
- `license.svn.lastchange.revision`: the SVN revision number of last change
- `license.svn.years.range`: the combination of `project.inceptionYear` and `license.svn.lastchange.year` delimited by a dash (-), or just `license.svn.lastchange.year` if `project.inceptionYear` is eqal to `license.svn.lastchange.year` or if `project.inceptionYear` isn't defined

## How to use license-maven-plugin-git

Just add `license-maven-plugin-svn` as a dependency to `license-maven-plugin` in your pom.xml file:

``` xml
...
<properties>
  <license-maven-plugin.version>X.Y.Z</license-maven-plugin.version>
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
          <artifactId>license-maven-plugin-svn</artifactId>
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

With this configuration, the plugin will be able to query svn metadata, described by the above properties, for each of the files it will handle.

For example if the beginning of the file my-header-folder/my-header-template.txt from the above example looks like this:

```
Copyright ${license.svn.years.range} My Company, Inc.
...
```

Within a porject with inceptionYear 2010 and a file with last change in 2013, this example template would be resolved by license-maven-plugin to something like this:

```
Copyright 2010-2013 My Company, Inc.
...
```

## SVN authentication

Your Subversion repository is probably a secured one.
`license-maven-plugin-svn` supports authentication based on user/password and relies on standard maven `<server>` entries in the maven `settings` plus a dedicated configuration in the `license-maven-plugin`.

Thus having in your settings

``` xml
<servers>
    ...
    <server>
        <id>mySVN-ID</id>
        <user>bob</user>
        <password>SuP3Rs3cuR3d</password>
    </server>
    ...
</servers>
```

You need to add in the configuration of `license-maven-plugin` a sub node called `license.svn.serverId` to the `properties` one that defines the server ID to use to authenticate for subversion calls.

``` xml
...
<properties>
  <license-maven-plugin.version>X.Y.Z</license-maven-plugin.version>
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
        <properties>
            <license.svn.serverId>mySVN-ID</license.svn.serverId>
        </properties>
       ...
      </configuration>
      <dependencies>
        <dependency>
          <groupId>com.mycila</groupId>
          <artifactId>license-maven-plugin-svn</artifactId>
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




