package com.adapty.kmp.internal.plugin.request

import com.adapty.kmp.models.AdaptyPlacement
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptyPlacementRequestResponse(
    @SerialName("developer_id")
    val developerId: String,

    @SerialName("audience_name")
    val audienceName: String,

    @SerialName("revision")
    val revision: Int,

    @SerialName("ab_test_name")
    val abTestName: String,

    @SerialName("placement_audience_version_id")
    val placementAudienceVersionId: String,

    @SerialName("is_tracking_purchases")
    val isTrackingPurchases: Boolean? = false,
)

internal fun AdaptyPlacementRequestResponse.asAdaptyPlacement(): AdaptyPlacement {
    return AdaptyPlacement(
        id = this.developerId,
        audienceName = this.audienceName,
        revision = this.revision,
        abTestName = this.abTestName,
        placementAudienceVersionId = this.placementAudienceVersionId,
        isTrackingPurchases = this.isTrackingPurchases ?: false
    )
}

internal fun AdaptyPlacement.asAdaptyPlacementRequestResponse(): AdaptyPlacementRequestResponse {
    return AdaptyPlacementRequestResponse(
        developerId = this.id,
        audienceName = this.audienceName,
        revision = this.revision,
        abTestName = this.abTestName,
        placementAudienceVersionId = this.placementAudienceVersionId,
        isTrackingPurchases = this.isTrackingPurchases
    )
}
