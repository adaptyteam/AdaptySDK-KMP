package com.adapty.kmp.internal.plugin.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptyGetOnboardingForDefaultAudienceRequest(
    @SerialName("placement_id") val placementId: String,
    @SerialName("locale") val locale: String? = null,
    @SerialName("fetch_policy") val fetchPolicy: AdaptyPaywallFetchPolicyRequest? = null,
)