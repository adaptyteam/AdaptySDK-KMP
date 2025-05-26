package com.adapty.kmp.internal.plugin.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptyGetPaywallProductsRequest(
    @SerialName("paywall") val paywall: AdaptyPaywallRequestResponse
)