package com.adapty.kmp.internal.plugin.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptyGetPaywallRequest(
    @SerialName("placement_id") val placementId: String,
    @SerialName("locale") val locale: String? = null,
    @SerialName("fetch_policy") val fetchPolicy: com.adapty.kmp.internal.plugin.request.AdaptyPaywallFetchPolicyRequest? = null,
    @SerialName("load_timeout") val loadTimeoutInSeconds: Long? = null,
)


