package com.adapty.kmp.internal.plugin.response

import com.adapty.kmp.models.AdaptyUIPaywallView
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptyUIPaywallViewResponse(
    @SerialName("id")
    val id: String,

    @SerialName("placement_id")
    val placementId: String,

    @SerialName("variation_id")
    val variationId: String
)

internal fun AdaptyUIPaywallViewResponse.asAdaptyUIView(): AdaptyUIPaywallView {
    return AdaptyUIPaywallView(
        id = id,
        placementId = placementId,
        variationId = variationId
    )
}