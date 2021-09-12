# Dependency Enforcement

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
