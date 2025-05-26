package com.adapty.kmp.internal.plugin.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptyLogShowPaywallRequest(
    @SerialName("paywall") val paywall: AdaptyPaywallRequestResponse
)