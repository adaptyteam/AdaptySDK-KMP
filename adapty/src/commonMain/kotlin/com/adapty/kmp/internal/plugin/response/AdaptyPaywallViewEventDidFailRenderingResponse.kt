package com.adapty.kmp.internal.plugin.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptyPaywallViewEventDidFailRenderingResponse(
    @SerialName("view")
    val view: AdaptyUIViewResponse,

    @SerialName("error")
    val error: AdaptyPluginErrorResponse,
)
