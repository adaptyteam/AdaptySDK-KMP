package com.adapty.kmp.internal.plugin.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptyPaywallViewEventWillPurchaseResponse(
    @SerialName("view")
    val view: AdaptyUIPaywallViewResponse,

    @SerialName("product")
    val product: AdaptyPaywallProductResponse,
)
