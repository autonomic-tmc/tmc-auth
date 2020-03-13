package com.autonomic.tmc.example.auth;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MavenAuthExampleTest {

    @Autowired
    MavenAuthExample ex;
    @Test
    public void givenBadCredentials_whenAuthenticating_thenVerifyErrorMessageContainsSdkVersion() {
        ex.run("hi");
    }

}
