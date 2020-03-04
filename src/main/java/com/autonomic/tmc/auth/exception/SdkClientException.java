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

public class SdkClientException extends BaseSdkException {

    private final String clientMessage;

    public SdkClientException(ErrorSourceType errorSourceType, String clientMessage) {
        super(errorSourceType, clientMessage);
        this.clientMessage = clientMessage;
    }

    public SdkClientException(ErrorSourceType errorSourceType, String clientMessage, Throwable cause) {
        super(errorSourceType, clientMessage, cause);
        this.clientMessage = clientMessage;
    }

    @Override
    public String getMessage() {

        return String.format("tmc-auth-%s-%s: %s", "2.2.0-alpha", errorSourceType, clientMessage);
    }

    @Override
    public String toString() {
        return "SdkClientException{" +
            "\nerrorSourceType=" + errorSourceType +
            "\nerrorMessage=" + clientMessage +
            '}';
    }
}
