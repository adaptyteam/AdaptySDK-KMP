package com.adapty.kmp.internal.plugin.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptyPaywallViewEventWillPurchaseResponse(
    @SerialName("view")
    val view: com.adapty.kmp.internal.plugin.response.AdaptyUIViewResponse,

    @SerialName("product")
    val product: com.adapty.kmp.internal.plugin.response.AdaptyPaywallProductResponse,
)
