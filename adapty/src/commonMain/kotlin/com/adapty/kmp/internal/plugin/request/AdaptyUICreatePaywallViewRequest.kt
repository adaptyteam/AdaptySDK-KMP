package com.adapty.kmp.internal.plugin.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptyUICreatePaywallViewRequest(
    @SerialName("paywall") val paywall: AdaptyPaywallRequestResponse,
    @SerialName("load_timeout") val loadTimeOutInSeconds: Long?,
    @SerialName("preload_products") val preloadProducts: Boolean = false,
    @SerialName("custom_tags") val customTags: Map<String, String>? = null,
    @SerialName("custom_timers") val customTimers: Map<String, String>? = null,
    @SerialName("custom_assets") val customAssets: List<AdaptyCustomAssetRequest>? = null,
    @SerialName("product_purchase_parameters") val productPurchaseParameters: Map<String, AdaptyPurchaseParametersRequest>? = null
)