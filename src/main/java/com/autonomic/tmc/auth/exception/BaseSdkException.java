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

import static com.autonomic.tmc.auth.exception.ProjectProperties.DEFAULT_NAME;
import static com.autonomic.tmc.auth.exception.ProjectProperties.DEFAULT_VERSION;
import static java.util.Optional.ofNullable;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BaseSdkException extends RuntimeException {

    BaseSdkException(String message) {
        super(message);
        log.warn(message);
    }

    BaseSdkException(String message, Throwable cause) {
        super(message, cause);
        log.warn(message, cause);
    }

    static String buildMessage(ErrorSourceType errorSourceType, String clientMessage) {
        try {
            final ProjectProperties properties = ProjectProperties.get();
            final String name = ofNullable(properties.getName()).orElse(DEFAULT_NAME);
            final String version = ofNullable(properties.getVersion()).orElse(DEFAULT_VERSION);
            return String.format("%s-%s-%s: %s.", name, version, errorSourceType, clientMessage);
        } catch (Throwable e) {
            return DEFAULT_NAME + "~" + DEFAULT_VERSION + "~" + errorSourceType.toString() + clientMessage;
        }
    }
}
