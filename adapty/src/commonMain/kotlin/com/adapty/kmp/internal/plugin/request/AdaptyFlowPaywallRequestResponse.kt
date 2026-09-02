package com.adapty.kmp.internal.plugin.request

import com.adapty.kmp.models.AdaptyFlowPaywall
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Wire representation of `AdaptyFlowPaywall` (cross_platform 4.0.0) — a single paywall
 * variation nested inside an [AdaptyFlowRequestResponse]. Also used standalone as the
 * `paywall` payload of `open_web_paywall` / `create_web_paywall_url`.
 */
@Serializable
internal data class AdaptyFlowPaywallRequestResponse(
    @SerialName("placement")
    val placement: AdaptyPlacementRequestResponse,

    @SerialName("paywall_id")
    val paywallId: String,

    @SerialName("paywall_name")
    val paywallName: String,

    @SerialName("variation_id")
    val variationId: String,

    @SerialName("products")
    val products: List<AdaptyPaywallProductReferenceRequestResponse> = emptyList(),

    @SerialName("web_purchase_url")
    val webPurchaseUrl: String? = null,
)

internal fun AdaptyFlowPaywallRequestResponse.asAdaptyFlowPaywall(): AdaptyFlowPaywall =
    AdaptyFlowPaywall(
        placement = this.placement.asAdaptyPlacement(),
        instanceIdentity = this.paywallId,
        name = this.paywallName,
        variationId = this.variationId,
        products = this.products.map { it.asAdaptyPaywallProductReference() },
        webPurchaseUrl = this.webPurchaseUrl,
    )

internal fun AdaptyFlowPaywall.asAdaptyFlowPaywallRequest(): AdaptyFlowPaywallRequestResponse =
    AdaptyFlowPaywallRequestResponse(
        placement = this.placement.asAdaptyPlacementRequestResponse(),
        paywallId = this.instanceIdentity,
        paywallName = this.name,
        variationId = this.variationId,
        products = this.products.map { it.asAdaptyPaywallProductReferenceRequest() },
        webPurchaseUrl = this.webPurchaseUrl,
    )
