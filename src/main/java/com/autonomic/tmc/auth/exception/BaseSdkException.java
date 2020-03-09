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

import java.io.FileReader;
import java.io.IOException;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

@Slf4j
public class BaseSdkException extends RuntimeException {

    private static final String PROJECT_NAME;
    private static final String PROJECT_VERSION;

    static {
        ProjectProperties projectProperties = new ProjectProperties();
        PROJECT_NAME = projectProperties.getName();
        PROJECT_VERSION = projectProperties.getVersion();
    }

    BaseSdkException(String message) {
        super(message);
    }

    BaseSdkException(String message, Throwable cause) {
        super(message, cause);
    }

    static String buildMessage(ErrorSourceType errorSourceType, String clientMessage) {
        return String
            .format("%s-%s-%s: %s.", PROJECT_NAME, PROJECT_VERSION, errorSourceType, clientMessage);
    }

    static class ProjectProperties {

        private static final String UNKNOWN = "[ UNKNOWN ]";
        static String pom = "pom.xml";

        private Model properties;

        public ProjectProperties() {
            try {
                MavenXpp3Reader reader = new MavenXpp3Reader();
                properties = reader.read(new FileReader(pom));
            } catch (IOException | XmlPullParserException e) {
                log.warn("Failed to read pom.xml while building BaseSdkException", e);
                properties = new Model();
            }
        }

        String getName() {
            return Optional.ofNullable(properties.getArtifactId()).orElse(UNKNOWN);
        }

        String getVersion() {
            return Optional.ofNullable(properties.getVersion()).orElse(UNKNOWN);
        }

        void setProperties(Model properties) {
            this.properties = properties;
        }
    }
}
