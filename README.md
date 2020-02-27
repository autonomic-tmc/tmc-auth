# Authenticating with TMC

## Let's get authenticated with an example application

We have built a runnable example application that demonstrates how to include the `tmc-auth` SDK and connect to TMC services in a secure manner. You can use this example application to login and retrieve an access token.

- [Getting authenticated using the Maven Example](./examples/maven-example)
- [Getting authenticated using the Gradle Example](./examples/gradle-example)

## tmc-auth SDK

Using the tmc-auth SDK with credentials provided by Autonomic, you can obtain a time-limited access token to be used with other services available on the platform.

With this library, you don't need to worry about expiring tokens. The token you `get()` is always valid.  If a token expires, this library will automatically get a new one.

## Adding tmc-auth as a dependency

- [Maven - pom.xml](./examples/maven-example/pom.xml)
- [Gradle - build.gradle](./examples/gradle-example/build.gradle)

See our [examples](examples) for more information on including this library in your Maven or Gradle project.

## Usage

```java
    TokenSupplier supplier = ClientCredentialsTokenSupplier.builder()
        .clientId("<your-client-id>")
        .clientSecret("<your-client-secret>")
        .tokenUrl("<your-token-url-if-not-using-default>")
        .build());

    String token = supplier.get();
```

## Configuration Options

| Builder Param | Description | Required/Optional | Default Value|
|---------------|-------------------------------------------------|-----------|-----------------------|
| clientId | This is your TMC Client identifier provided to you by Au. | Required | none |
| clientSecret | This is your TMC Client secret provided to you by Au. | Required | none |
| tokenUrl | The authentication url | Optional | https://accounts.autonomic.ai/auth/realms/iam/protocol/openid-connect/token|

## Troubleshooting

The `ClientCredentialsTokenSupplier.get()` primarily throws two exceptions:

[AuthenticationCommunicationException](src/main/java/com/autonomic/tmc/auth/AuthenticationCommunicationException.java) - A temporary communication problem, and retrying at a later point will likely succeed.

[AuthenticationFailedException](src/main/java/com/autonomic/tmc/auth/AuthenticationFailedException.java) Your credentials are incorrect and should be corrected before calling again.

## Building

```shell
./mvnw clean install
```

## Running the Integration Tests

*NOTE:* You will need to run the `integration-tests` profile with the following environment variables to run integration tests.

- TMC_CLIENT_ID
- TMC_CLIENT_SECRET

For example:

```shell
./mvnw -Pintegration-tests clean verify
```

You can pass them as arguments to `mvnw` if you do not have them set on your path.

For example:

```shell
(export TMC_CLIENT_ID=your-client-id && export TMC_CLIENT_SECRET=your-client-secret && ./mvnw -Pintegration-tests clean verify)
```

## 3rd Party Components

This project has binary dependencies on other open source projects.  These components are listed in the [THIRD-PARTY.txt](THIRD-PARTY.txt) file.

## Tools we use

If you decide to fork `tmc-auth` or build it locally, you will be interested in learning more about the tools we use to ensure we are developing high-quality code. The tools we use are:

* [JaCoCo](https://www.eclemma.org/jacoco/) for code coverage
* [Sonar](https://www.sonarqube.org/) for static code analysis
* [RevAPI](https://revapi.org/) to validate that we are not introducing breaking API changes
