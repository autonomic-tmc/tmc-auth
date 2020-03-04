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

//    static String projectVersion() {
//        Properties props = System.getProperties();
//        props.size();
//        if (!Objects.isNull(projectVersion)) {
//            return projectVersion;
//        }
//
//        MavenXpp3Reader reader = new MavenXpp3Reader();
//        try {
//            Model model = reader.read(new FileReader("pom.xml"));
//            projectVersion = model.getVersion();
//        } catch (Exception e) {
//            projectVersion = "UNKNOWN";
//        }
//        return projectVersion;
//        return "UNKNOWN";
//    }

//    @Override
//    public String toString() {
//        return "BaseSdkException{" +
//            "errorSourceType=" + errorSourceType +
//            '}';
//    }
}
