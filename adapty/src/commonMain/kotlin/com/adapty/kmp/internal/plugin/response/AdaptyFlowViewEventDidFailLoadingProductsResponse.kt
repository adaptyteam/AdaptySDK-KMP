package com.adapty.kmp.internal.plugin.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptyFlowViewEventDidFailLoadingProductsResponse(
    @SerialName("view")
    val view: AdaptyUIFlowViewResponse,

    @SerialName("error")
    val error: AdaptyPluginErrorResponse,
)
