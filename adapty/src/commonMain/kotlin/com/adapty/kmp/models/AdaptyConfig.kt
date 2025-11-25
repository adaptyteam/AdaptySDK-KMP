package com.adapty.kmp.models

import KMPAdapty.adapty.BuildConfig

public class AdaptyConfig private constructor(
    internal val apiKey: String,
    internal val observerMode: Boolean,
    internal val customerUserId: String?,
    internal val ipAddressCollectionDisabled: Boolean,
    internal val googleAdvertisingIdCollection: Boolean,
    internal val googleEnablePendingPrepaidPlans: Boolean,
    internal val googleLocalAccessLevelAllowed: Boolean,
    internal val appleIdfaCollectionDisabled: Boolean,
    internal val backendProxyHost: String?,
    internal val backendProxyPort: Int?,
    internal val serverCluster: String?,
    internal val crossPlatformSDKName: String,
    internal val crossPlatformSDKVersion: String,
    internal val activateUI: Boolean,
    internal val logLevel: AdaptyLogLevel?,
    internal val mediaCache: MediaCacheConfiguration,
    internal val customerIdentity: AdaptyCustomerIdentity?
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
        private var googleEnablePendingPrepaidPlans = false
        private var googleLocalAccessLevelAllowed = false
        private var appleIdfaCollectionDisabled = false
        private var backendProxyHost: String? = null
        private var backendProxyPort: Int? = null
        private var serverCluster: String? = null
        private var crossPlatformSDKName: String = SDK_NAME
        private var crossPlatformSDKVersion: String = SDK_VERSION
        private var activateUI: Boolean = false
        private var logLevel: AdaptyLogLevel? = AdaptyLogLevel.INFO
        private var mediaCache: MediaCacheConfiguration = MediaCacheConfiguration()
        private var customerIdentity: AdaptyCustomerIdentity? = null


        public fun withCustomerUserId(
            id: String?,
            iosAppAccountToken: String? = null,
            androidObfuscatedAccountId: String? = null
        ): Builder = apply {
            this.customerUserId = id
            customerIdentity = AdaptyCustomerIdentity.createIfNotEmpty(
                iosAppAccountToken = iosAppAccountToken,
                androidObfuscatedAccountId = androidObfuscatedAccountId
            )
        }

        public fun withObserverMode(enabled: Boolean): Builder =
            apply { this.observerMode = enabled }

        public fun withIpAddressCollectionDisabled(disabled: Boolean): Builder =
            apply { this.ipAddressCollectionDisabled = disabled }

        public fun withGoogleAdvertisingIdCollectionDisabled(disabled: Boolean): Builder =
            apply { this.googleAdvertisingIdCollection = disabled }

        public fun withGoogleEnablePendingPrepaidPlans(enabled: Boolean): Builder =
            apply { this.googleEnablePendingPrepaidPlans = enabled }

        public fun withGoogleLocalAccessLevelAllowed(enabled: Boolean): Builder =
            apply { this.googleLocalAccessLevelAllowed = enabled }

        public fun withAppleIdfaCollectionDisabled(disabled: Boolean): Builder =
            apply { this.appleIdfaCollectionDisabled = disabled }

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
                googleEnablePendingPrepaidPlans = googleEnablePendingPrepaidPlans,
                googleLocalAccessLevelAllowed = googleLocalAccessLevelAllowed,
                ipAddressCollectionDisabled = ipAddressCollectionDisabled,
                backendProxyHost = backendProxyHost,
                backendProxyPort = backendProxyPort,
                serverCluster = serverCluster,
                crossPlatformSDKName = crossPlatformSDKName,
                crossPlatformSDKVersion = crossPlatformSDKVersion,
                activateUI = activateUI,
                logLevel = logLevel,
                mediaCache = mediaCache,
                customerIdentity = customerIdentity
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