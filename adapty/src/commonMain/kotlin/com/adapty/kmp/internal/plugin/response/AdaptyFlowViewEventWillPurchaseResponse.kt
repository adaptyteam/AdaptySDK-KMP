package com.adapty.kmp.internal.plugin.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptyFlowViewEventWillPurchaseResponse(
    @SerialName("view")
    val view: AdaptyUIFlowViewResponse,

    @SerialName("product")
    val product: AdaptyPaywallProductResponse,
)
