package com.adapty.kmp.models

import KMPAdapty.adapty.BuildConfig

public class AdaptyConfig private constructor(
    internal val apiKey: String,
    internal val observerMode: Boolean,
    internal val customerUserId: String?,
    internal val ipAddressCollectionDisabled: Boolean,
    internal val googleAdvertisingIdCollection: Boolean,
    internal val appleIdfaCollectionDisabled: Boolean,
    internal val backendBaseUrl: String?,
    internal val backendFallbackBaseUrl: String?,
    internal val backendConfigsBaseUrl: String?,
    internal val backendUABaseUrl: String?,
    internal val backendProxyHost: String?,
    internal val backendProxyPort: Int?,
    internal val serverCluster: String?,
    internal val crossPlatformSDKName: String,
    internal val crossPlatformSDKVersion: String,
    internal val activateUI: Boolean,
    internal val logLevel: AdaptyLogLevel?,
    internal val mediaCache: MediaCacheConfiguration,
) {
    internal companion object {
        const val SDK_NAME = "kmp"
        const val SDK_VERSION = BuildConfig.ADAPTY_KMP_VERSION
    }

    /**
     * @property[apiKey] You can find it in your app settings
     * in [Adapty Dashboard](https://app.adapty.io/) _App settings_ > _General_.
     */
    public class Builder(private val apiKey: String) {

        private var customerUserId: String? = null
        private var observerMode = false
        private var ipAddressCollectionDisabled = false
        private var googleAdvertisingIdCollection = false
        private var appleIdfaCollectionDisabled = false
        private var backendBaseUrl: String? = null
        private var backendFallbackBaseUrl: String? = null
        private var backendConfigsBaseUrl: String? = null
        private var backendUABaseUrl: String? = null
        private var backendProxyHost: String? = null
        private var backendProxyPort: Int? = null
        private var serverCluster: String? = null
        private var crossPlatformSDKName: String = SDK_NAME
        private var crossPlatformSDKVersion: String = SDK_VERSION
        private var activateUI: Boolean = false
        private var logLevel: AdaptyLogLevel? = AdaptyLogLevel.INFO
        private var mediaCache: MediaCacheConfiguration = MediaCacheConfiguration()

        public fun withCustomerUserId(id: String?): Builder = apply { this.customerUserId = id }
        public fun withObserverMode(enabled: Boolean): Builder =
            apply { this.observerMode = enabled }

        public fun withIpAddressCollectionDisabled(disabled: Boolean): Builder =
            apply { this.ipAddressCollectionDisabled = disabled }

        public fun withGoogleAdvertisingIdCollectionDisabled(disabled: Boolean): Builder =
            apply { this.googleAdvertisingIdCollection = disabled }

        public fun withAppleIdfaCollectionDisabled(disabled: Boolean): Builder =
            apply { this.appleIdfaCollectionDisabled = disabled }

        public fun withBackendBaseUrl(url: String): Builder = apply { this.backendBaseUrl = url }
        public fun withBackendFallbackBaseUrl(url: String): Builder =
            apply { this.backendFallbackBaseUrl = url }

        public fun withBackendConfigsBaseUrl(url: String): Builder = apply { this.backendConfigsBaseUrl = url }
        public fun withBackendUABaseUrl(url: String): Builder = apply { this.backendUABaseUrl = url }

        public fun withBackendProxyHost(host: String): Builder =
            apply { this.backendProxyHost = host }

        public fun withBackendProxyPort(port: Int): Builder = apply { this.backendProxyPort = port }
        internal fun withCrossPlatformSDKName(name: String): Builder =
            apply { this.crossPlatformSDKName = name }

        internal fun withCrossPlatformSDKVersion(version: String): Builder =
            apply { this.crossPlatformSDKVersion = version }

        public fun withServerCluster(cluster: ServerCluster): Builder = apply {
            this.serverCluster = when (cluster) {
                ServerCluster.EU -> "eu"
                else -> "default"
            }
        }

        public fun withActivateUI(enabled: Boolean): Builder = apply { this.activateUI = enabled }
        public fun withLogLevel(level: AdaptyLogLevel): Builder = apply { this.logLevel = level }
        public fun withMediaCacheConfiguration(config: MediaCacheConfiguration): Builder =
            apply { this.mediaCache = config }


        public fun build(): AdaptyConfig {
            return AdaptyConfig(
                apiKey = apiKey,
                customerUserId = customerUserId,
                observerMode = observerMode,
                appleIdfaCollectionDisabled = appleIdfaCollectionDisabled,
                googleAdvertisingIdCollection = googleAdvertisingIdCollection,
                ipAddressCollectionDisabled = ipAddressCollectionDisabled,
                backendBaseUrl = backendBaseUrl,
                backendFallbackBaseUrl = backendFallbackBaseUrl,
                backendConfigsBaseUrl = backendConfigsBaseUrl,
                backendProxyHost = backendProxyHost,
                backendProxyPort = backendProxyPort,
                serverCluster = serverCluster,
                crossPlatformSDKName = crossPlatformSDKName,
                crossPlatformSDKVersion = crossPlatformSDKVersion,
                activateUI = activateUI,
                logLevel = logLevel,
                mediaCache = mediaCache,
                backendUABaseUrl = backendUABaseUrl
            )
        }
    }

    public enum class ServerCluster {
        DEFAULT,
        EU
    }

    public data class MediaCacheConfiguration(
        val memoryStorageTotalCostLimit: Int = 100 * 1024 * 1024, //100mb
        val memoryStorageCountLimit: Int = Int.MAX_VALUE,
        val diskStorageSizeLimit: Int = 100 * 1024 * 1024, //100mb
    )
}