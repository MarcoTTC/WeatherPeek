import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.ksp)
}

// The OpenWeather One Call app id is read from the (git ignored) local.properties file:
//     oneCallAppId=YOUR_APP_ID
// A missing value does not fail the build: the app is built with an empty id and tells the user to
// set one up at runtime (see AppKeyUtil and R.string.no_valid_app_id).
val localProperties = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        localPropertiesFile.reader(Charsets.UTF_8).use { reader -> load(reader) }
    }
}
val oneCallAppId = localProperties.getProperty("oneCallAppId").orEmpty().trim()
if (oneCallAppId.isEmpty()) {
    logger.warn(
        "WARNING: no OpenWeather app id found. Add 'oneCallAppId=YOUR_APP_ID' to " +
            "local.properties and rebuild, or the app will not be able to retrieve weather data."
    )
}
// Java string literal for BuildConfig, with backslashes and quotes escaped so any value compiles
val oneCallAppIdLiteral =
    "\"" + oneCallAppId.replace("\\", "\\\\").replace("\"", "\\\"") + "\""

// Kotlin compiles with AGP's built-in Kotlin support, whose jvmTarget follows
// compileOptions.targetCompatibility (17), so no kotlin { compilerOptions } block is needed.

android {
    namespace = "br.com.marcottc.weatherpeek"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "br.com.marcottc.weatherpeek"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 4
        versionName = "1.3"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments["clearPackageData"] = "true"

        buildConfigField("String", "ONE_CALL_APP_ID", oneCallAppIdLiteral)
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    sourceSets {
        // Exported Room schemas, read by MigrationTestHelper in the instrumentation tests
        getByName("androidTest").assets.srcDir("$projectDir/schemas")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    // This configurations is necessary to run the instrumented tests on an emulated x86_64 device.
    // It used to be written inside testOptions { }, but testOptions has no packaging block of its
    // own: Groovy resolved it to this android-level packaging all along, so this is the same setting.
    packaging {
        jniLibs {
            useLegacyPackaging = true
        }
    }

    // This configurations is necessary to use Mockk in instrumentation tests
    packaging {
        resources.excludes.add("META-INF/LICENSE.md")
        resources.excludes.add("META-INF/LICENSE-notice.md")
    }
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    // Kotlin
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.core.ktx)

    // The app gets kotlinx-serialization-core through AndroidX lifecycle, while room-testing (used
    // by MigrationTestHelper) needs a newer kotlinx-serialization-json. Instrumentation tests are
    // forced onto the app's versions, so the mismatched pair failed at runtime with an
    // AbstractMethodError. The BOM keeps every kotlinx-serialization artifact on one version.
    implementation(platform(libs.kotlinx.serialization.bom))

    // AndroidX
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.androidx.test.espresso.idling.resource)

    // AndroidX Lifecycle
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)

    // Room
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)

    // Kotlin + coroutines
    implementation(libs.androidx.work.runtime.ktx)

    // Material
    implementation(libs.material)

    // Retrofit2
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)

    // Koin
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.workmanager)

    // Glide (no annotation processor: the app has no AppGlideModule for it to generate code from)
    implementation(libs.glide)

    // Unit and Instrumented Test libraries

    // Mockk
    testImplementation(libs.mockk)
    androidTestImplementation(libs.mockk.android)

    // KotlinX Coroutines Test
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.kotlinx.coroutines.test)

    // JUnit
    testImplementation(libs.junit)

    // Jetpack
    testImplementation(libs.androidx.arch.core.testing)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.work.testing)
    androidTestImplementation(libs.androidx.room.testing)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.androidx.test.espresso.intents)
    androidTestImplementation(libs.androidx.test.espresso.contrib)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.rules)
}
