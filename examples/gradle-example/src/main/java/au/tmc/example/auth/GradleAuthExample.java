package au.tmc.example.auth;

import static java.lang.String.format;

import com.autonomic.tmc.auth.ClientCredentialsTokenSupplier;
import com.autonomic.tmc.auth.TokenSupplier;
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
public class GradleAuthExample implements CommandLineRunner {

    private static final Logger LOGGER = Logger.getLogger("GradleAuthExample");

    @Value("${TMC_CLIENT_ID}")
    private String clientId;

    @Value("${TMC_CLIENT_SECRET}")
    private String clientSecret;

    @Value("${TMC_TOKEN_URL:https://accounts.autonomic.ai/auth/realms/iam/protocol/openid-connect/token}")
    private String tokenUrl;

    @Value("${TMC_BASE_URL:https://api.autonomic.ai}")
    private String baseUrl;

    public static void main(String[] args) {
        SpringApplication.run(GradleAuthExample.class, args);
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
        try {
            TokenSupplier tokenSupplier;

            //#Example 1: REST Authentication token for Production
            tokenSupplier = createTokenSupplier(clientId, clientSecret);
            printToken(tokenSupplier, "with default URL");

            //Example 2: REST Authentication token when you want to tell the tokenSupplier what
            // environment to connect to
            tokenSupplier = createTokenSupplierWithTokenUrl(clientId, clientSecret, tokenUrl);
            printToken(tokenSupplier, "with provided URL");

            //Example 3: An authenticated gRPC channel that can be used when creating a client stub
            AuthenticatedChannelBuilder channelBuilder = new AuthenticatedChannelBuilder(
                tokenSupplier);

            Channel authenticatedGRPCChannel = channelBuilder.buildWithUrl(baseUrl);

            String msg = format("Authenticated Channel: %s", authenticatedGRPCChannel);
            LOGGER.info(msg);

            // ExampleStub exampleStub = ExampleGrpc.newStub(authenticatedGRPCChannel);
            //
            // Note: `newStub` could also be a blocking stub, and async call stub, and a future
            // stub. Check with the Autonomic service to learn what is the best stub to use for the
            // service.

        } catch (MalformedURLException | IllegalArgumentException e) {
            LOGGER.log(Level.SEVERE, "Generic message, Something went wrong: ", e);
            System.exit(-1);
        }
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
     * tokenUrl is 'https://accounts.autonomic.ai/auth/realms/iam/protocol/openid-connect/token'.
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
