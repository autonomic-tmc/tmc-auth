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
        url = uri("https://autonomic.bintray.com/au-tmc-oss")
    }
}

dependencies {
    implementation("com.autonomic.tmc:tmc-auth:2.0.0-alpha")
}

application {
    mainClassName = "com.autonomic.tmc.example.auth.GradleAuthExample"
}
