import org.jetbrains.dokka.gradle.DokkaMultiModuleTask

plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
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
    ignoredProjects += "composeApp"
}

allprojects {
    val excludedModules = listOf("composeApp", "example")
    if (name !in excludedModules) {
        apply(plugin = "org.jetbrains.dokka")

        tasks.withType<org.jetbrains.dokka.gradle.DokkaTaskPartial>().configureEach {
            dokkaSourceSets.configureEach {
                perPackageOption {
                    matchingRegex.set(".*\\.internal.*")
                    suppress.set(true)
                }
            }
        }
    }
}

tasks.withType<org.jetbrains.dokka.gradle.DokkaTask>().configureEach {
    notCompatibleWithConfigurationCache("Dokka tasks are not compatible with configuration cache")
}

tasks.withType<org.jetbrains.dokka.gradle.DokkaTaskPartial>().configureEach {
    notCompatibleWithConfigurationCache("Dokka tasks are not compatible with configuration cache")
}

tasks.withType<org.jetbrains.dokka.gradle.DokkaMultiModuleTask>().configureEach {
    notCompatibleWithConfigurationCache("Dokka tasks are not compatible with configuration cache")
}
