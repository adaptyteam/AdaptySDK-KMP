package com.adapty.kmp.models

public class AdaptyConfig private constructor(
    internal val apiKey: String,
    internal val observerMode: Boolean,
    internal val customerUserId: String?,
    internal val ipAddressCollectionDisabled: Boolean,
    internal val adIdCollectionDisabled: Boolean,
    internal val backendBaseUrl: String,
) {

    /**
     * @property[apiKey] You can find it in your app settings
     * in [Adapty Dashboard](https://app.adapty.io/) _App settings_ > _General_.
     */
    public class Builder(private val apiKey: String) {

        private var customerUserId: String? = null

        private var observerMode = false

        private var ipAddressCollectionDisabled = false

        private var adIdCollectionDisabled = false

        private var backendBaseUrl = ServerCluster.DEFAULT.url

        /**
         * @param[customerUserId] User identifier in your system.
         *
         * Default value is `null`.
         */
        public fun withCustomerUserId(customerUserId: String?): Builder = apply {
            this.customerUserId = customerUserId
        }

        /**
         * @param[observerMode] A boolean value controlling [Observer mode](https://adapty.io/docs/observer-vs-full-mode).
         * Turn it on if you handle purchases and subscription status yourself and use Adapty for sending
         * subscription events and analytics.
         *
         * Default value is `false`.
         */
        public fun withObserverMode(observerMode: Boolean): Builder = apply {
            this.observerMode = observerMode
        }

        public fun withIpAddressCollectionDisabled(disabled: Boolean): Builder = apply {
            this.ipAddressCollectionDisabled = disabled
        }

        public fun withAdIdCollectionDisabled(disabled: Boolean): Builder = apply {
            this.adIdCollectionDisabled = disabled
        }

        public fun withServerCluster(serverCluster: ServerCluster): Builder = apply {
            this.backendBaseUrl = serverCluster.url
        }

        public fun build(): AdaptyConfig {
            return AdaptyConfig(
                apiKey,
                observerMode,
                customerUserId,
                ipAddressCollectionDisabled,
                adIdCollectionDisabled,
                backendBaseUrl,
            )
        }
    }

    public enum class ServerCluster(internal val url: String) {
        DEFAULT("https://api.adapty.io/api/v1"),
        EU("https://api-eu.adapty.io/api/v1"),
    }
}