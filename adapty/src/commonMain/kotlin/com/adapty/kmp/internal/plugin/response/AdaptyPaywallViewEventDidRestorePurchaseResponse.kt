package com.adapty.kmp.internal.plugin.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptyPaywallViewEventDidRestorePurchaseResponse(
    @SerialName("view")
    val view: AdaptyUIViewResponse,

    @SerialName("profile")
    val profile: AdaptyProfileResponse,
)
