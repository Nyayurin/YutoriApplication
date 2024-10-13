import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
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

    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            freeCompilerArgs.add("-P")
            freeCompilerArgs.add("plugin:androidx.compose.compiler.plugins.kotlin:stabilityConfigurationPath=${project.projectDir.absolutePath}/src/androidMain/compose_compiler_config.conf")
        }
    }

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
            implementation(libs.yutori)
            implementation(libs.yutorix.satori.core)
            implementation(libs.yutorix.satori.adapter)
            implementation(libs.multiplatform.settings)
            implementation(libs.multiplatform.settings.serialization)
            implementation(libs.compose.settings)
            implementation(libs.compose.settings.extended)
            implementation(libs.sketch.compose)
            implementation(libs.sketch.extensions.compose)
            implementation(libs.sketch.svg)
            implementation(libs.sketch.animated)
            implementation(libs.compose.placeholder)
            implementation(libs.compose.material3.adaptive)
            implementation(libs.compose.material3.adaptive.layout)
            implementation(libs.compose.material3.adaptive.navigation)
            implementation(libs.compose.material3.adaptive.navigation.suite)
        }

        androidMain.dependencies {
            implementation(compose.uiTooling)
            implementation(compose.preview)
            implementation(libs.startup.runtime)
            implementation(libs.activity.compose)
            implementation(libs.kotlinx.coroutines.android)
        }

        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
        }

        /*jsMain.dependencies {
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
    compileSdk = 35

    defaultConfig {
        minSdk = 26
        targetSdk = 35

        applicationId = "cn.yurn.yutori.application"
        versionCode = 1
        versionName = System.getenv("VERSION")
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
            val os = System.getProperty("os.name")
            when {
                os.contains("Windows") -> targetFormats(TargetFormat.Msi, TargetFormat.Exe, TargetFormat.AppImage)
                os.contains("Linux") -> targetFormats(TargetFormat.Deb, TargetFormat.Rpm, TargetFormat.AppImage)
                os.contains("Mac OS") -> targetFormats(TargetFormat.Dmg, TargetFormat.Pkg)
                else -> error("Unsupported OS: $os")
            }
            packageName = "Yutori Application"
            packageVersion = System.getenv("VERSION")
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