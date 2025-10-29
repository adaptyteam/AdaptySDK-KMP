package com.adapty.kmp.internal.plugin.response

import com.adapty.kmp.models.AdaptyRemoteConfig
import com.adapty.kmp.internal.utils.convertJsonToMapOfAny
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptyRemoteConfigResponse(
    @SerialName("lang") val locale: String,
    @SerialName("data") val jsonString: String,
)

internal fun AdaptyRemoteConfigResponse.asAdaptyPaywallRemoteConfig(): AdaptyRemoteConfig {
    return AdaptyRemoteConfig(
        locale = locale,
        dataJsonString = jsonString,
        dataMap = jsonString.convertJsonToMapOfAny()
    )
}

internal fun AdaptyRemoteConfig.asAdaptyPaywallRemoteConfigResponse(): AdaptyRemoteConfigResponse {
    return AdaptyRemoteConfigResponse(
        locale = locale,
        jsonString = dataJsonString
    )
}
