package com.adapty.kmp.models

/**
 * Defines policies for fetching paywalls from the Adapty backend.
 *
 * This allows you to control how the SDK retrieves paywall data,
 * either always reloading from the network, using cached data, or a combination.
 */
public sealed interface AdaptyPaywallFetchPolicy {

    /** Always reload from the backend while revalidating cached data. */
    public data object ReloadRevalidatingCacheData : AdaptyPaywallFetchPolicy

    /** Return cached data if available, otherwise load from the backend. */
    public data object ReturnCacheDataElseLoad : AdaptyPaywallFetchPolicy

    /**
     * Return cached data if it is not older than [maxAgeMillis], otherwise load from the backend.
     *
     * @param maxAgeMillis Maximum age in milliseconds for cached data to be considered valid.
     */
    public data class ReturnCacheDataIfNotExpiredElseLoad(public val maxAgeMillis: Long) :
        AdaptyPaywallFetchPolicy


    public companion object {

        /** Default fetch policy: always reload and revalidate cached data. */
        public val Default: AdaptyPaywallFetchPolicy = ReloadRevalidatingCacheData
    }
}
