package com.adapty.kmp.internal.plugin.response

import com.adapty.kmp.models.AdaptyUIOnboardingView
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptyUIOnboardingViewResponse(
    @SerialName("id")
    val id: String,

    @SerialName("placement_id")
    val placementId: String,

    @SerialName("variation_id")
    val variationId: String
)

internal fun AdaptyUIOnboardingViewResponse.asAdaptyUIOnboardingView(): AdaptyUIOnboardingView {
    return AdaptyUIOnboardingView(
        id = id,
        placementId = placementId,
        variationId = variationId
    )
}