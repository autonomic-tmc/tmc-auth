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

import com.autonomic.tmc.auth.exception.SdkClientException;
import com.autonomic.tmc.auth.exception.SdkServiceException;
import com.nimbusds.oauth2.sdk.AccessTokenResponse;
import com.nimbusds.oauth2.sdk.AuthorizationGrant;
import com.nimbusds.oauth2.sdk.ClientCredentialsGrant;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.TokenRequest;
import com.nimbusds.oauth2.sdk.TokenResponse;
import com.nimbusds.oauth2.sdk.auth.ClientAuthentication;
import com.nimbusds.oauth2.sdk.auth.ClientSecretPost;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * This class provides an OAuth 2.0 - Client Credentials Grant based implementation of {@link
 * TokenSupplier}.
 *
 * <p>Reference: <a target="_blank" href="https://tools.ietf.org/html/rfc6749#section-1.3.4">RFC
 * 6749 section-1.3.4</a></p>
 */
@Slf4j
public class ClientCredentialsTokenSupplier implements TokenSupplier {

    private static final String DEFAULT_TOKEN_URL = "https://accounts.autonomic.ai/auth/realms/iam/protocol/openid-connect/token";

    // properties
    private final String clientId;
    private final String clientSecret;
    private final String tokenUrl;
    private final URI tokenEndpoint;

    // state
    private Token token = null;

    /**
     * Public Constructor.
     *
     * <p>A builder is also provided and encouraged as an option in lieu of using this constructor
     * directly.</p>
     *
     * <p>Builder Example: </p>
     * <pre>{@code
     * ClientCredentialsTokenSupplier tokenSupplier = ClientCredentialsTokenSupplier
     *             .builder()
     *             .clientId("your-client-id")
     *             .clientSecret("your-client-secret")
     *             .tokenUrl("your-token-url-if-not-using-default")
     *             .build();
     * }</pre>
     *
     * @param clientId     The client_id param of the client credentials request
     * @param clientSecret The client_secret param of the client credentials request
     * @param tokenUrl     The URL against which the client credentials request will POST. A null
     *                     value will result in the default value being used. Default:
     *                     https://accounts.autonomic.ai/auth/realms/iam/protocol/openid-connect/token
     */
    @Builder
    public ClientCredentialsTokenSupplier(@NonNull String clientId,
        @NonNull String clientSecret, String tokenUrl) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        //TODO: Add validation for clientId and Secret
        this.tokenUrl = (tokenUrl != null) ? tokenUrl : DEFAULT_TOKEN_URL;
        try {
            this.tokenEndpoint = new URI(this.tokenUrl);
        } catch (URISyntaxException e) {
            throw new SdkClientException(
                String.format("tokenUrl [%s] is not a valid URL", tokenUrl), e);
        }
    }

    /**
     * This method responds with a valid String representation of the Bearer token.  If a token
     * becomes expired, a new valid token will be requested automatically. This method will always
     * return a valid token.
     *
     * @return String representation of Bearer token.
     * @throws SdkServiceException Thrown when an unexpected condition is
     *                                              encountered while making the client credentials
     *                                              grant POST
     * @throws SdkClientException        Thrown when the credentials that were provided
     *                                              are expressly rejected.
     */
    @Override
    public synchronized String get() {
        if (token != null && !token.isExpired()) {
            log.debug("Cached token found.");
            return token.getValue();
        }
        resetToken();
        return token.getValue();
    }

    private void resetToken() {
        // if not already reset
        if (token == null || token.isExpired()) {
            log.debug("No cached token. Retrieving a new token.");
            token = authenticate();
        }
    }

    private Token authenticate() {

        TokenResponse response = executeTokenRequest();
        AccessToken accessToken = processTokenResponse(response);

        return buildToken(accessToken);
    }

    private TokenRequest createTokenRequest() {
        AuthorizationGrant clientGrant = new ClientCredentialsGrant();
        ClientID clientID = new ClientID(clientId);
        Secret secret = new Secret(this.clientSecret);
        ClientAuthentication clientAuthentication = new ClientSecretPost(clientID, secret);

        return new TokenRequest(this.tokenEndpoint, clientAuthentication, clientGrant, null);
    }

    AccessToken processTokenResponse(TokenResponse response) {

        if (!response.indicatesSuccess()) {
            HTTPResponse httpResponse = response.toHTTPResponse();

            int responseCode = httpResponse.getStatusCode();
            if (responseCode == 401 || responseCode == 400) {
                throw new SdkServiceException(String
                    .format("Authorization failed for user [%s] at tokenUrl [%s]",
                        this.clientId, this.tokenUrl), response.toErrorResponse());
            }

            throw new SdkServiceException(String
                .format("Unexpected response [%s] from tokenUrl [%s]: %s", responseCode,
                    this.tokenUrl, httpResponse.getContent()), response.toErrorResponse());
        }

        AccessTokenResponse successResponse = response.toSuccessResponse();

        return successResponse.getTokens().getAccessToken();
    }

    TokenResponse executeTokenRequest() {
        TokenRequest request = createTokenRequest();

        HTTPResponse response;
        try {
            response = request.toHTTPRequest().send();
        } catch (IOException e) {
            throw new SdkServiceException(String
                .format("Unexpected issue communicating with tokenUrl [%s]", this.tokenUrl), e);
        }
        try {
            return TokenResponse.parse(response);
        } catch (ParseException e) {
            throw new SdkClientException(String
                .format("Unexpected issue parsing token response: [%s]", response.getContent()), e);
        }
    }

    Token buildToken(AccessToken accessToken) {
        return Token.builder()
            .value(accessToken.getValue())
            .expiration(Instant.now().plus(accessToken.getLifetime(), ChronoUnit.SECONDS))
            .build();
    }

    Token getExistingToken() {
        return this.token;
    }
}
