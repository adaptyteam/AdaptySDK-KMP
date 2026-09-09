package com.adapty.kmp.internal.plugin.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptyFlowViewEventDidFinishWebPaymentNavigationResponse(
    @SerialName("view")
    val view: AdaptyUIFlowViewResponse,

    @SerialName("product")
    val product: AdaptyPaywallProductResponse? = null,

    @SerialName("error")
    val error: AdaptyPluginErrorResponse? = null,
)
