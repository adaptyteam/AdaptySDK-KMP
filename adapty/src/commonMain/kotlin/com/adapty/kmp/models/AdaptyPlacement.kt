package com.adapty.kmp.models

public data class AdaptyPlacement internal constructor(
    val id: String,
    val audienceName: String,
    val revision: Int,
    val abTestName: String,
    val placementAudienceVersionId: String,
    val isTrackingPurchases: Boolean = false,
)