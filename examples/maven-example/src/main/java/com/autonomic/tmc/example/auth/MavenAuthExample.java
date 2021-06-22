package com.autonomic.tmc.example.auth;

import static java.lang.String.format;

import com.autonomic.tmc.auth.ClientCredentialsTokenSupplier;
import com.autonomic.tmc.auth.TokenSupplier;
import com.autonomic.tmc.exception.BaseSdkException;
import com.google.common.annotations.VisibleForTesting;
import io.grpc.Channel;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@Configuration
public class MavenAuthExample implements CommandLineRunner {

    @VisibleForTesting
    static Logger LOGGER = Logger.getLogger("MavenAuthExample");

    @Value("${tmc.auth.clientId: }")
    private String clientId;

    @Value("${tmc.auth.clientSecret: }")
    private String clientSecret;

    @Value("${tmc.auth.tokenUrl:https://accounts.autonomic.ai/v1/auth/oidc/token}")
    private String tokenUrl;

    @Value("${tmc.some-service.serviceUrl:https://api.autonomic.ai}")
    private String serviceUrl;

    public static void main(String[] args) {
        SpringApplication.run(MavenAuthExample.class, args);
    }

    @Override
    public void run(String... args) {
        try {
            this.run();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(),e.getStackTrace());
        }
    }

    private void run() {
        TokenSupplier tokenSupplier = null;

        //#Example 1: REST Authentication token for Production
        try {
            tokenSupplier = createTokenSupplier(clientId, clientSecret);
            printToken(tokenSupplier, "with default URL");
        } catch (BaseSdkException e) {
            LOGGER.log(Level.SEVERE,
                "Example 1: REST Authentication token for Production failed: ", e);
        }

        //Example 2: REST Authentication token when you want to tell the tokenSupplier what
        // environment to connect to
        try {
            tokenSupplier = createTokenSupplierWithTokenUrl(clientId, clientSecret, tokenUrl);
            printToken(tokenSupplier, "with provided URL");
        } catch (BaseSdkException e) {
            LOGGER.log(Level.SEVERE,
                "Example 2: REST Authentication token for your environment failed: ", e);
        }

        //Example 3: An authenticated gRPC channel that can be used when creating a client stub
        try {
            AuthenticatedChannelBuilder channelBuilder = new AuthenticatedChannelBuilder(
                tokenSupplier);

            Channel authenticatedGRPCChannel = channelBuilder.buildWithUrl(serviceUrl);

            String msg = format("Authenticated Channel: %s", authenticatedGRPCChannel);
            LOGGER.info(msg);
        } catch (BaseSdkException | MalformedURLException e) {
            LOGGER.log(Level.SEVERE,
                "Example 3: A gRPC authenticated token failed: ", e);
        }

        // ExampleStub exampleStub = ExampleGrpc.newStub(authenticatedGRPCChannel);
        //
        // Note: `newStub` could also be a blocking stub, and async call stub, and a future
        // stub. Check with the Autonomic service to learn what is the best stub to use for the
        // service.
    }

    private void printToken(TokenSupplier tokenSupplier, String partialMsg) {
        String token = tokenSupplier.get();
        String msg =
            format("We got a token %s: %s%s", partialMsg, token.substring(0, 10), "**REDACTED**");
        LOGGER.info(msg);
    }

    /**
     * Configure token supplier with your clientId and clientSecret.  In most use-cases, this is all that is required. Conventionally (for
     * security reasons), you would typically read these values in from an externalized source like environment variables or properties
     * files.
     *
     * By default, the token supplier is configured to communicate with the production environment.
     */
    private TokenSupplier createTokenSupplier(String clientId, String clientSecret) {
        return ClientCredentialsTokenSupplier.builder()
            .clientId(clientId)
            .clientSecret(clientSecret)
            .build();
    }

    /**
     * This is an example of how to retrieve a token from a non-production environment. When not explicitly set, the default value of
     * tokenUrl is 'https://accounts.autonomic.ai/v1/auth/oidc/token'.
     *
     * @param tokenUrl - String representation of a token URL
     * @return TokenSupplier
     */
    private TokenSupplier createTokenSupplierWithTokenUrl(String clientId, String clientSecret,
        String tokenUrl) {
        return ClientCredentialsTokenSupplier.builder()
            .clientId(clientId)
            .clientSecret(clientSecret)
            .tokenUrl(tokenUrl)
            .build();
    }
}
