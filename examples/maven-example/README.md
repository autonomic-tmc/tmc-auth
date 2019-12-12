# Maven Auth Example

This example shows you how to:
 1. Get a REST Authentication token for Production 
 2. Get a REST Authentication token when you want to tell the tokenSupplier what environment to connect to 
 3. Create an authenticated gRPC channel that can be used when creating a client stub 

## Steps to get started

### Prerequisites


In order to begin integrating with the TMC, we require the following:

- At least Java 8 installed
- Access to Autonomic's Bintray instance
- Access to the TMC Platform (Your Client Id and Client Secret that have been provided to you)

### Maven Setup

To access Autonomic's open source dependencies, you can add a Maven url to the repositories section of your `settings.xml` file, for example:

```xml
<settings xmlns='http://maven.apache.org/SETTINGS/1.0.0'
  xsi:schemaLocation='http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd'
  xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'>
  <profiles>
    <profile>
      <repositories>
        <repository>
          <id>tmc</id>
          <name>bintray</name>
          <url>https://autonomic.bintray.com/au-tmc-oss</url>
        </repository>
      </repositories>
      <pluginRepositories>
        <pluginRepository>
          <id>tmc</id>
          <name>bintray-plugins</name>
          <url>https://autonomic.bintray.com/au-tmc-oss</url>
        </pluginRepository>
      </pluginRepositories>
      <id>tmc</id>
    </profile>
  </profiles>
  <activeProfiles>
    <activeProfile>tmc</activeProfile>
  </activeProfiles>
</settings>
```

To use this settings file, it needs to either be placed in your `~/.m2` directory on Mac or `%userprofile%\.m2` on Windows. The settings file can also be invoked directly with the `-s` command line option (eg, `./mvnw -s these-settings.xml clean verify`).


## Configuration

The following properties should be added to your environment or this example's `application.resources` file.

|Property|Description|Required/Optional|Default Value|
|------|------|-----------------------|-----------|
|TMC_CLIENT_ID|This is your TMC Client identifier.|Required| |
|TMC_CLIENT_SECRET|This is your TMC Client secret.|Required| |
|TMC_TOKEN_URL|This is the URL for authenticating against the TMC. Use the default value unless Autonomic has provided you a different value.|Required|<https://accounts.autonomic.ai/auth/realms/iam/protocol/openid-connect/token>|
|TMC_BASE_URL|This is the URL for accessing the TMC. Use the default value unless Autonomic has provided you a different value.|Required|<https://api.autonomic.ai/>|

## Running the Example

Use the command below:

*Linux/Mac:*
```bash
./mvnw clean verify spring-boot:run 
```

*Windows:*
```cmd
mvnw clean verify spring-boot:run
```
## Support

If you require support with this SDK, please reach out to your Customer Representative or send an email to support@autonomic.ai
