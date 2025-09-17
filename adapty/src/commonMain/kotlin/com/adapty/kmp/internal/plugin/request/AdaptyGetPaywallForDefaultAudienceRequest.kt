package com.adapty.kmp.internal.plugin.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptyGetPaywallForDefaultAudienceRequest(
    @SerialName("placement_id") val placementId: String,
    @SerialName("locale") val locale: String? = null,
    @SerialName("fetch_policy") val fetchPolicy: com.adapty.kmp.internal.plugin.request.AdaptyPaywallFetchPolicyRequest? = null,
)