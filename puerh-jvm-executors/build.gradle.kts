import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    id("com.android.library")
    kotlin("android")
}

repositories {
    mavenCentral()
}

android.prepare()

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events(*TestLogEvent.values())
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":puerh-core"))
    kotest()
    testImplementation("io.kotest:kotest-extensions-robolectric-jvm:4.0.1")
}
