# TMC Auth Example Using Maven

## Overview

This example shows you how to:

 1. Get a REST Authentication token for Production
 2. Get a REST Authentication token when you want to tell the tokenSupplier what environment to connect to
 3. Create an authenticated gRPC channel that can be used when creating a client stub

### Prerequisites

In order to begin integrating with the TMC, we require the following:

- At least Java 8 installed
- Access to the TMC Platform (Your Client Id and Client Secret that have been provided to you)

## Step 1: Configure the example application

The following properties should be added to your environment or this example's [application.yml](src/main/resources/application.yml) file. Notice that the `application.yml` file references two environment variables which you should set in your environment in order to help protect sensitive credentials.

|Property|Environment Variable|Description|Required/Optional|
|------|------|------|-----------------------|
|tmc.auth.clientId|TMC_CLIENT_ID|This is your TMC Client identifier.|Required|
|tmc.auth.clientSecret|TMC_CLIENT_SECRET|This is your TMC Client secret.|Required|

## Step 2: Run the example application

From the project directory, run the following:

*Linux/Mac*: `./mvnw -s ./settings.xml spring-boot:run` or `./run.sh`

*Windows*: `mvn -s .\settings.xml spring-boot:run` or `run.bat`

## More information on the Example Application

### How the Example Authenticates
See [MavenAuthExample.java](src/main/java/com/autonomic/tmc/example/auth/MavenAuthExample.java) class for an example of using the `tmc-auth` SDK to retrieve an access token to the TMC. This class also provides an example of creating an authenticated gRPC channel.

### Maven Setup

To access Autonomic's open source dependencies, you can add a Maven url to the repositories section of your `settings.xml` file. See the [settings.xml](settings.xml) file for an example.

To use this settings file, it needs to either be placed in your `~/.m2` directory on Mac or `%userprofile%\.m2` on Windows. The settings file can also be invoked directly with the `-s` command line option (eg, `./mvnw -s these-settings.xml clean verify`).

### Including the tmc-auth Maven Dependency

Add the `tmc-auth` client library to the `<dependencies>` section of your pom.xml. See the [pom.xml](pom.xml) file for an example.

### Optional: Configuration for Another Environment

By default, this example assumes you want to connect to Autonomic's production environment.  However, Autonomic may have provided you access to another environment for testing and validation. To test tmc-auth in another environment, you should set the following value:

Property|Description|Required/Optional|Default Value|
|------|------|-----------------------|------|
|tmc.auth.tokenUrl|The authentication url.|Optional|https://accounts.autonomic.ai/auth/realms/iam/protocol/openid-connect/token|

### Optional: Configuration for a Secure gRPC Channel

This example exposes the `tmc.some-service.serviceUrl` property for testing and establishing a secure gRPC channel. This is just an example value and you should configure it for the Autonomic gRPC service you wish to interact with.

Property|Description|Required/Optional|Default Value|
|------|------|-----------------------|------|
|tmc.some-service.serviceUrl|The url of the service you wish to connect to securely.|Optional| Dependent on service.

### Compiling the Code

A Maven wrapper is included in this project and references a project specific settings.xml. See [Maven Setup](#maven-setup) for an example of the `settings.xml` file.

By using the maven wrapper, you will be able to build the project without having to install Maven locally.

Linux/Mac: `./mvnw -s ./settings.xml clean install`

Windows: `mvnw -s .\settings.xml clean install`

## Helpful Information

The `tmc-auth` SDK used in this example application provides a mechanism that automatically will refresh your access token for you. This feature provides a buffer of roughly 10 seconds in order to compensate for network latency.

## Support

If you require support with this SDK, please reach out to your Customer Representative or send an email to support@autonomic.ai
