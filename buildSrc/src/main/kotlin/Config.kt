import com.android.build.gradle.BaseExtension
import com.android.build.gradle.internal.dsl.TestOptions
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.delegateClosureOf
import org.gradle.kotlin.dsl.get

fun BaseExtension.prepare() {
    compileSdkVersion(28)

    defaultConfig {
        minSdkVersion(15)
        targetSdkVersion(28)
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    testOptions {
        unitTests.delegateClosureOf<TestOptions.UnitTestOptions> {
            isIncludeAndroidResources = true
        }
    }

    sourceSets["main"].java.srcDir("src/main/kotlin")
}

fun DependencyHandlerScope.kotest() {
    testImplementation("io.kotest:kotest-runner-junit5-jvm:4.1.1")
    testImplementation("io.kotest:kotest-assertions-core-jvm:4.1.1")
    testImplementation("io.kotest:kotest-property-jvm:4.1.1")
    testImplementation("io.kotest:kotest-runner-console-jvm:4.1.1")
}

fun DependencyHandlerScope.inkremental() {
    val latestRelease = "0.12"
    implementation("dev.inkremental:anvil-sdk:21-$latestRelease")
    implementation("dev.inkremental:anvil-sdk:21-$latestRelease")
    implementation("dev.inkremental:anvil-support-v4:1.1.0-$latestRelease")
    implementation("dev.inkremental:anvil-appcompat-v7:1.1.0-$latestRelease")
    implementation("dev.inkremental:anvil-recyclerview-v7:1.1.0-$latestRelease")
    implementation("dev.inkremental:anvil-design:1.1.0-$latestRelease")
    implementation("com.google.android.material:material:1.1.0")
}

fun DependencyHandlerScope.testImplementation(dependency: String) {
    add("testImplementation", dependency)
}

fun DependencyHandlerScope.implementation(dependency: String) {
    add("implementation", dependency)
}
