package com.adapty.kmp.internal.plugin.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptyIosUpdateCollectingRefundDataRequest(
    @SerialName("consent")
    val consent: Boolean
)
