package com.adapty.kmp.internal.plugin.request

import com.adapty.kmp.internal.AdaptyKMPInternal
import com.adapty.kmp.internal.plugin.response.AdaptyPaywallViewConfigurationResponse
import com.adapty.kmp.internal.plugin.response.AdaptyRemoteConfigResponse
import com.adapty.kmp.internal.plugin.response.asAdaptyPaywallRemoteConfig
import com.adapty.kmp.internal.plugin.response.asAdaptyPaywallRemoteConfigResponse
import com.adapty.kmp.internal.plugin.response.asAdaptyPaywallViewConfiguration
import com.adapty.kmp.internal.plugin.response.asAdaptyPaywallViewConfigurationResponse
import com.adapty.kmp.internal.utils.jsonInstance
import com.adapty.kmp.models.AdaptyOnboarding
import com.adapty.kmp.models.AdaptyPaywall
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptyPaywallRequestResponse(
    @SerialName("placement")
    val placement: AdaptyPlacementRequestResponse,

    @SerialName("paywall_id")
    val paywallId: String,

    @SerialName("paywall_name")
    val paywallName: String,

    @SerialName("response_created_at")
    val responseCreatedAt: Long,

    @SerialName("variation_id")
    val variationId: String,

    @SerialName("remote_config")
    val remoteConfig: AdaptyRemoteConfigResponse? = null,

    @SerialName("paywall_builder")
    val viewConfiguration: AdaptyPaywallViewConfigurationResponse? = null,

    @SerialName("products")
    val products: List<AdaptyPaywallProductReferenceRequestResponse>,

    @SerialName("payload_data")
    val payloadData: String? = null,

    @SerialName("request_locale")
    val requestLocale: String?,

    @SerialName("web_purchase_url")
    val webPurchaseUrl: String? = null
)

internal fun AdaptyPaywallRequestResponse.asAdaptyPaywall(): AdaptyPaywall {
    return AdaptyPaywall(
        instanceIdentity = this.paywallId,
        name = this.paywallName,
        variationId = this.variationId,
        remoteConfig = this.remoteConfig?.asAdaptyPaywallRemoteConfig(),
        viewConfiguration = this.viewConfiguration?.asAdaptyPaywallViewConfiguration(),
        products = this.products.map { it.asAdaptyPaywallProductReference() },
        payloadData = this.payloadData,
        responseCreatedAt = this.responseCreatedAt,
        placement = this.placement.asAdaptyPlacement(),
        requestLocale = this.requestLocale,
        webPurchaseUrl = this.webPurchaseUrl
    )
}

internal fun AdaptyPaywall.asAdaptyPaywallRequest(): AdaptyPaywallRequestResponse {

    return AdaptyPaywallRequestResponse(
        paywallId = this.instanceIdentity,
        paywallName = this.name,
        variationId = this.variationId,
        remoteConfig = this.remoteConfig?.asAdaptyPaywallRemoteConfigResponse(),
        viewConfiguration = this.viewConfiguration?.asAdaptyPaywallViewConfigurationResponse(),
        products = this.products.map { it.asAdaptyPaywallProductReferenceRequest() },
        payloadData = this.payloadData,
        responseCreatedAt = responseCreatedAt,
        placement = this.placement.asAdaptyPlacementRequestResponse(),
        requestLocale = this.requestLocale,
        webPurchaseUrl = this.webPurchaseUrl,
    )
}
