package com.adapty.kmp.internal.plugin.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptyGetOnboardingRequest(
    @SerialName("placement_id") val placementId: String,
    @SerialName("locale") val locale: String? = null,
    @SerialName("fetch_policy") val fetchPolicy: AdaptyPaywallFetchPolicyRequest? = null,
    @SerialName("load_timeout") val loadTimeoutInSeconds: Long? = null,
)


