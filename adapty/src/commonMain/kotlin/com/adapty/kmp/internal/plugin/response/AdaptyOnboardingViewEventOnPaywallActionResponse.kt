package com.adapty.kmp.internal.plugin.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptyOnboardingViewEventOnPaywallActionResponse(
    @SerialName("view")
    val view: AdaptyUIOnboardingViewResponse,
    @SerialName("meta")
    val meta: AdaptyUIOnboardingMetaResponse,
    @SerialName("action_id")
    val actionId: String
)
