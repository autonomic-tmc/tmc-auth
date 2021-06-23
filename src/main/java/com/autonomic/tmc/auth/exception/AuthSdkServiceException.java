/*-
 * ‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾
 * TMC Auth SDK
 * ——————————————————————————————————————————————————————————————————————————————
 * Copyright (C) 2016 - 2020 Autonomic, LLC
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
package com.autonomic.tmc.auth.exception;

import static java.util.Optional.ofNullable;

import com.nimbusds.oauth2.sdk.ErrorObject;
import com.nimbusds.oauth2.sdk.TokenErrorResponse;
import java.util.Objects;
import lombok.Getter;

@SuppressWarnings("squid:S110")
public class AuthSdkServiceException extends com.autonomic.tmc.exception.SdkServiceException {

    @Getter
    private int httpStatusCode = 500;

    public AuthSdkServiceException(String clientMessage, Throwable cause) {
        super(clientMessage, cause, AuthSdkServiceException.class);
    }

    public AuthSdkServiceException(String clientMessage, TokenErrorResponse errorResponse) {
        super(buildMessage(clientMessage, errorResponse), AuthSdkServiceException.class);
        if (Objects.nonNull(errorResponse)) {
            final ErrorObject error = ofNullable(errorResponse.getErrorObject())
                .orElseGet(() -> new ErrorObject("0"));
            httpStatusCode = error.getHTTPStatusCode();
        }
    }

    private static String buildMessage(String clientMessage, TokenErrorResponse errorResponse) {
        StringBuilder sb = new StringBuilder(clientMessage);
        if (Objects.nonNull(errorResponse)) {
            final ErrorObject error = ofNullable(errorResponse.getErrorObject())
                .orElseGet(() -> new ErrorObject("0"));
            sb.append(String.format("Error: code=%s and httpStatusCode=%s",
                ofNullable(error.getCode()).orElseGet(()->"0"),
                error.getHTTPStatusCode()));
        }
        return sb.toString();
    }
}
