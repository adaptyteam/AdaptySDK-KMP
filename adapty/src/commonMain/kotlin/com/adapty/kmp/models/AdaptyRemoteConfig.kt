package com.adapty.kmp.models

/**
 * Represents a remote configuration for a paywall or onboarding, fetched from Adapty.
 *
 * @property locale The locale used for this configuration (e.g., "en-US").
 * @property dataJsonString The raw JSON string of the configuration.
 * @property dataMap A parsed representation of the configuration as a [Map] of keys to values.
 */
public data class AdaptyRemoteConfig(
    public val locale: String,
    public val dataJsonString: String,
    public val dataMap: Map<String, Any>,
) {

    override fun toString(): String {
        return "AdaptyPaywallRemoteConfig(locale=$locale, dataMap=${dataMap})"
    }
}