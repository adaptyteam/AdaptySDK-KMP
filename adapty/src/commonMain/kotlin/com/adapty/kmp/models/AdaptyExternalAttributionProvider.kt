package com.adapty.kmp.models

/**
 * An external attribution provider recognized by the Adapty backend.
 *
 * The predefined values below are a snapshot of the providers available when this SDK version
 * was released. If the backend adds another provider, pass its identifier straight to the
 * constructor without waiting for an SDK update — unknown ids arrive unchanged rather than
 * collapsing into an "unknown" constant:
 *
 * ```
 * Adapty.updateExternalAttribution(
 *     attribution = mapOf("status" to "organic"),
 *     provider = AdaptyExternalAttributionProvider.APPSFLYER,
 * )
 * ```
 *
 * @property value the raw provider identifier as sent over the wire.
 */
public data class AdaptyExternalAttributionProvider(public val value: String) {

    public companion object {
        public val APPLE_ADS: AdaptyExternalAttributionProvider =
            AdaptyExternalAttributionProvider("apple_search_ads")
        public val ADJUST: AdaptyExternalAttributionProvider =
            AdaptyExternalAttributionProvider("adjust")
        public val APPSFLYER: AdaptyExternalAttributionProvider =
            AdaptyExternalAttributionProvider("appsflyer")
        public val BRANCH: AdaptyExternalAttributionProvider =
            AdaptyExternalAttributionProvider("branch")
        public val TENJIN: AdaptyExternalAttributionProvider =
            AdaptyExternalAttributionProvider("tenjin")
        public val CUSTOM: AdaptyExternalAttributionProvider =
            AdaptyExternalAttributionProvider("custom")
    }
}
