import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {

    ////////////////////////////////////
    // ⭐ MODERN HIERARCHY (FIXED)
    ////////////////////////////////////
    applyDefaultHierarchyTemplate()

    ////////////////////////
    // ANDROID
    ////////////////////////
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    ////////////////////////
    // iOS
    ////////////////////////
    iosArm64()
    iosSimulatorArm64()

    ////////////////////////
    // DESKTOP
    ////////////////////////
    jvm()

    sourceSets {

        ////////////////////////////////////
        // COMMON
        ////////////////////////////////////
        val commonMain by getting {
            dependencies {

                // Compose
                implementation(libs.compose.runtime)
                implementation(libs.compose.foundation)
                implementation(libs.compose.material3)
                implementation(libs.compose.ui)
                implementation(libs.compose.components.resources)
                implementation(libs.compose.uiToolingPreview)
                implementation(compose.materialIconsExtended)

                // Lifecycle
                implementation(libs.androidx.lifecycle.viewmodelCompose)
                implementation(libs.androidx.lifecycle.runtimeCompose)

                // Navigation
                implementation("cafe.adriel.voyager:voyager-navigator:1.1.0-beta03")

                // Ktor
                implementation("io.ktor:ktor-client-core:3.4.0")
                implementation("io.ktor:ktor-client-content-negotiation:3.4.0")
                implementation("io.ktor:ktor-serialization-kotlinx-json:3.4.0")

                // Serialization
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.10.0")

                // Settings
                implementation("com.russhwolf:multiplatform-settings:1.3.0")
                implementation("com.russhwolf:multiplatform-settings-no-arg:1.3.0")

                // Coroutines
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")

                // ⭐ DATETIME
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0")

            }
        }

        ////////////////////////////////////
        // ANDROID
        ////////////////////////////////////
        val androidMain by getting {
            dependencies {
                implementation(libs.androidx.activity.compose)
                implementation("io.ktor:ktor-client-okhttp:3.4.0")
                implementation("io.coil-kt:coil-compose:2.5.0")
                implementation("com.google.code.gson:gson:2.10.1")
            }
        }

        ////////////////////////////////////
        // DESKTOP
        ////////////////////////////////////
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(libs.kotlinx.coroutinesSwing)
                implementation("io.ktor:ktor-client-okhttp:3.4.0")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0")

            }
        }

        ////////////////////////////////////
        // iOS (auto-created)
        ////////////////////////////////////
        val iosMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-darwin:3.4.0")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0")

            }
        }

        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }
}

//////////////////////////////////
// ANDROID CONFIG
//////////////////////////////////
android {
    namespace = "com.example.paymyfine"

    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.example.paymyfine"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(libs.compose.uiTooling)
}

//////////////////////////////////
// DESKTOP
//////////////////////////////////
compose.desktop {
    application {
        mainClass = "com.example.paymyfine.MainKt"

        nativeDistributions {
            targetFormats(
                TargetFormat.Dmg,
                TargetFormat.Msi,
                TargetFormat.Deb
            )
            packageName = "com.example.paymyfine"
            packageVersion = "1.0.0"
        }
    }
}
