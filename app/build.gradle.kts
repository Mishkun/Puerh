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
}
