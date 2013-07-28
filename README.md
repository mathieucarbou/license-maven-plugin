**Table of Contents**

- [Mycila JMX](#mycila-jmx)
	- [Maven Repository](#maven-repository)
	- [Documentation](#documentation)
		- [1. Annotations](#1-annotations)
		- [2. Dynamic naming](#2-dynamic-naming)
		- [3. How to use](#3-how-to-use)
		- [4. JmxMetadataAssembler](#4-jmxmetadataassembler)
		- [5. Mycile Guice Integration](#5-mycile-guice-integration)
		- [6. Tools](#6-tools)

# Mycila JMX #

This small project enables to export your classes easily through JMX.

__Issues:__ https://github.com/mycila/jmx/issues

<img width="100px" src="http://www.sonatype.com/system/images/W1siZiIsIjIwMTMvMDQvMTIvMTEvNDAvMzcvMTgzL05leHVzX0ZlYXR1cmVfTWF0cml4X29zZ2lfbG9nby5wbmciXV0/Nexus-Feature-Matrix-osgi-logo.png" title="OSGI Compliant"></img>
[![Build Status](https://travis-ci.org/mycila/jmx.png?branch=master)](https://travis-ci.org/mycila/jmx)
[![Bitdeli Badge](https://d2weczhvl823v0.cloudfront.net/mycila/jmx/trend.png)](https://bitdeli.com/free "Bitdeli Badge")
[![githalytics.com alpha](https://cruel-carlota.pagodabox.com/85294c815bb3ce46bd79f4cf8d9bb341 "githalytics.com")](http://githalytics.com/mycila/jmx)

## Maven Repository ##

 - __Releases__ 

Available in Maven Central Repository: http://repo1.maven.org/maven2/com/mycila/jmx/

 - __Snapshots__
 
Available in OSS Repository:  https://oss.sonatype.org/content/repositories/snapshots/com/mycila/jmx/

__Maven dependency__

    <dependency>
        <groupId>com.mycila</groupId>
        <artifactId>mycila-jmx</artifactId>
        <version>X.Y.ga</version>
    </dependency>

## Documentation ##

### 1. Annotations ###

 - `@JmxBean`: mark a class to be exported to JMX and set its name 
 - `@JmxMethod`: export a method
 - `@JmxProperty`: export a bean property (getter/setter) and specify its access writes 
 - `@JmxField`: export a field to JMX and specify its access writes
 - `@JmxMetric`: export a JMX metric, its name and unit (counter or gauge)
 
### 2. Dynamic naming ###

A bean can implement the interface `JmxSelfNaming` to return the its `ObjectName`

### 3. How to use ###

Supposing you have annotated your class like this

    @JmxBean("com.company:type=MyService,name=main")
    public final class MyService {
    
        private String name;
    
        @JmxField
        private int internalField = 10;
    
        @JmxProperty
        public String getName() { return name; }
    
        public void setName(String name) { this.name = name; }
    
        @JmxMethod(parameters = {@JmxParam(value = "number", description = "put a big number please !")})
        void increment(int n) {
            internalField += n;
        }
    }

Then create an exporter which will be able to export your beans:

    MycilaJmxExporter exporter = new MycilaJmxExporter();
    exporter.setMetadataAssembler(getMetadataAssembler());
    exporter.setEnsureUnique(true);

And export your service:

    exporter.register(myServiceInstance)


### 4. JmxMetadataAssembler ###

`JmxMetadataAssembler` are implementations which describes the exportable members of a class and how to export them. Several implementation exist:

 - `PublicMetadataAssembler`: when beans are registered, only public methods, properties and fields are exported
 - `AnnotationMetadataAssembler`: when beans are registered, only annotated methods, properties and fields are exported
- `CustomMetadataAssembler`: you can specify which members are exported. This class can be useful if you only need to export just some members and if you do not have access to the source code of the class to export

__Example__

    CustomMetadataAssembler assembler = new CustomMetadataAssembler()
        .addAttribute(MyClass.class, "myRWField")
        .addProperty(MyClass.class, "myProperty")
        .addOperation(MyClass.class, "goMethod");

You will be able to find other examples in the Unit Tests 

### 5. Mycile Guice Integration ###

When using the service discovery feature of [Mycile Guice] (http://mycila.github.io/guice/) (Google Guice extensions) JMX annoteated beans are automatically discovered and exported


### 6. Tools ###

 - [VisualVM] (http://visualvm.java.net/): Connects to a Java process and manage exported JMX classes
 - [MX4J] (http://mx4j.sourceforge.net/): Web interface yo manage JMX beans
