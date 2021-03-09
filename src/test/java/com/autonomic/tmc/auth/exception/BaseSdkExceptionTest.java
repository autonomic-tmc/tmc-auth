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
import static org.mockito.Mockito.when;

import com.autonomic.tmc.auth.exception.BaseSdkException.ProjectProperties;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@Slf4j
class BaseSdkExceptionTest {

    @Mock
    private ProjectProperties mockProperties;

    @Test
    void projectProperties_returnsUnknown_when_POMFileNotFound() {
        ProjectProperties projectProperties = new ProjectProperties();

        assertThat(projectProperties.getName()).isEqualTo("[ UNKNOWN ]");
        assertThat(projectProperties.getVersion()).isEqualTo("[ UNKNOWN ]");
    }

    @Test
    void initializeProjectProperties_throwsRuntimeException_doesNotCauseExceptionInitializer() {
        when(mockProperties.getName()).thenThrow(new RuntimeException("End of the world"));

//        final String actualMessage = BaseSdkException.buildMessage(ErrorSourceType.SERVICE, "unit test");
//        assertThat(actualMessage).contains("AUTONOMIC");
//        assertThat(actualMessage).contains("SDK");

        try {
            BaseSdkException.initializeProjectProperties(mockProperties);
        } catch (Throwable e) {
            fail("exceptions must not be thrown");
        }
        final String actualMessage2 = BaseSdkException.buildMessage(ErrorSourceType.SERVICE, "unit test");
        assertThat(actualMessage2).contains("AUTONOMIC");
        assertThat(actualMessage2).contains("SDK");
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
