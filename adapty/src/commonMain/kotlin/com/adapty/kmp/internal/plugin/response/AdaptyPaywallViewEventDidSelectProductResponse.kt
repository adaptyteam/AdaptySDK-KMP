package com.adapty.kmp.internal.plugin.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptyPaywallViewEventDidSelectProductResponse(
    @SerialName("view")
    val view: AdaptyUIPaywallViewResponse,

    @SerialName("product_id")
    val productId: String,
)