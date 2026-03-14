import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.buildConfig)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
            export(projects.adapty)
        }
    }

    sourceSets {
        commonMain.dependencies {
            api(projects.adapty)
            implementation(libs.kotlinx.coroutine)
        }
    }
}

android {
    namespace = "com.adapty.nativeuiexample.shared"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

buildConfig {
    packageName("com.adapty.nativeuiexample.shared")
    buildConfigField("ADAPTY_API_KEY", gradleLocalProperties(rootDir, providers).getProperty("ADAPTY_API_KEY"))
}
