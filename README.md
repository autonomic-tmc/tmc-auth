# Authenticating with the TMC

## tmc-auth SDK

Using the tmc-auth SDK with credentials provided by Autonomic, you can obtain a time-limited access token to be used with other services available on the platform.

With this library, you don't need to worry about expiring tokens. The token you `get()` is always valid.  If a token expires, this library will automatically get a new one.

## Let's get authenticated with an example application

We have built a runnable example application that demonstrates how to authenticate on the TMC using the `tmc-auth` SDK. This example application will login and retrieve an access token which is used when accessing TMC services.

- [Getting authenticated using the Maven Example](./examples/maven-example)
- [Getting authenticated using the Gradle Example](./examples/gradle-example)

## Adding tmc-auth as a dependency

See our [examples](examples) for more information on including this library in your Maven or Gradle project. It is distributed as a JAR file for easy consumption.

- [Maven - pom.xml](./examples/maven-example/pom.xml)
- [Gradle - build.gradle](./examples/gradle-example/build.gradle)

> Are you using a different build tool other than Maven or Gradle? Are you interested in just browsing what is available in our repository? Do you want to download the jar files required for `tmc-auth` directly? You can find everything you need in our [Cloudsmith](https://cloudsmith.io/~autonomic/repos/au-tmc-oss/groups/) repository.

## Usage

### Create a TokenSupplier
```java
TokenSupplier tokenSupplier = null;
try {
  tokenSupplier = ClientCredentialsTokenSupplier.builder()
    .clientId("<your-client-id>")
    .clientSecret("<your-client-secret>")
    .tokenUrl("<your-token-url-if-not-using-default>")
    .build();
} catch (BaseSdkException e) {
  //Decide what action you would like to take.  For example, you might want to retry creating a token.
}
```
### Get a Token
```Java
try {
  String bearerToken = tokenSupplier.get();
} catch (BaseSdkException e) {
  //Decide what action you would like to take.  For example, you might want to retry getting a token.
}
```

## Configuration Options

| Builder Param | Description | Required/Optional | Default Value|
|---------------|-------------------------------------------------|-----------|-----------------------|
| clientId | This is your TMC Client identifier provided to you by Au. | Required | none |
| clientSecret | This is your TMC Client secret provided to you by Au. | Required | none |
| tokenUrl | The authentication url | Optional | https://accounts.autonomic.ai/v1/auth/oidc/token|

## Troubleshooting

The [ClientCredentialsTokenSupplier.get()](src/main/java/com/autonomic/tmc/auth/ClientCredentialsTokenSupplier.java) primarily throws two exceptions:

[SdkClientException](src/main/java/com/autonomic/tmc/auth/exception/SdkClientException.java) - An issue faced while parsing the tokenUrl or unable to parse the service response.

[SdkServiceException](src/main/java/com/autonomic/tmc/auth/exception/SdkServiceException.java) - Your credentials are incorrect and should be corrected before calling again. Or, there is another issue while communicating with the service.

## Building

```shell
mvn clean install
```

## Running the Integration Tests

*NOTE:* You will need to run the `integration-tests` profile with the following environment variables to run integration tests.

- TMC_CLIENT_ID
- TMC_CLIENT_SECRET

For example:

```shell
mvn -Pintegration-tests clean verify
```

You can pass them as arguments to `mvn` if you do not have them set on your path.

For example:

```shell
(export TMC_CLIENT_ID=your-client-id && export TMC_CLIENT_SECRET=your-client-secret && mvn -Pintegration-tests clean verify)
```

## 3rd Party Components

This project has binary dependencies on other open source projects.  These components are listed in the [THIRD-PARTY.txt](THIRD-PARTY.txt) file.

## Tools we use

If you decide to fork `tmc-auth` or build it locally, you will be interested in learning more about the tools we use to ensure we are developing high-quality code. The tools we use are:

* [JaCoCo](https://www.eclemma.org/jacoco/) for code coverage
* [Sonar](https://www.sonarqube.org/) for static code analysis
* [RevAPI](https://revapi.org/) to validate that we are not introducing breaking API changes
* [DependencyTrack](https://dependencytrack.org/) to analyze dependencies
