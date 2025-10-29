package com.adapty.kmp.internal.plugin.response

import com.adapty.kmp.models.AdaptyUIOnboardingMeta
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptyUIOnboardingMetaResponse(
    @SerialName("onboarding_id") val onboardingId: String,
    @SerialName("screen_cid") val screenClientId: String,
    @SerialName("screen_index") val screenIndex: Int,
    @SerialName("total_screens") val totalScreens: Int
)

internal fun AdaptyUIOnboardingMetaResponse.asAdaptyUIOnboardingMeta(): AdaptyUIOnboardingMeta{

    return AdaptyUIOnboardingMeta(
        onboardingId = onboardingId,
        screenClientId = screenClientId,
        screenIndex = screenIndex,
        screensTotal = totalScreens
    )
}
