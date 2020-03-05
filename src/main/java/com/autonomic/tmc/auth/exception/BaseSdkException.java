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
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

@Slf4j
public class BaseSdkException extends RuntimeException {

    private static String projectVersion;

    final ErrorSourceType errorSourceType;

    public BaseSdkException(ErrorSourceType errorSourceType, String message) {
        super(message);
        this.errorSourceType = errorSourceType;
    }

    public BaseSdkException(ErrorSourceType errorSourceType, String message, Throwable cause) {
        super(message, cause);
        this.errorSourceType = errorSourceType;
    }

    @Override
    public String getMessage() {
        String message = String
            .format("tmc-auth-%s-%s: %s", getProjectVersion(), errorSourceType, super.getMessage());
        //Todo: remove before prod
        log.info(message);
        return message;
    }

    private static String getProjectVersion() {
        if (Objects.isNull(projectVersion)) {
            projectVersion = loadProjectVersion("pom.xml");
        }
        return projectVersion;
    }

    public static String loadProjectVersion(String from) {
        try {
            MavenXpp3Reader reader = new MavenXpp3Reader();
            return reader.read(new FileReader(from)).getVersion();
        } catch (XmlPullParserException | IOException e) {
            log.warn("Failed to read " + from + " to retrieve library version", e);
            return "[ UNKNOWN ]";
        }
    }
}
