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
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ClientCredentialsTokenSupplierTest {

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

    @Test
    void malformed_token_url_throws_exception() {
        assertThrows(IllegalArgumentException.class, () -> {
            ClientCredentialsTokenSupplier.builder()
                .clientSecret("a-secret")
                .clientId("a-client-id")
                .tokenUrl("\\\\")
                .build();
        });
    }

    @Test
    void cannot_construct_without_clientId() {
        assertThrows(Exception.class, () -> {
            ClientCredentialsTokenSupplier.builder()
                .clientId(null)
                .clientSecret("a-secret")
                .build();
        });
    }

    @Test
    void cannot_construct_without_clientSecret() {
        assertThrows(Exception.class, () -> {
            ClientCredentialsTokenSupplier.builder()
                .clientId("a-client-id")
                .clientSecret(null)
                .build();
        });
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
        ClientCredentialsTokenSupplier tokenSupplier = ClientCredentialsTokenSupplier
            .builder()
            .clientId("a-client-id")
            .clientSecret("a-client-secret")
            .tokenUrl("http://localhost:" + authServer.port() + "/relative-token-url")
            .build();

        // an auth failure exception is thrown when token is requested
        assertThrows(AuthenticationFailedException.class, tokenSupplier::get);

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
        ClientCredentialsTokenSupplier tokenSupplier = ClientCredentialsTokenSupplier
            .builder()
            .clientId("a-client-id")
            .clientSecret("a-client-secret")
            .tokenUrl("http://localhost:" + authServer.port() + "/relative-token-url")
            .build();

        // a communication exception is thrown when token is requested
        assertThrows(AuthenticationCommunicationException.class, tokenSupplier::get);

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
        assertThrows(AuthenticationCommunicationException.class, tokenSupplier::get);

    }

    @Test
    void does_not_request_new_token_until_token_expires() throws Exception {

        // given a valid response with token that expires in 1 second
        authServer.stubFor(
            post("/relative-token-url")
                .willReturn(
                    aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBody(sampleOauthResponse())
                )
        );

        // and a client
        ClientCredentialsTokenSupplier tokenSupplier = ClientCredentialsTokenSupplier
            .builder()
            .clientId("a-client-id")
            .clientSecret("a-client-secret")
            .tokenUrl("http://localhost:" + authServer.port() + "/relative-token-url")
            .build();

        //when an auth token is retrieved 3 times
        tokenSupplier.get();
        tokenSupplier.get();
        tokenSupplier.get();

        //then only one call was made
        authServer.verify(1, postRequestedFor(urlEqualTo("/relative-token-url")));

        // when the token expires
        await().atMost(3, TimeUnit.SECONDS)
            .until(() -> tokenSupplier.getExistingToken().isExpired());

        // and auth token is retrieved again
        tokenSupplier.get();

        //then a second call was made
        authServer.verify(2, postRequestedFor(urlEqualTo("/relative-token-url")));

    }

    @Test
    void sends_the_correct_request_and_maps_response() throws Exception {

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
        ClientCredentialsTokenSupplier tokenSupplier = ClientCredentialsTokenSupplier
            .builder()
            .clientId("a-client-id")
            .clientSecret("a-client-secret")
            .tokenUrl("http://localhost:" + authServer.port() + "/relative-token-url")
            .build();

        //when an auth token is retrieved
        String token = tokenSupplier.get();

        //then the mock auth token was retrieved
        assertEquals("mock-auth-token", token);

    }

    private String sampleOauthResponse() {
        return "{\"access_token\":\"mock-auth-token\",\"expires_in\":\"31\",\"token_type\":\"bearer\"}";
    }

}
