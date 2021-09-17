/*-
 * ‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾
 * TMC Auth SDK
 * ——————————————————————————————————————————————————————————————————————————————
 * Copyright (C) 2016 - 2021 Autonomic, LLC
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
package com.autonomic.tmc.auth;

import com.autonomic.tmc.environment.EnvironmentDetails;
import java.io.IOException;
import java.util.Properties;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class ProjectProperties {

    private static final Properties properties = new Properties();
    @Getter private static String artifactId = "tmc-auth";
    @Getter private static String version = "unknown";
    @Getter private static final EnvironmentDetails environmentDetails;

    static {
        try {
            properties.load(ProjectProperties.class.getClassLoader()
                .getResourceAsStream("project.properties"));
            artifactId = properties.getProperty("artifactId", "tmc-auth");
            version = properties.getProperty("version", "unknown");
        } catch (IOException e) {
            log.warn("Could not find project.properties file, version of SDK is unknown.", e);
        }
        environmentDetails = new EnvironmentDetails(artifactId, version);
    }

    private ProjectProperties() {
        // Hide constructor
    }
}
