package com.autonomic.tmc.example.auth;

import static org.junit.jupiter.api.Assertions.assertTrue;
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
        MavenAuthExample.LOGGER = loggerStub;
        example.run("hi");
        Mockito.verify(MavenAuthExample.LOGGER).log(
            eq(Level.SEVERE),
            eq("Generic message, Something went wrong: "),
            errorMessages.capture());
        String actualError = errorMessages.getAllValues().get(0).getMessage();

        assertTrue(actualError.contains("tmc-auth"));
        assertTrue(actualError.contains("-SERVICE"));
        assertTrue(actualError.contains("Authorization failed for user [NOTAREALCLIENTID]"));
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
