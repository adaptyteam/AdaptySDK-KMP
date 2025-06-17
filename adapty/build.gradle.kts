import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import com.android.build.gradle.internal.tasks.factory.dependsOn
import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.konan.target.KonanTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.dokka)
    alias(libs.plugins.buildConfig)
    alias(libs.plugins.vanniktech.mavenPublish)
}

kotlin {
    explicitApi()
    androidTarget {
        publishAllLibraryVariants()
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()

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
        val xcodeBuildTaskName = "build${platform.capitalize()}"

        iosTarget.compilations {
            val main by getting {
                cinterops.create("AdaptySwiftBridge") {
                    defFile("src/nativeInterop/cinterop/AdaptySwiftBridge.def")
                    val interopTask = tasks[interopProcessingTaskName]
                    interopTask.dependsOn(xcodeBuildTaskName)
                    val archBuildDir = "$derivedDataBuildDir/Release-$platform/includes"
                    includeDirs.headerFilterOnly(archBuildDir)
                }
                compileTaskProvider.dependsOn(xcodeBuildTaskName)
            }

        }
    }


    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlinx.serialization)
                implementation(libs.kotlinx.coroutine)
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
                implementation(libs.androidx.startup.runtime)
                implementation(project.dependencies.platform(libs.adapty.bom))
                implementation(libs.adapty.android.sdk)
                implementation(libs.adapty.android.ui)
                implementation(libs.adapty.internal.crossplatform)
            }
        }
    }
}

listOf("iphoneos", "iphonesimulator").forEach { sdk ->
    tasks.create<Exec>("build${sdk.capitalize()}") {
        group = "build"

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
        outputs.upToDateWhen { false }
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
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    if (!project.gradle.startParameter.taskNames.any { it.contains("publishToMavenLocal") }) {
        signAllPublications()
    }

    coordinates(
        groupId = "com.adapty",
        artifactId = "adapty-kmp",
        version = project.properties["adaptyKmpVersion"] as String
    )

    pom {
        name = "My library"
        description = "A library."
        inceptionYear = "2024"
        url = "https://github.com/kotlin/multiplatform-library-template/"
        licenses {
            license {
                name = "XXX"
                url = "YYY"
                distribution = "ZZZ"
            }
        }
        developers {
            developer {
                id = "XXX"
                name = "YYY"
                url = "ZZZ"
            }
        }
        scm {
            url = "XXX"
            connection = "YYY"
            developerConnection = "ZZZ"
        }
    }
}
