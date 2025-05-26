package com.adapty.kmp.internal.plugin.response

import com.adapty.kmp.models.AdaptyUIView
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptyUIViewResponse(
    @SerialName("id")
    val id: String,

    @SerialName("placement_id")
    val placementId: String,

    @SerialName("paywall_variation_id")
    val paywallVariationId: String
)

internal fun AdaptyUIViewResponse.asAdaptyUIView(): AdaptyUIView {
    return AdaptyUIView(
        id = id,
        placementId = placementId,
        paywallVariationId = paywallVariationId
    )
}