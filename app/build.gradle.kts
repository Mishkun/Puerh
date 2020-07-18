plugins {
    id("com.android.application")
    kotlin("android")
}

repositories {
    maven(url = "https://dl.bintray.com/inkremental/maven")
}

android {
    prepare()
    compileSdkVersion(30)
    defaultConfig {
        applicationId = "io.github.mishkun.puerh"
        minSdkVersion(21)
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(project(":puerh-core"))
    implementation(project(":puerh-jvm-executors"))
    implementation(kotlin("stdlib"))
    implementation("androidx.appcompat:appcompat:1.1.0")

    implementation("androidx.appcompat:appcompat:1.3.0-alpha01")
    implementation("androidx.activity:activity-ktx:1.1.0")
    implementation("androidx.core:core-ktx:1.5.0-alpha01")

    inkremental()
}
