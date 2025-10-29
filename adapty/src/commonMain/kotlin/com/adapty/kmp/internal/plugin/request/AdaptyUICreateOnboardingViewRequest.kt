package com.adapty.kmp.internal.plugin.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptyUICreateOnboardingViewRequest(
    @SerialName("onboarding")
    val onboarding: AdaptyOnboardingRequestResponse
)


