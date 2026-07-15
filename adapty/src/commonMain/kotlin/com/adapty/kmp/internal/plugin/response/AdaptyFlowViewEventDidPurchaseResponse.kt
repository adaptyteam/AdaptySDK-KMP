package com.adapty.kmp.internal.plugin.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptyFlowViewEventDidPurchaseResponse(
    @SerialName("view")
    val view: AdaptyUIFlowViewResponse,

    @SerialName("product")
    val product: AdaptyPaywallProductResponse,

    @SerialName("purchased_result")
    val purchasedResult: AdaptyPurchaseResultResponse,
)
