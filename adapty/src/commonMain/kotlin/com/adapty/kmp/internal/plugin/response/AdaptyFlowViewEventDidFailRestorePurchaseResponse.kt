package com.adapty.kmp.internal.plugin.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptyFlowViewEventDidFailRestorePurchaseResponse(
    @SerialName("view")
    val view: AdaptyUIFlowViewResponse,

    @SerialName("error")
    val error: AdaptyPluginErrorResponse,
)
