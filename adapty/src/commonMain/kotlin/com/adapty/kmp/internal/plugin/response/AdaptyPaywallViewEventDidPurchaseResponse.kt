package com.adapty.kmp.internal.plugin.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptyPaywallViewEventDidPurchaseResponse(
    @SerialName("view")
    val view: AdaptyUIPaywallViewResponse,

    @SerialName("product")
    val product: AdaptyPaywallProductResponse,

    @SerialName("purchased_result")
    val purchasedResult: AdaptyPurchaseResultResponse,
)
