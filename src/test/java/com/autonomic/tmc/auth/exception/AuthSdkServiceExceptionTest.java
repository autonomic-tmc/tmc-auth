/*-
 * ‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾
 * TMC Auth SDK
 * ——————————————————————————————————————————————————————————————————————————————
 * Copyright (C) 2016 - 2024 Autonomic, LLC
 * ——————————————————————————————————————————————————————————————————————————————
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 * ______________________________________________________________________________
 */
package com.autonomic.tmc.auth.exception;

import static org.mockito.Mockito.when;

import com.nimbusds.oauth2.sdk.ErrorObject;
import com.nimbusds.oauth2.sdk.TokenErrorResponse;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthSdkServiceExceptionTest {

    @Mock
    TokenErrorResponse mockTokenErrorResponse;

    @Mock
    ErrorObject mockErrorObject;

    @Test
    void getHttpStatusCode_returnsValuesBasedOnTokenErrorResponse() {
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(new AuthSdkServiceException("", new Throwable()).getHttpStatusCode())
            .describedAs("When Throwable constructor is used").isEqualTo(500);
        softly.assertThat(new AuthSdkServiceException("", (TokenErrorResponse) null).getHttpStatusCode())
            .describedAs("When TokenErrorResponse is null").isEqualTo(500);
        when(mockTokenErrorResponse.getErrorObject()).thenReturn(null);
        softly.assertThat(new AuthSdkServiceException("", mockTokenErrorResponse).getHttpStatusCode())
            .describedAs("When TokenErrorResponse.ErrorObject is null").isEqualTo(0);
        when(mockErrorObject.getHTTPStatusCode()).thenReturn(400);
        softly.assertThat(new AuthSdkServiceException("", new TokenErrorResponse(mockErrorObject)).getHttpStatusCode())
            .describedAs("When ErrorObject.HttpStatusCode is 400").isEqualTo(400);
        softly.assertAll();
    }
}
