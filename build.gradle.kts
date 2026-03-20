import org.jetbrains.dokka.gradle.DokkaMultiModuleTask

plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.dokka) apply false
    alias(libs.plugins.vanniktech.mavenPublish) apply false
    alias(libs.plugins.kotlinSerialization) apply false
    alias(libs.plugins.buildConfig) apply false
    alias(libs.plugins.kotlinx.binary.validator)
}

apiValidation {
    @OptIn(kotlinx.validation.ExperimentalBCVApi::class)
    klib {
        enabled = true
    }
    ignoredProjects += listOf("composeApp", "shared", "androidApp")
}

allprojects {
    val excludedModules = listOf("composeApp", "example", "composeMultiplatformApp", "kmpNativeUI", "shared", "androidApp")
    if (name !in excludedModules) {
        apply(plugin = "org.jetbrains.dokka")
    }
}
