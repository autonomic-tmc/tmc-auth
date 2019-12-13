# Gradle Auth Example

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

### Gradle Setup

To access Autonomic's open source dependencies, you can add a maven url to the repositories section of your `build.gradle` file, for example:

```groovy
repositories {
    // other repositories here...
    maven {
        url "https://autonomic.bintray.com/au-tmc-oss"
    }
}
```

## Including the tmc-auth Gradle Dependency

Add the `tmc-auth` client library to the `dependencies` section of your build.gradle. **Note:** Verify the version you wish to include. This dependency is already included in this example application.

```groovy
dependencies {
    compile 'com.autonomic.tmc:tmc-auth:2.0.0-alpha'
}
```

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
./gradlew clean build run
```

*Windows:*
```cmd
gradlew clean build run
```
## Support

If you require support with this SDK, please reach out to your Customer Representative or send an email to support@autonomic.ai
