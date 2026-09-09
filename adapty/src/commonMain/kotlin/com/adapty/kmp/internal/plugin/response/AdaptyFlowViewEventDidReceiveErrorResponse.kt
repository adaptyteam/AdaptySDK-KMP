package com.adapty.kmp.internal.plugin.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptyFlowViewEventDidReceiveErrorResponse(
    @SerialName("view")
    val view: AdaptyUIFlowViewResponse,

    @SerialName("error")
    val error: AdaptyPluginErrorResponse,
)
