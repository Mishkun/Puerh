import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    id("com.android.library")
    kotlin("android")
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
    kotest()
}

