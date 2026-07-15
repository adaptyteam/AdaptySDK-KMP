package com.adapty.kmp.internal.plugin.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptyFlowViewEventWillRestorePurchaseResponse(
    @SerialName("view")
    val view: AdaptyUIFlowViewResponse,
)
