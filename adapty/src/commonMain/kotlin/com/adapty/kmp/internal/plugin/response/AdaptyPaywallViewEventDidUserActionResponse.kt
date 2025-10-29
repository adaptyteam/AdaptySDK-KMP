package com.adapty.kmp.internal.plugin.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptyPaywallViewEventDidUserActionResponse(
    @SerialName("view")
    val view: AdaptyUIPaywallViewResponse,

    @SerialName("action")
    val action: AdaptyUIActionResponse,
)