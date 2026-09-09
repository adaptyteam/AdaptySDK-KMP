package com.adapty.kmp.internal.plugin.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptyFlowViewEventDidFailPurchaseResponse(
    @SerialName("view")
    val view: AdaptyUIFlowViewResponse,

    @SerialName("product")
    val product: AdaptyPaywallProductResponse,

    @SerialName("error")
    val error: AdaptyPluginErrorResponse,
)
