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

import static com.autonomic.tmc.auth.exception.ErrorSourceType.SDK_CLIENT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.autonomic.tmc.auth.exception.BaseSdkException.ProjectProperties;
import org.apache.maven.model.Model;
import org.junit.jupiter.api.Test;

public class BaseSdkExceptionTest {

    @Test
    void givenApom_whenExceptionIsRaised_thenMessageDoesNotContainUnknown() {

        String actualMessage = "A test exception message";
        ProjectProperties.pom = "pom.xml";

        assertThatThrownBy(() -> throwNewSDKClientException(actualMessage))
            .hasMessageContaining(actualMessage)
            .hasMessageContaining("tmc-auth")
            .hasMessageContaining(SDK_CLIENT.toString());
    }

    @Test
    void projectProperties_returnsUnknown_when_POMFileNotFound() {
        ProjectProperties projectProperties = new ProjectProperties();

        // pom not found
        projectProperties.setProperties(new Model());

        assertThat(projectProperties.getName()).isEqualTo("[ UNKNOWN ]");
        assertThat(projectProperties.getVersion()).isEqualTo("[ UNKNOWN ]");
    }

    private void throwNewSDKClientException(String actualMessage) {
        throw new SdkClientException(actualMessage, new RuntimeException());
    }
}
