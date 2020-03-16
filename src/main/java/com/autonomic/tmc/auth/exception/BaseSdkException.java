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

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.Optional;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import lombok.extern.slf4j.Slf4j;

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
        private Manifest manifest;

        private Manifest getManifest() {
            if (Objects.isNull(manifest)) {
                try {
                    String jarPath = BaseSdkException.class.getProtectionDomain()
                        .getCodeSource().getLocation().toURI().getPath();
                    try (JarFile jarFile = new JarFile(jarPath)) {
                        manifest = jarFile.getManifest();
                    }
                } catch (IOException | URISyntaxException ignored) {}
            }
            return manifest;
        }

        private String getAttribute(String name) {
            return Optional.ofNullable(getManifest())
                .map(m -> m.getMainAttributes().getValue(name))
                .orElse(null);
        }

        String getName() {
            return Optional.ofNullable(getAttribute("Implementation-Title"))
                .orElse(UNKNOWN);
        }

        String getVersion() {
            return Optional.ofNullable(getAttribute("Implementation-Version"))
                .orElse(UNKNOWN);
        }
    }
}
