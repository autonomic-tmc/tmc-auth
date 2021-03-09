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

import static java.util.Optional.ofNullable;

import java.util.jar.JarFile;
import java.util.jar.Manifest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BaseSdkException extends RuntimeException {

    BaseSdkException(String message) {
        super(message);
        log.warn(message);
    }

    BaseSdkException(String message, Throwable cause) {
        super(message, cause);
        log.warn(message, cause);
    }

    static String buildMessage(ErrorSourceType errorSourceType, String clientMessage) {
        try {
            final ProjectProperties properties = ProjectProperties.get();
            final String name = ofNullable(properties.getName()).orElseGet(() -> "[ AUTONOMIC ]");
            final String version = ofNullable(properties.getVersion()).orElseGet(() -> "[ SDK ]");
            return String.format("%s-%s-%s: %s.", name, version, errorSourceType, clientMessage);
        } catch (Throwable e) {
            return "[ AUTONOMIC! ]-[ SDK! ]-" + errorSourceType.toString() + clientMessage;
        }
    }

    static class ProjectProperties {

        static ProjectProperties singletonInstance = null;

        Manifest manifest;

        ProjectProperties() {
            setManifest();
        }

        public static ProjectProperties get() {
            if (singletonInstance == null) {
                singletonInstance = new ProjectProperties();
            }
            return singletonInstance;
        }

        private void setManifest() {
            try {
                String jarPath = BaseSdkException.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI().getPath();
                try (JarFile jarFile = new JarFile(jarPath)) {
                    // This branch is intentionally not unit tested. This cannot be tested
                    // because the tests run before the library gets packaged. These lines have
                    // been tested in the examples which are distributed separately from this
                    // library.
                    manifest = jarFile.getManifest();
                }
            } catch (Throwable e) {
                log.debug("Unable to find manifest", e);
            }
        }

        private String getAttribute(String name, String defaultValue) {
            try {
                return ofNullable(manifest)
                    .map(m -> m.getMainAttributes().getValue(name))
                    .orElse(defaultValue);
            } catch (Throwable e) {
                return defaultValue;
            }
        }

        String getName() {
            return getAttribute("Implementation-Title", "[ AUTONOMIC ]");
        }

        String getVersion() {
            return getAttribute("Implementation-Version", "[ SDK ]");
        }
    }
}
