package com.adapty.kmp.internal.plugin.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptyPreloadFlowsForDefaultAudienceRequest(
    @SerialName("placement_ids") val placementIds: List<String>,
)
