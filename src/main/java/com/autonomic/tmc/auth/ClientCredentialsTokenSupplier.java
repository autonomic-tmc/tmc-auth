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

import com.autonomic.tmc.auth.exception.BaseSdkException;
import com.autonomic.tmc.auth.exception.SdkClientException;
import com.autonomic.tmc.auth.exception.SdkServiceException;
import com.nimbusds.oauth2.sdk.TokenResponse;
import com.nimbusds.oauth2.sdk.token.AccessToken;
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

    private final BaseClientCredentialsTokenSupplier baseSupplier;
    private Token token;

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
        try {
            baseSupplier = new BaseClientCredentialsTokenSupplier(clientId, clientSecret, tokenUrl);
        } catch (SdkClientException e) {
            throw new IllegalArgumentException(e.getMessage(), e.getCause());
        }

    }

    /**
     * This method responds with a valid String representation of the Bearer token.  If a token
     * becomes expired, a new valid token will be requested automatically. This method will always
     * return a valid token.
     *
     * @return String representation of Bearer token.
     * @throws AuthenticationCommunicationException Thrown when an unexpected condition is
     *                                              encountered while making the client credentials
     *                                              grant POST
     * @throws AuthenticationFailedException        Thrown when the credentials that were provided
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

    Token getExistingToken() {
        return token;
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

        return baseSupplier.buildToken(accessToken);
    }

    private AccessToken processTokenResponse(TokenResponse response) {
        try {
            return baseSupplier.processTokenResponse(response);
        } catch (SdkServiceException e) {
            int statusCode = e.getStatusCode();
            if (statusCode == 401 || statusCode == 400) {
                throw new AuthenticationFailedException(e.getMessage());
            }
            throw new AuthenticationCommunicationException(e.getMessage());
        }
    }

    private TokenResponse executeTokenRequest() {
        try {
            return baseSupplier.executeTokenRequest();
        } catch (BaseSdkException e) {
            throw new AuthenticationCommunicationException(e.getMessage());
        }
    }
}
