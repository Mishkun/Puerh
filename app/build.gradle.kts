plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("plugin.serialization") version "1.3.70"
    id("com.github.b3er.local.properties") version "1.1"
}

repositories {
    maven(url = "https://dl.bintray.com/inkremental/maven")
}

android {
    prepare()
    compileSdkVersion(30)
    defaultConfig {
        applicationId = "io.github.mishkun.puerh.sampleapp"
        minSdkVersion(21)
    }
    buildTypes.forEach { type ->
        type.buildConfigField("String", "API_KEY", project.properties["api_key"].toString())
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

    implementation("com.github.kittinunf.fuel:fuel:2.2.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.20.0")

    inkremental()
}
