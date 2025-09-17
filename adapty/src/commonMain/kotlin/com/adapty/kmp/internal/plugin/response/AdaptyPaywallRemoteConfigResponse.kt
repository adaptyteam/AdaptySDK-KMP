package com.adapty.kmp.internal.plugin.response

import com.adapty.kmp.models.AdaptyPaywallRemoteConfig
import com.adapty.kmp.internal.utils.convertJsonToMapOfAny
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptyPaywallRemoteConfigResponse(
    @SerialName("lang") val locale: String,
    @SerialName("data") val jsonString: String,
)

internal fun AdaptyPaywallRemoteConfigResponse.asAdaptyPaywallRemoteConfig(): AdaptyPaywallRemoteConfig {
    return AdaptyPaywallRemoteConfig(
        locale = locale,
        dataJsonString = jsonString,
        dataMap = jsonString.convertJsonToMapOfAny()
    )
}

internal fun AdaptyPaywallRemoteConfig.asAdaptyPaywallRemoteConfigResponse(): AdaptyPaywallRemoteConfigResponse {
    return AdaptyPaywallRemoteConfigResponse(
        locale = locale,
        jsonString = dataJsonString
    )
}
