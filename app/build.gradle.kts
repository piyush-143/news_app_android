import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.android.application)
    // FIX: Explicitly define the Kotlin version to resolve "Plugin not found" error
    id("org.jetbrains.kotlin.android") version "1.8.22"
}

android {
    namespace = "com.example.newsapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.news_app"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    // FIX: Updated from VERSION_1_8 to VERSION_17 to support newer libraries
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

// FIX: Configure Kotlin JVM target for all Kotlin tasks
tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // Fix for "Duplicate class" errors: Align Kotlin versions
    implementation(platform(libs.kotlin.bom))
    implementation(libs.appcompat.v161)
    implementation(libs.material.v190)
    implementation(libs.constraintlayout.v214)

    // Networking
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // Image Loading
    implementation(libs.glide)

    // Circular Image View
    implementation(libs.circleimageview)
    // Dots Indicator
    implementation(libs.dotsindicator)

    // --- TESTING DEPENDENCIES (Added to fix your error) ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}