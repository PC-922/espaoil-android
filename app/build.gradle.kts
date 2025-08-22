plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

import java.util.Properties

android {
    namespace = "com.pc922.espaoilandroid"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.pc922.espaoilandroid"
        minSdk = 33
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // Expose optional BASE_API_URL to BuildConfig.
    // Priority: root gradle.properties.local (if present) -> project property BASE_API_URL -> empty string
    val localPropsFile = rootProject.file("gradle.properties.local")
    val localProps = Properties()
    if (localPropsFile.exists()) {
        localProps.load(localPropsFile.inputStream())
    }
    val baseApiUrl: String? = when {
        localProps.getProperty("BASE_API_URL") != null -> localProps.getProperty("BASE_API_URL")
        project.hasProperty("BASE_API_URL") -> project.findProperty("BASE_API_URL") as String
        else -> null
    }
    val networkTimeoutMsProp: String? = when {
        localProps.getProperty("NETWORK_TIMEOUT_MS") != null -> localProps.getProperty("NETWORK_TIMEOUT_MS")
        project.hasProperty("NETWORK_TIMEOUT_MS") -> project.findProperty("NETWORK_TIMEOUT_MS") as String
        else -> null
    }
    val networkTimeoutMs = networkTimeoutMsProp?.toLongOrNull() ?: 10000L
    buildTypes.all {
        buildConfigField("String", "BASE_API_URL", "\"${baseApiUrl ?: ""}\"")
        buildConfigField("long", "NETWORK_TIMEOUT_MS", "${networkTimeoutMs}L")
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
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
    compose = true
    buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.animation)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.compose.ui.tooling.preview)

    // Networking
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}