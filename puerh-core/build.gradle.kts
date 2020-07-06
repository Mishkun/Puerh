plugins {
    id("com.android.library")
    kotlin("android")
}

android.prepare()

dependencies {
    implementation(kotlin("stdlib"))
}
