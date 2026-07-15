package com.adapty.kmp.internal.plugin.response

import com.adapty.kmp.models.AdaptyUIFlowView
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptyUIFlowViewResponse(
    @SerialName("id")
    val id: String,

    @SerialName("placement_id")
    val placementId: String,

    @SerialName("variation_id")
    val variationId: String
)

internal fun AdaptyUIFlowViewResponse.asAdaptyUIFlowView(): AdaptyUIFlowView {
    return AdaptyUIFlowView(
        id = id,
        placementId = placementId,
        variationId = variationId
    )
}