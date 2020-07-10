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
    implementation(project(":puerh-core"))
    kotest()
}
