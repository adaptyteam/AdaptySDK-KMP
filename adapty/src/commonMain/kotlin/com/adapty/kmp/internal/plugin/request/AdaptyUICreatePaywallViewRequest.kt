package com.adapty.kmp.internal.plugin.request

import com.adapty.kmp.internal.AdaptyKMPInternal
import com.adapty.kmp.internal.utils.asAdaptyValidDateTimeFormat
import com.adapty.kmp.internal.utils.jsonInstance
import com.adapty.kmp.models.AdaptyCustomAsset
import com.adapty.kmp.models.AdaptyPaywall
import com.adapty.kmp.models.AdaptyProductIdentifier
import com.adapty.kmp.models.AdaptyPurchaseParameters
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptyUICreatePaywallViewRequest(
    @SerialName("paywall") val paywall: AdaptyPaywallRequestResponse,
    @SerialName("load_timeout") val loadTimeOutInSeconds: Double?,
    @SerialName("preload_products") val preloadProducts: Boolean = false,
    @SerialName("custom_tags") val customTags: Map<String, String>? = null,
    @SerialName("custom_timers") val customTimers: Map<String, String>? = null,
    @SerialName("custom_assets") val customAssets: List<AdaptyCustomAssetRequest>? = null,
    @SerialName("product_purchase_parameters") val productPurchaseParameters: Map<String, AdaptyPurchaseParametersRequest>? = null
)

@AdaptyKMPInternal
public fun createPaywallViewRequestJsonString(
    paywall: AdaptyPaywall,
    customTags: Map<String, String>?,
    customTimers: Map<String, LocalDateTime>?,
    customAssets: Map<String, AdaptyCustomAsset>?,
    productPurchaseParams: Map<AdaptyProductIdentifier, AdaptyPurchaseParameters>?
): String {

    val request = AdaptyUICreatePaywallViewRequest(
        paywall = paywall.asAdaptyPaywallRequest(),
        loadTimeOutInSeconds = null,
        customTags = customTags,
        customTimers = customTimers?.asAdaptyValidDateTimeFormat(),
        productPurchaseParameters = productPurchaseParams?.map { (key, value) ->
            key.adaptyProductId to value.asAdaptyPurchaseParametersRequest()
        }?.toMap(),
        customAssets = customAssets?.map { (key, value) ->
            value.asAdaptyCustomAssetRequest(key)
        }
    )

    return jsonInstance.encodeToString(request)
}