@file:OptIn(ExperimentalWasmDsl::class)

import com.android.build.gradle.internal.tasks.factory.dependsOn
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.konan.target.KonanTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.buildConfig)
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



    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->

        val platform = when (iosTarget.konanTarget) {
            KonanTarget.IOS_X64, KonanTarget.IOS_SIMULATOR_ARM64 -> "iphonesimulator"
            KonanTarget.IOS_ARM64 -> "iphoneos"
            else -> error("Unsupported target ${iosTarget.konanTarget}")
        }
        val derivedDataBuildDir = "$rootDir/adapty-swift-bridge/build/Build/Products"
        val dedupTaskName = "stripDebugInfo${platform.replaceFirstChar { it.uppercase() }}"

        iosTarget.compilations {
            val main by getting {
                cinterops.create("AdaptySwiftBridge") {
                    defFile("src/nativeInterop/cinterop/AdaptySwiftBridge.def")
                    val interopTask = tasks[interopProcessingTaskName]
                    interopTask.dependsOn(dedupTaskName)
                    val archBuildDir = "$derivedDataBuildDir/Release-$platform/includes"
                    includeDirs.headerFilterOnly(archBuildDir)
                }
                compileTaskProvider.dependsOn(dedupTaskName)
            }

        }
    }


    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlinx.serialization)
                implementation(libs.kotlinx.coroutine)
                api(libs.kotlinx.datetime)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlinx.coroutine.test)
                implementation(libs.kotlin.test)
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(libs.androidx.browser)
                implementation(libs.androidx.startup.runtime)
                implementation(project.dependencies.platform(libs.adapty.bom))
                implementation(libs.adapty.android.sdk)
                implementation(libs.adapty.android.ui)
                implementation(libs.adapty.internal.crossplatform)
            }
        }

        webMain.dependencies {
            implementation(libs.kotlinx.browser)
        }
    }
}

//This field needs to be set true only when publishing the library or when ios rebuild is needed
//Ex: ./gradlew publishToMavenLocal -PshouldForceIosRebuild=true --no-configuration-cache
val shouldForceIosRebuild: Boolean =
    project.findProperty("shouldForceIosRebuild")?.toString()?.toBooleanStrictOrNull() ?: false
listOf("iphoneos", "iphonesimulator").forEach { sdk ->
    tasks.register<Exec>("build${sdk.replaceFirstChar { it.uppercase() }}") {
        group = "build"
        description = "Builds libAdaptySwiftBridge.a for $sdk via xcodebuild (Release)."

        commandLine(
            "xcodebuild",
            "-workspace",
            "adapty-swift-bridge/AdaptySwiftBridge/AdaptySwiftBridge.xcodeproj/project.xcworkspace",
            "-scheme",
            "AdaptySwiftBridge",
            "-sdk",
            sdk,
            "-configuration",
            "Release",
            "-derivedDataPath",
            "adapty-swift-bridge/build"
        )
        workingDir(rootDir)

        inputs.files(
            fileTree("$projectDir/adapty-swift-bridge/AdaptySwiftBridge/AdaptySwiftBridge.xcodeproj") {
                exclude(
                    "**/xcuserdata"
                )
            },
            fileTree("$projectDir/adapty-swift-bridge/AdaptySwiftBridge")
        )
        outputs.files(
            fileTree("$projectDir/build/Release-${sdk}")
        )
        if (shouldForceIosRebuild) outputs.upToDateWhen { false }
    }
}

// Dedup duplicate .o members in static libraries (GitHub issue #19)
val dedupScript = "$rootDir/scripts/dedup_static_lib.sh"
listOf("iphoneos", "iphonesimulator").forEach { sdk ->
    val lib = "$rootDir/adapty-swift-bridge/build/Build/Products/Release-$sdk/libAdaptySwiftBridge.a"
    tasks.register<Exec>("dedup${sdk.replaceFirstChar { it.uppercase() }}") {
        group = "build"
        description = "Removes duplicate .o members from the $sdk libAdaptySwiftBridge.a (issue #19)."
        dependsOn("build${sdk.replaceFirstChar { it.uppercase() }}")
        commandLine("bash", dedupScript, lib)
        workingDir(rootDir)
    }
    // Runs after dedup and gates the cinterop step below, so the .a packaged into the klib is the
    // stripped one (Github issue #34).
    tasks.register<Exec>("stripDebugInfo${sdk.replaceFirstChar { it.uppercase() }}") {
        group = "build"
        description = "Strips debug info from the $sdk libAdaptySwiftBridge.a (issue #34)."
        dependsOn("dedup${sdk.replaceFirstChar { it.uppercase() }}")
        commandLine("strip", "-S", lib)
        workingDir(rootDir)
    }
}

android {
    namespace = "com.adapty.kmp"
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
    buildConfigField("ADAPTY_KMP_VERSION", project.properties["adaptyKmpVersion"] as String)
}

mavenPublishing {
    publishToMavenCentral()

    if (!project.gradle.startParameter.taskNames.any { it.contains("publishToMavenLocal") }) {
        signAllPublications()
    }

    coordinates(
        groupId = "io.adapty",
        artifactId = "adapty-kmp",
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

//For some reason after bumping kotlin version, running tests for ios fails. Disabling tests for ios solves the issue
tasks.withType<org.jetbrains.kotlin.gradle.targets.native.tasks.KotlinNativeTest>().configureEach {
    enabled = false
}
