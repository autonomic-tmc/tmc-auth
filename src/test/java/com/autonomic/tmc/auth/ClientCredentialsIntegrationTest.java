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


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

public class ClientCredentialsIntegrationTest {

    @Test
    @EnabledIfSystemProperty(named = "tmc.integration.tests", matches = "true")
    void integration_test() {
        //given environment settings for clientId and secret
        Settings settings = environmentSettings();

        //and a token supplier
        ClientCredentialsTokenSupplier tokenSupplier = ClientCredentialsTokenSupplier
            .builder()
            .clientId(settings.clientId)
            .clientSecret(settings.clientSecret)
            .build();

        //when a token is requests
        String token = tokenSupplier.get();

        //then we get a token
        Assertions.assertNotNull(token);

        Token tokenObject = tokenSupplier.getExistingToken();
        //and the token is not expired
        Assertions.assertFalse(tokenSupplier.getExistingToken().isExpired());
    }


    static class Settings {

        String clientId;
        String clientSecret;

        Settings(String clientId, String clientSecret) {
            this.clientId = clientId;
            this.clientSecret = clientSecret;
        }
    }

    static Settings environmentSettings() {
        String clientId = System.getenv().get("TMC_CLIENT_ID");
        String clientSecret = System.getenv().get("TMC_CLIENT_SECRET");

        if (clientId != null && clientSecret != null) {
            return new Settings(clientId, clientSecret);
        }

        throw new RuntimeException(
            "These environment variables are required: TMC_CLIENT_ID, TMC_CLIENT_SECRET");
    }

}
