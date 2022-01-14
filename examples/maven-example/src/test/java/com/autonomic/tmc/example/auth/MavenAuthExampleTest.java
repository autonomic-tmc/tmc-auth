/*-
 * ‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾
 * TMC Auth SDK
 * ——————————————————————————————————————————————————————————————————————————————
 * Copyright (C) 2016 - 2022 Autonomic, LLC
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
package com.autonomic.tmc.example.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MavenAuthExampleTest {

    @Autowired
    private MavenAuthExample example;

    @Captor
    private ArgumentCaptor<Exception> errorMessages;

    @Spy
    LoggerStub loggerStub;

    @Test
    void givenBadCredentials_whenAuthenticating_thenVerifyErrorMessageContainsSdkVersion() {
        // Arrange
        MavenAuthExample.LOGGER = loggerStub;
        // Act
        example.run("hi");
        // Assert
        Mockito.verify(MavenAuthExample.LOGGER, Mockito.times(1)).log(
            eq(Level.SEVERE),
            eq("Example 1: REST Authentication token for Production failed: "),
            errorMessages.capture());
        String actualError = errorMessages.getAllValues().get(0).getMessage();

        assertThat(actualError)
            .contains("tmc-auth")
            .contains("-SERVICE")
            .contains("Authorization failed for user [NOTAREALCLIENTID]");
    }

    private static class LoggerStub extends Logger {

        public LoggerStub() {
            super(LoggerStub.class.toString(), null);
        }

        @Override
        public void log(Level level, String message, Throwable e) {
            super.log(level, message, e);
        }
    }

}
