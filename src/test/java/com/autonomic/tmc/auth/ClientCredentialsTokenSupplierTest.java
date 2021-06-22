/*-
 * ‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾
 * tmc-auth
 * ——————————————————————————————————————————————————————————————————————————————
 * Copyright (C) 2016 - 2019 Autonomic, LLC - All rights reserved
 * ——————————————————————————————————————————————————————————————————————————————
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License
 * ______________________________________________________________________________
 */
package com.autonomic.tmc.auth;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.of;
import static org.mockito.Mockito.when;

import com.autonomic.tmc.auth.exception.AuthSdkClientException;
import com.autonomic.tmc.auth.exception.AuthSdkServiceException;
import com.autonomic.tmc.exception.ProjectProperties;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

@Slf4j
class ClientCredentialsTokenSupplierTest {

    private static final String DEFAULT_TOKEN_URL = "https://accounts.autonomic.ai/v1/auth/oidc/token";

    private WireMockServer authServer;

    @BeforeEach
    void setUp() {
        authServer = new WireMockServer(WireMockConfiguration.options().dynamicPort());
        authServer.start();
    }

    @AfterEach
    void tearDown() {
        authServer.stop();
    }

    @ParameterizedTest(name = "Given Cause: {1}")
    @MethodSource("exceptionsToTest")
    <T extends Throwable> void anyExceptionThrown_alwaysRethrowsAsSdkException(String expectedMessage, Throwable cause,
        Class<T> expectedExceptionOfType) {
        startAuthStub();

        assertThatExceptionOfType(expectedExceptionOfType).isThrownBy(() -> {
            ClientCredentialsTokenSupplier supplier = buildSupplierForStub();

            Token token = Mockito.mock(Token.class);
            supplier.setToken(token);
            when(token.getValue()).thenThrow(cause);
            // Act
            supplier.get();
        }).withMessageContaining(expectedMessage)
            .withCauseInstanceOf(RuntimeException.class);
    }

    private static Stream<Arguments> exceptionsToTest() {
        RuntimeException cause = new RuntimeException("Bada!");
        final String expectedMessage = "Boom!";
        return Stream.of(
            of(expectedMessage, new AuthSdkClientException(expectedMessage, cause), AuthSdkClientException.class),
            of(expectedMessage, new AuthSdkServiceException(expectedMessage, cause), AuthSdkServiceException.class),
            of("Undocumented exception occurred", new RuntimeException(expectedMessage), AuthSdkClientException.class)
        );
    }

    @Test
    void actuallyValidUrl_hasInvalidCharacters_wrapsAndPropagatesException() {
        assertThatExceptionOfType(AuthSdkServiceException.class).isThrownBy(() -> {
            ClientCredentialsTokenSupplier.builder()
                .clientSecret("a-secret")
                .clientId("a-client-id")
                .tokenUrl("http://google.com")
                .build().get();

        }).withMessageContaining("405");
    }

    @Nested
    class ConstructorOnly {

        @Test
        void rfc2396_tokenUrlSyntax_throwsSdkClientException() {
            assertThatExceptionOfType(AuthSdkClientException.class).isThrownBy(() -> {
                ClientCredentialsTokenSupplier.builder()
                    .clientSecret("a-secret")
                    .clientId("a-client-id")
                    .tokenUrl("http:\\google.com") // backslashes instead of forward slashes
                    .build();
            }).withCauseInstanceOf(URISyntaxException.class);
        }

        @Test
        void blankTokenUrl_isConvertedTo_defaultTokenUrl() {
            final ClientCredentialsTokenSupplier actualClientTokenSupplier = ClientCredentialsTokenSupplier.builder()
                .clientSecret("a-secret")
                .clientId("a-client-id")
                .tokenUrl("   ")
                .build();

            assertThat(actualClientTokenSupplier.getTokenUrl()).isEqualTo(DEFAULT_TOKEN_URL);
        }

        @Test
        void nullTokenUrl_isConvertedTo_defaultTokenUrl() {
            final ClientCredentialsTokenSupplier actualClientTokenSupplier = ClientCredentialsTokenSupplier.builder()
                .clientSecret("a-secret")
                .clientId("a-client-id")
                .tokenUrl(null)
                .build();

            assertThat(actualClientTokenSupplier.getTokenUrl()).isEqualTo(DEFAULT_TOKEN_URL);
        }

        @Test
        void malFormedTokenUrl_throwsSdkClientException() {
            assertThatExceptionOfType(AuthSdkClientException.class).isThrownBy(() -> ClientCredentialsTokenSupplier.builder()
                .clientSecret("a-secret")
                .clientId("a-client-id")
                .tokenUrl("\\\\")
                .build())
                .withCauseInstanceOf(URISyntaxException.class)
                .withMessageContaining("is not a valid URL");
        }

        @Test
        void cannot_construct_without_clientId() {
            assertThatExceptionOfType(AuthSdkClientException.class).isThrownBy(() -> ClientCredentialsTokenSupplier.builder()
                .clientId(null)
                .clientSecret("a-secret")
                .build())
                .withMessageContaining("Both client id and client secret are required and cannot be blank")
                .withNoCause();
        }

        @Test
        void cannot_construct_without_clientSecret() {
            assertThatExceptionOfType(AuthSdkClientException.class).isThrownBy(() -> ClientCredentialsTokenSupplier.builder()
                .clientId("a-client-id")
                .clientSecret(null)
                .build())
                .withMessageContaining("Both client id and client secret are required and cannot be blank")
                .withNoCause();
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {400, 401})
    void response_400_or_401_throws_auth_failed_exception(int responseCode) {
        // given a response indicating problem with creds
        authServer.stubFor(
            post("/relative-token-url")
                .willReturn(
                    aResponse().withStatus(responseCode)
                )
        );

        // and a token supplier
        ClientCredentialsTokenSupplier tokenSupplier = buildSupplierForStub();

        // an auth failure exception is thrown when token is requested
        assertThatThrownBy(tokenSupplier::get)
            .isInstanceOf(AuthSdkServiceException.class)
            .hasMessageContaining("Authorization failed for user [")
            .hasMessageContaining("at tokenUrl [")
            .hasFieldOrPropertyWithValue("httpStatusCode", responseCode);
    }

    @ParameterizedTest
    @ValueSource(ints = {500, 302, 404, 418})
    void response_not_recognized_throws_communication_exception(int responseCode) {

        // given a bad response
        authServer.stubFor(
            post("/relative-token-url")
                .willReturn(
                    aResponse().withStatus(responseCode)
                )
        );

        // and a token supplier
        ClientCredentialsTokenSupplier tokenSupplier = buildSupplierForStub();

        // a communication exception is thrown when token is requested
        assertThatThrownBy(tokenSupplier::get)
            .isInstanceOf(AuthSdkServiceException.class)
            .hasMessageContaining("Unexpected response [")
            .hasMessageContaining("from tokenUrl [")
            .hasFieldOrPropertyWithValue("httpStatusCode", responseCode);
    }

    @Test
    void no_network_throws_communication_exception() {

        //given a client that points to nowhere
        ClientCredentialsTokenSupplier tokenSupplier = ClientCredentialsTokenSupplier
            .builder()
            .clientId("a-client-id")
            .clientSecret("a-client-secret")
            .tokenUrl("http://nowhere:666/relative-token-url")
            .build();

        // an exception is thrown when token is requested
        assertThrows(AuthSdkServiceException.class, tokenSupplier::get);
    }

    @Test
    void does_not_request_new_token_until_token_expires() {

        // given a valid response with token that expires in 1 second
        startAuthStub();

        // and a client
        ClientCredentialsTokenSupplier tokenSupplier = buildSupplierForStub();

        //when an auth token is retrieved 3 times
        tokenSupplier.get();
        tokenSupplier.get();
        tokenSupplier.get();

        //then only one call was made
        authServer.verify(1, postRequestedFor(urlEqualTo("/relative-token-url")));

        // when the token expires
        await().atMost(2, TimeUnit.SECONDS)
            .until(() -> tokenSupplier.getExistingToken().isExpired());

        // and auth token is retrieved again
        tokenSupplier.get();

        //then a second call was made
        authServer.verify(2, postRequestedFor(urlEqualTo("/relative-token-url")));
    }

    @Test
    void sends_a_user_agent_to_server() {
        startAuthStub();
        ClientCredentialsTokenSupplier tokenSupplier = buildSupplierForStub();

        tokenSupplier.get();

        authServer.verify(1, postRequestedFor(urlEqualTo("/relative-token-url")).withHeader("User-Agent", equalTo(
            ProjectProperties.get(ClientCredentialsTokenSupplier.class).getFormattedUserAgent("tmc-auth"))));
    }

    @Test
    void sends_the_correct_request_and_maps_response() {

        //given a 200 response when params match
        authServer.stubFor(
            post("/relative-token-url")
                .withRequestBody(containing("client_id=a-client-id"))
                .withRequestBody(containing("client_secret=a-client-secret"))
                .willReturn(
                    aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBody(sampleOauthResponse())
                )
        );

        //and a client
        ClientCredentialsTokenSupplier tokenSupplier = buildSupplierForStub();

        //when an auth token is retrieved
        String token = tokenSupplier.get();

        //then the mock auth token was retrieved
        assertEquals("mock-auth-token", token);

    }

    private String sampleOauthResponse() {
        return "{\"access_token\":\"mock-auth-token\",\"expires_in\":\"11\",\"token_type\":\"bearer\"}";
    }

    private void startAuthStub() {
        authServer.stubFor(
            post("/relative-token-url")
                .willReturn(
                    aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBody(sampleOauthResponse())
                )
        );
    }

    private ClientCredentialsTokenSupplier buildSupplierForStub() {
        return ClientCredentialsTokenSupplier.builder()
            .clientId("a-client-id")
            .clientSecret("a-client-secret")
            .tokenUrl("http://localhost:" + authServer.port() + "/relative-token-url")
            .build();
    }
}
