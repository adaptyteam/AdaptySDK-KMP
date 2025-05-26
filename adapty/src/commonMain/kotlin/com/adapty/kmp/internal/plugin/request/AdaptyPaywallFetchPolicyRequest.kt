package com.adapty.kmp.internal.plugin.request

import com.adapty.kmp.models.AdaptyPaywallFetchPolicy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal sealed class AdaptyPaywallFetchPolicyRequest {
    @Serializable
    @SerialName("reload_revalidating_cache_data")
    data object ReloadRevalidatingCacheData : AdaptyPaywallFetchPolicyRequest()

    @Serializable
    @SerialName("return_cache_data_else_load")
    data object ReturnCacheDataElseLoad : AdaptyPaywallFetchPolicyRequest()

    @Serializable
    @SerialName("return_cache_data_if_not_expired_else_load")
    data class ReturnCacheDataIfNotExpiredElseLoad(
        @SerialName("max_age") val maxAgeInSeconds: Long // seconds
    ) : AdaptyPaywallFetchPolicyRequest()
}

internal fun AdaptyPaywallFetchPolicy.asAdaptyPaywallFetchPolicyRequest(): AdaptyPaywallFetchPolicyRequest {
    return when (this) {
        AdaptyPaywallFetchPolicy.ReloadRevalidatingCacheData -> AdaptyPaywallFetchPolicyRequest.ReloadRevalidatingCacheData
        AdaptyPaywallFetchPolicy.ReturnCacheDataElseLoad -> AdaptyPaywallFetchPolicyRequest.ReturnCacheDataElseLoad
        is AdaptyPaywallFetchPolicy.ReturnCacheDataIfNotExpiredElseLoad -> AdaptyPaywallFetchPolicyRequest.ReturnCacheDataIfNotExpiredElseLoad(
            maxAgeInSeconds = maxAgeMillis / 1000L
        )
    }
}