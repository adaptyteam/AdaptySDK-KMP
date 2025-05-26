package com.adapty.kmp.internal.plugin.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptyUICreateViewRequest(
    @SerialName("paywall") val paywall: AdaptyPaywallRequestResponse,
    @SerialName("load_timeout") val loadTimeOutInSeconds: Long?,
    @SerialName("preload_products") val preloadProducts: Boolean = false,
    @SerialName("custom_tags") val customTags: Map<String, String>? = null,
    @SerialName("custom_timers") val customTimers: Map<String, String>? = null,
    @SerialName("android_personalized_offers") val androidPersonalizedOffers: Map<String, Boolean>? = null
)