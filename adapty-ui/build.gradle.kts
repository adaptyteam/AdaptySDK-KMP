@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.vanniktech.mavenPublish)
}

kotlin {
    explicitApi()
    androidTarget {
        publishLibraryVariants("release")
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    jvm()
    wasmJs {
        browser()
    }
    js(IR) {
        nodejs()
        browser()
        binaries.library()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(projects.adapty)
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.ui)
                implementation(compose.components.uiToolingPreview)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(project.dependencies.platform(libs.adapty.bom))
                implementation(libs.androidx.activity.compose)
                implementation(libs.adapty.android.sdk)
                implementation(libs.adapty.android.ui)
                implementation(libs.adapty.internal.crossplatform)
            }
        }
    }
}

android {
    namespace = "com.adapty.kmp.ui"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

mavenPublishing {
    publishToMavenCentral()

    if (!project.gradle.startParameter.taskNames.any { it.contains("publishToMavenLocal") }) {
        signAllPublications()
    }

    coordinates(
        groupId = "io.adapty",
        artifactId = "adapty-kmp-ui",
        version = project.properties["adaptyKmpVersion"] as String
    )

    pom {
        name = "Adapty Kotlin Multiplatform SDK"
        description = "Easy In-App Purchases KMP Integration to Make Your App Profitable"
        url = "https://github.com/adaptyteam/AdaptySDK-KMP"

        organization {
            name = "Adapty Tech Inc."
            url = "https://adapty.io"
        }
        licenses {
            license {
                name = "MIT License"
                url = "https://github.com/adaptyteam/AdaptySDK-KMP/blob/main/LICENSE"
            }
        }
        developers {
            developer {
                id = "adapty"
                name = "Adapty"
                email = "support@adapty.io"
            }
        }
        scm {
            url = "https://github.com/adaptyteam/AdaptySDK-KMP/tree/main"
            connection = "scm:git:github.com/adaptyteam/AdaptySDK-KMP.git"
            developerConnection = "scm:git:ssh://github.com/adaptyteam/AdaptySDK-KMP.git"
        }
    }
}
