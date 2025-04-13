package com.adapty.kmp.models

public sealed interface AdaptyPaywallFetchPolicy {

    public data object ReloadRevalidatingCacheData : AdaptyPaywallFetchPolicy
    public data object ReturnCacheDataElseLoad : AdaptyPaywallFetchPolicy
    public data class ReturnCacheDataIfNotExpiredElseLoad(public val maxAgeMillis: Long) :
        AdaptyPaywallFetchPolicy


    public companion object {
        public val Default: AdaptyPaywallFetchPolicy = ReloadRevalidatingCacheData
    }
}
