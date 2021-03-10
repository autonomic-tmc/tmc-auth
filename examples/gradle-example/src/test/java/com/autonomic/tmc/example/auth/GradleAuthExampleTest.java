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
class GradleAuthExampleTest {

    @Autowired
    private GradleAuthExample example;

    @Captor
    private ArgumentCaptor<Exception> errorMessages;

    @Spy
    LoggerStub loggerStub;

    @Test
    void givenBadCredentials_whenAuthenticating_thenVerifyErrorMessageContainsSdkVersion() {
        GradleAuthExample.LOGGER = loggerStub;
        example.run("hi");
        Mockito.verify(GradleAuthExample.LOGGER, Mockito.times(2)).log(
            eq(Level.SEVERE),
            eq("Generic message, Something went wrong: "),
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
