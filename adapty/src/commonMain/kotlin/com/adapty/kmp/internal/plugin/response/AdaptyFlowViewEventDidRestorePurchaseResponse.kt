package com.adapty.kmp.internal.plugin.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptyFlowViewEventDidRestorePurchaseResponse(
    @SerialName("view")
    val view: AdaptyUIFlowViewResponse,

    @SerialName("profile")
    val profile: AdaptyProfileResponse,
)
