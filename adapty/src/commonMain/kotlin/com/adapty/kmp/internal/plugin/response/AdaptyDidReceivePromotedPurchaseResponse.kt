package com.adapty.kmp.internal.plugin.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptyDidReceivePromotedPurchaseResponse(
    @SerialName("product")
    val product: AdaptyPromotedProductResponse
)
