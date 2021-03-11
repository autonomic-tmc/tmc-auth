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
