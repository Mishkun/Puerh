plugins {
    id("com.android.library")
    kotlin("android")
}

android.prepare()

tasks.withType<Test> {
    useJUnitPlatform()
}

dependencies {
    implementation(kotlin("stdlib"))
    kotest()
}

