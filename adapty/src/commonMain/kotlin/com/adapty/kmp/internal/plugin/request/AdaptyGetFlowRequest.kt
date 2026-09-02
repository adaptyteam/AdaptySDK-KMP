package com.adapty.kmp.internal.plugin.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptyGetFlowRequest(
    @SerialName("placement_id") val placementId: String,
    @SerialName("fetch_policy") val fetchPolicy: AdaptyPaywallFetchPolicyRequest? = null,
    @SerialName("load_timeout") val loadTimeoutInSeconds: Double? = null,
)
