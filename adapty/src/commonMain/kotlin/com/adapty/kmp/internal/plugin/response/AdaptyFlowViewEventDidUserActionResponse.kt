package com.adapty.kmp.internal.plugin.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptyFlowViewEventDidUserActionResponse(
    @SerialName("view")
    val view: AdaptyUIFlowViewResponse,

    @SerialName("action")
    val action: AdaptyUIActionResponse,
)