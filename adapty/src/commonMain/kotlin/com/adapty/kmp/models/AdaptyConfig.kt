package com.adapty.kmp.models

import KMPAdapty.adapty.BuildConfig

/** The main configuration object used to initialize the Adapty SDK.
 *
 * Use [AdaptyConfig.Builder] to create and customize an instance before passing it to [Adapty.activate].
 *
 * Example:
 * ```
 * val config = AdaptyConfig.Builder("YOUR_PUBLIC_SDK_KEY")
 *     .withCustomerUserId("user_123")
 *     .withObserverMode(false)
 *     .withActivateUI(true)
 *     .build()
 *
 * Adapty.activate(config)
 * ```
 *
 * @property apiKey Your Adapty Public SDK Key, found in the [Adapty Dashboard](https://app.adapty.io/)
 * under **App Settings → General**.
 * @property observerMode Enables observer mode when `true`. Use this if purchases are handled
 * outside Adapty (e.g., via your own billing logic). Default value is false
 * @property customerUserId Optional unique identifier for the current user in your system.
 * @property ipAddressCollectionDisabled Disables IP address collection if `true`. Default value is false.
 * @property googleAdvertisingIdCollection Enables Google Advertising ID collection when `true`. Default value is false.
 * @property appleIdfaCollectionDisabled Disables IDFA collection (iOS only) if `true`. Default value is false.
 * @property backendBaseUrl Custom backend base URL for debugging or proxying requests.
 * @property backendFallbackBaseUrl Fallback backend base URL.
 * @property backendConfigsBaseUrl Base URL for fetching remote configuration.
 * @property backendUABaseUrl Base URL for the User Acquisition service.
 * @property backendProxyHost Custom proxy host (for internal use).
 * @property backendProxyPort Custom proxy port (for internal use).
 * @property serverCluster Region of Adapty’s backend servers (default or EU).
 * @property crossPlatformSDKName Name of the SDK if used via another platform (e.g., Flutter, React Native).
 * @property crossPlatformSDKVersion Version of the wrapper SDK.
 * @property activateUI Enables AdaptyUI module (for displaying paywalls and onboarding).
 * @property logLevel The level of logging output. Defaults to [AdaptyLogLevel.INFO].
 * @property mediaCache Configuration for caching media assets used in AdaptyUI.
 */
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
     * A builder class for constructing [AdaptyConfig] instances.
     *
     * @property apiKey Your Adapty Public SDK Key, found in the [Adapty Dashboard](https://app.adapty.io/)
     * under **App Settings → General**.
     *
     * Example:
     * ```
     * val config = AdaptyConfig.Builder("YOUR_SDK_KEY")
     *     .withObserverMode(true)
     *     .withCustomerUserId("user_123")
     *     .build()
     * ```
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

        /** Unique identifier for the current user in your system */
        public fun withCustomerUserId(id: String?): Builder = apply { this.customerUserId = id }

        /** Enables observer mode if your app handles purchases manually. */
        public fun withObserverMode(enabled: Boolean): Builder =
            apply { this.observerMode = enabled }

        /** Disables/Enables IP address collection. Default value is false. */
        public fun withIpAddressCollectionDisabled(disabled: Boolean): Builder =
            apply { this.ipAddressCollectionDisabled = disabled }

        /** Disables/Enables Google Advertising ID collection. Default value is false. */
        public fun withGoogleAdvertisingIdCollectionDisabled(disabled: Boolean): Builder =
            apply { this.googleAdvertisingIdCollection = disabled }

        /** Disables/Enables IDFA collection (iOS only). Default value is false. */
        public fun withAppleIdfaCollectionDisabled(disabled: Boolean): Builder =
            apply { this.appleIdfaCollectionDisabled = disabled }

        /** Sets custom backend URLs (useful for testing or proxying). */
        public fun withBackendBaseUrl(url: String): Builder = apply { this.backendBaseUrl = url }

        /** Sets custom backend URLs (useful for testing or proxying). */
        public fun withBackendFallbackBaseUrl(url: String): Builder =
            apply { this.backendFallbackBaseUrl = url }

        /** Sets custom backend URLs (useful for testing or proxying). */
        public fun withBackendConfigsBaseUrl(url: String): Builder =
            apply { this.backendConfigsBaseUrl = url }

        /** Sets custom backend URLs (useful for testing or proxying). */
        public fun withBackendUABaseUrl(url: String): Builder =
            apply { this.backendUABaseUrl = url }

        /** Sets custom backend URLs (useful for testing or proxying). */
        public fun withBackendProxyHost(host: String): Builder =
            apply { this.backendProxyHost = host }

        /** Sets custom backend URLs (useful for testing or proxying). */
        public fun withBackendProxyPort(port: Int): Builder = apply { this.backendProxyPort = port }
        internal fun withCrossPlatformSDKName(name: String): Builder =
            apply { this.crossPlatformSDKName = name }

        internal fun withCrossPlatformSDKVersion(version: String): Builder =
            apply { this.crossPlatformSDKVersion = version }

        /** Sets the region of Adapty’s backend servers (default or EU). */
        public fun withServerCluster(cluster: ServerCluster): Builder = apply {
            this.serverCluster = when (cluster) {
                ServerCluster.EU -> "eu"
                else -> "default"
            }
        }

        /** Enables AdaptyUI integration (for paywalls, onboarding, etc). */
        public fun withActivateUI(enabled: Boolean): Builder = apply { this.activateUI = enabled }

        /** Sets the desired log level for Adapty logs. */
        public fun withLogLevel(level: AdaptyLogLevel): Builder = apply { this.logLevel = level }

        /** Configures media caching (for AdaptyUI assets). */
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

    /**
     * Defines the available Adapty backend server regions.
     */
    public enum class ServerCluster {
        /** Default region (US/global). */
        DEFAULT,

        /** European region (EU). */
        EU
    }

    /**
     * Configuration for Adapty’s in-memory and disk media caching system.
     *
     * Used primarily by AdaptyUI for caching paywall and onboarding images.
     *
     * @property memoryStorageTotalCostLimit Maximum memory cache size in bytes. Default: 100 MB.
     * @property memoryStorageCountLimit Maximum number of cached items in memory.
     * @property diskStorageSizeLimit Maximum disk cache size in bytes. Default: 100 MB.
     */
    public data class MediaCacheConfiguration(
        val memoryStorageTotalCostLimit: Int = 100 * 1024 * 1024, //100mb
        val memoryStorageCountLimit: Int = Int.MAX_VALUE,
        val diskStorageSizeLimit: Int = 100 * 1024 * 1024, //100mb
    )
}