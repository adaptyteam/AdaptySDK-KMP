package com.adapty.kmp.models

import com.adapty.models.AdaptyPaywall as AdaptyPaywallAndroid
import com.adapty.models.AdaptyPaywall.FetchPolicy as AdaptyPaywallFetchPolicyAndroid


internal fun AdaptyPaywallAndroid.asAdaptyPaywall(): AdaptyPaywall {
    return object : AdaptyPaywall {} //TODO fix this
}

internal fun AdaptyPaywall.asAdaptyPaywall(): AdaptyPaywallAndroid {
    return AdaptyPaywallAndroid // TODO fix this
}

internal fun AdaptyPaywall.FetchPolicy.asAdaptyPaywallFetchPolicy(): AdaptyPaywallFetchPolicyAndroid {

    return when (this) {
        is AdaptyPaywall.FetchPolicy.ReloadRevalidatingCacheData -> AdaptyPaywallFetchPolicyAndroid.ReloadRevalidatingCacheData
        is AdaptyPaywall.FetchPolicy.ReturnCacheDataElseLoad -> AdaptyPaywallFetchPolicyAndroid.ReturnCacheDataElseLoad
        is AdaptyPaywall.FetchPolicy.ReturnCacheDataIfNotExpiredElseLoad ->
            AdaptyPaywallFetchPolicyAndroid.ReturnCacheDataIfNotExpiredElseLoad(this.maxAgeMillis)
    }
}