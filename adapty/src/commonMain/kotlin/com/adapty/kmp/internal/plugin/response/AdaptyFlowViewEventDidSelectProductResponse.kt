package com.adapty.kmp.internal.plugin.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptyFlowViewEventDidSelectProductResponse(
    @SerialName("view")
    val view: AdaptyUIFlowViewResponse,

    @SerialName("product_id")
    val productId: String,
)