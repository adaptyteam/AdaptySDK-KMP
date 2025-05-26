package com.adapty.kmp.internal.plugin.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptyLogShowOnboardingRequest(
    @SerialName("params") val params: AdaptyOnboardingScreenParametersRequest
)

@Serializable
internal data class AdaptyOnboardingScreenParametersRequest(
    @SerialName("onboarding_screen_order") val onboardingScreenOrder: Int,
    @SerialName("onboarding_name") val onboardingName: String? = null,
    @SerialName("onboarding_screen_name") val onboardingScreenName: String? = null,
)