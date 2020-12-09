/**
 * By default this file is NOT used.
 * The build.gradle file takes precedence over this file.
 * This file is intended to be an example to follow if you are using the Kotlin (*.kts) DSL
 */

plugins {
    java
    application
}

repositories {
    maven {
        url = uri("https://dl.cloudsmith.io/public/autonomic/au-tmc-oss/maven/")
    }
}

dependencies {
    implementation("com.autonomic.tmc:tmc-auth:3.0.5-beta")
}

application {
    mainClassName = "com.autonomic.tmc.example.auth.GradleAuthExample"
}
