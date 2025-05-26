package com.adapty.kmp.internal.plugin.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptyPaywallViewEventDidRestorePurchaseResponse(
    @SerialName("view")
    val view: com.adapty.kmp.internal.plugin.response.AdaptyUIViewResponse,

    @SerialName("profile")
    val profile: com.adapty.kmp.internal.plugin.response.AdaptyProfileResponse,
)
