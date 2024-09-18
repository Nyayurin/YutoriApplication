import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose)
    alias(libs.plugins.android.application)
    alias(libs.plugins.serialization)
    alias(libs.plugins.sqldelight)
}

kotlin {
    jvmToolchain(17)

    androidTarget()

    jvm()

    /*js {
        browser()
        binaries.executable()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }*/

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.components.resources)
            implementation(libs.lifecycle.viewModel.compose)
            implementation(libs.navigation.compose)
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor)
            implementation(libs.yutori)
            implementation(libs.yutorix.satori.core)
            implementation(libs.yutorix.satori.adapter)
            implementation(libs.multiplatform.settings)
            implementation(libs.multiplatform.settings.serialization)
            implementation(libs.sqldelight.coroutines)
        }

        androidMain.dependencies {
            implementation(compose.uiTooling)
            implementation(compose.preview)
            implementation(libs.startup.runtime)
            implementation(libs.activity.compose)
            implementation(libs.kotlinx.coroutines.android)
            implementation(libs.coil.gif)
            implementation(libs.sqldelight.android)
        }

        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
            implementation(libs.sqldelight.jvm)
        }

        /*jsMain.dependencies {
            implementation(libs.sqldelight.js)
            implementation(devNpm("copy-webpack-plugin", "9.1.0"))
        }

        wasmJsMain.dependencies {
        }*/
    }
}

android {
    signingConfigs {
        create("release") {
            storeFile = file("YutoriApplication.jks")
            storePassword = System.getenv("SIGNING_STORE_PASSWORD")
            keyAlias = System.getenv("SIGNING_KEY_ALIAS")
            keyPassword = System.getenv("SIGNING_KEY_PASSWORD")
        }
    }
    namespace = "cn.yurn.yutori.application"
    compileSdk = 34

    defaultConfig {
        minSdk = 26
        targetSdk = 34

        applicationId = "cn.yurn.yutori.application"
        versionCode = 1
        versionName = "1.0.0"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.getByName("release")
        }
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb, TargetFormat.AppImage)
            packageName = "Yutori Application"
            packageVersion = "1.0.0"
            jvmArgs("-Dfile.encoding=UTF-8")
        }

        buildTypes.release.proguard {
            obfuscate = true
            configurationFiles.from(project.file("compose-desktop.pro"))
        }
    }
}

sqldelight {
    databases {
        create("Database") {
            packageName = "cn.yurn.yutori.application.database"
        }
    }
}