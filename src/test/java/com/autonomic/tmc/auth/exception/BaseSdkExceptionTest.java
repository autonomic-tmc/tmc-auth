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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.autonomic.tmc.auth.exception.BaseSdkException.ProjectProperties;
import java.util.jar.Manifest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@Slf4j
class BaseSdkExceptionTest {

    private Manifest mockManifest;

    private ProjectProperties mockProperties;

    @BeforeEach
    void setup() {
        mockManifest = mock(Manifest.class);
        mockProperties = mock(ProjectProperties.class);
        ProjectProperties.singletonInstance=null;
    }


    @Test
    void projectProperties_returnsUnknown_when_POMFileNotFound() {
        ProjectProperties projectProperties = ProjectProperties.get();

        assertThat(projectProperties.getName()).isEqualTo("[ AUTONOMIC ]");
        assertThat(projectProperties.getVersion()).isEqualTo("[ SDK ]");
    }

    @Test
    void initializeManifest_throwsRuntimeException_doesNotCauseExceptionInitializer() {
        ProjectProperties.get().manifest = mockManifest;
        when(mockManifest.getMainAttributes()).thenThrow(new RuntimeException("End of the world"));

        try {
            new BaseSdkException("test");
        } catch (Throwable e) {
            fail("exceptions must not be thrown");
        }
        final String actualMessage2 = BaseSdkException
            .buildMessage(ErrorSourceType.SERVICE, "unit test");
        assertThat(actualMessage2).contains("AUTONOMIC");
        assertThat(actualMessage2).contains("SDK");
    }

    @Test
    void initializeManifest_returnsNull_NoNullPointerException_returnsDefault() {
        ProjectProperties.get().manifest = mockManifest;
        when(mockManifest.getMainAttributes()).thenReturn(null);

        try {
            new BaseSdkException("test");
        } catch (Throwable e) {
            fail("exceptions must not be thrown");
        }
        final String actualMessage2 = BaseSdkException
            .buildMessage(ErrorSourceType.SERVICE, "unit test");
        assertThat(actualMessage2).contains("AUTONOMIC");
        assertThat(actualMessage2).contains("SDK");
    }

    @Test
    void initializeProjectProperties_throwsRuntimeException_doesNotCauseExceptionInitializer() {
        ProjectProperties.singletonInstance = mockProperties;
        when(mockProperties.getName()).thenReturn(null);

        try {
            BaseSdkException actual = new BaseSdkException("test");
        } catch (Throwable e) {
            fail("exceptions must not be thrown");
        }
    }

    @Test
    void BinitializeProjectProperties_throwsRuntimeException_doesNotCauseExceptionInitializer() {
        ProjectProperties.singletonInstance = mockProperties;
        when(mockProperties.getName()).thenReturn(null);
        when(mockProperties.getVersion()).thenReturn(null);

        assertThat(BaseSdkException.buildMessage(ErrorSourceType.SDK_CLIENT, "test"))
            .doesNotContain("null");
    }

    @Test
    void CinitializeProjectProperties_throwsRuntimeException_doesNotCauseExceptionInitializer() {
        ProjectProperties.singletonInstance = mockProperties;
        when(mockProperties.getName()).thenReturn("null");
        when(mockProperties.getVersion()).thenReturn("null");

        assertThat(BaseSdkException.buildMessage(ErrorSourceType.SDK_CLIENT, "test"))
            .contains("null");
    }

    @Test
    void AinitializeProjectProperties_throwsRuntimeException_doesNotCauseExceptionInitializer() {
        ProjectProperties.singletonInstance = mockProperties;
        when(mockProperties.getName()).thenThrow(new RuntimeException("Bada Boom!"));

        BaseSdkException actual = new SdkClientException("test", new RuntimeException("Boom!"));
        assertThat(actual.getMessage()).contains("[ AUTONOMIC! ]");
    }

    @Test
    void initializeProjectProperties_withNull_thenNoExceptionThrown() {
        try {
            new BaseSdkException("something");
        } catch (Throwable e) {
            fail("exceptions must not be thrown");
        }
    }


}
