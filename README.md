# LIcense Maven Plugin

- __Build Status:__ [![Java CI](https://github.com/mathieucarbou/license-maven-plugin/actions/workflows/ci.yaml/badge.svg)](https://github.com/mathieucarbou/license-maven-plugin/actions/workflows/ci.yaml)
- __Issues:__ https://github.com/mathieucarbou/license-maven-plugin/issues
- __Documentation:__ https://mycila.carbou.me/license-maven-plugin/
- __License:__ [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0)

## Quick Links ##

- [Default Excludes](https://github.com/mathieucarbou/license-maven-plugin/blob/master/license-maven-plugin/src/main/java/com/mycila/maven/plugin/license/Default.java)
- [Header Types](https://github.com/mathieucarbou/license-maven-plugin/blob/master/license-maven-plugin/src/main/java/com/mycila/maven/plugin/license/header/HeaderType.java)

## Quick Tip ##

Often necessary to exclude a number of files that would otherwise grow the exclusion list on setup and do not fit with the default exclusions provided by this plugin.  To exclude complete sections, please into a special folder to do exactly that.

```**/unlicensed/**```
