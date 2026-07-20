package com.adapty.kmp.internal.plugin.request

import com.adapty.kmp.internal.AdaptyKMPInternal
import com.adapty.kmp.internal.utils.asAdaptyValidDateTimeFormat
import com.adapty.kmp.internal.utils.jsonInstance
import com.adapty.kmp.models.AdaptyCustomAsset
import com.adapty.kmp.models.AdaptyFlow
import com.adapty.kmp.models.AdaptyProductIdentifier
import com.adapty.kmp.models.AdaptyPurchaseParameters
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptyUICreateFlowViewRequest(
    @SerialName("flow") val flow: AdaptyFlowRequestResponse,
    @SerialName("load_timeout") val loadTimeOutInSeconds: Double?,
    @SerialName("preload_products") val preloadProducts: Boolean = false,
    @SerialName("custom_tags") val customTags: Map<String, String>? = null,
    @SerialName("custom_timers") val customTimers: Map<String, String>? = null,
    @SerialName("custom_assets") val customAssets: List<AdaptyCustomAssetRequest>? = null,
    @SerialName("product_purchase_parameters") val productPurchaseParameters: Map<String, AdaptyPurchaseParametersRequest>? = null,
    @SerialName("enable_safe_area_paddings") val enableSafeAreaPaddings: Boolean = true
)

@AdaptyKMPInternal
public fun createFlowViewRequestJsonString(
    flow: AdaptyFlow,
    customTags: Map<String, String>?,
    customTimers: Map<String, LocalDateTime>?,
    customAssets: Map<String, AdaptyCustomAsset>?,
    productPurchaseParams: Map<AdaptyProductIdentifier, AdaptyPurchaseParameters>?,
    enableSafeAreaPaddings: Boolean = true
): String = createFlowViewRequestJsonString(
    flow = flow.asAdaptyFlowRequest(),
    customTags = customTags,
    customTimers = customTimers,
    customAssets = customAssets,
    productPurchaseParams = productPurchaseParams,
    enableSafeAreaPaddings = enableSafeAreaPaddings
)

private fun createFlowViewRequestJsonString(
    flow: AdaptyFlowRequestResponse,
    customTags: Map<String, String>?,
    customTimers: Map<String, LocalDateTime>?,
    customAssets: Map<String, AdaptyCustomAsset>?,
    productPurchaseParams: Map<AdaptyProductIdentifier, AdaptyPurchaseParameters>?,
    enableSafeAreaPaddings: Boolean = true
): String {

    val request = AdaptyUICreateFlowViewRequest(
        flow = flow,
        loadTimeOutInSeconds = null,
        customTags = customTags,
        customTimers = customTimers?.asAdaptyValidDateTimeFormat(),
        productPurchaseParameters = productPurchaseParams?.map { (key, value) ->
            key.adaptyProductId to value.asAdaptyPurchaseParametersRequest()
        }?.toMap(),
        customAssets = customAssets?.map { (key, value) ->
            value.asAdaptyCustomAssetRequest(key)
        },
        enableSafeAreaPaddings = enableSafeAreaPaddings
    )

    return jsonInstance.encodeToString(request)
}
