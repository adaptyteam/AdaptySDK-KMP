package com.adapty.kmp.models

/**
 * Represents a paywall or onboarding placement in the Adapty system.
 *
 * @property id Unique identifier of the placement configured in the Adapty Dashboard.
 * @property audienceName Name of the audience targeted by this placement.
 * @property revision The revision number of the placement, useful for versioning.
 * @property abTestName Name of the A/B test associated with this placement.
 * @property placementAudienceVersionId Identifier for the placement audience version.
 * @property isTrackingPurchases Indicates whether purchases should be tracked for this placement.
 */
public data class AdaptyPlacement internal constructor(
    val id: String,
    val audienceName: String,
    val revision: Int,
    val abTestName: String,
    val placementAudienceVersionId: String,
    val isTrackingPurchases: Boolean = false,
)