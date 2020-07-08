plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    prepare()
    defaultConfig {
        applicationId = "io.github.mishkun.puerh"
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("androidx.appcompat:appcompat:1.1.0")
}
