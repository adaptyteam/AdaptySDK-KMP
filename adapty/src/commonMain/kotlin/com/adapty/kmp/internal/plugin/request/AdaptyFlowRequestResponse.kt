package com.adapty.kmp.internal.plugin.request

import com.adapty.kmp.internal.plugin.response.AdaptyRemoteConfigResponse
import com.adapty.kmp.internal.plugin.response.asAdaptyPaywallRemoteConfig
import com.adapty.kmp.internal.plugin.response.asAdaptyPaywallRemoteConfigResponse
import com.adapty.kmp.models.AdaptyFlow
import com.adapty.kmp.models.AdaptyPaywall
import com.adapty.kmp.models.AdaptyPaywallViewConfiguration
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Wire representation of `AdaptyFlow` (cross_platform 4.0.0).
 *
 * A flow groups one or more paywall variations plus flow-level metadata. Converted to/from the
 * public [AdaptyFlow] and the legacy [AdaptyPaywall] via the `asAdapty*` / `asAdapty*Request`
 * mappers in this package, following the codebase's model <-> wire conversion convention.
 */
@Serializable
internal data class AdaptyFlowRequestResponse(
    @SerialName("placement")
    val placement: AdaptyPlacementRequestResponse,

    @SerialName("flow_id")
    val flowId: String,

    @SerialName("flow_name")
    val flowName: String,

    @SerialName("variation_id")
    val variationId: String,

    @SerialName("remote_configs")
    val remoteConfigs: List<AdaptyRemoteConfigResponse>? = null,

    @SerialName("flow_version_id")
    val flowVersionId: String? = null,

    @SerialName("variations")
    val variations: List<AdaptyFlowPaywallRequestResponse> = emptyList(),

    @SerialName("payload_data")
    val payloadData: String? = null,

    @SerialName("response_created_at")
    val responseCreatedAt: Long = 0L,
)

/**
 * Returns the flow variation matching the flow's active `variation_id`, falling back to the
 * first variation. Used to map a multi-variation flow onto the single legacy [AdaptyPaywall].
 */
internal fun AdaptyFlowRequestResponse.selectedVariation(): AdaptyFlowPaywallRequestResponse? =
    variations.firstOrNull { it.variationId == variationId } ?: variations.firstOrNull()

/** Maps the flow wire DTO onto the public [AdaptyFlow]. */
internal fun AdaptyFlowRequestResponse.asAdaptyFlow(): AdaptyFlow =
    AdaptyFlow(
        placement = this.placement.asAdaptyPlacement(),
        instanceIdentity = this.flowId,
        name = this.flowName,
        variationId = this.variationId,
        remoteConfigs = this.remoteConfigs?.map { it.asAdaptyPaywallRemoteConfig() } ?: emptyList(),
        flowVersionId = this.flowVersionId,
        variations = this.variations.map { it.asAdaptyFlowPaywall() },
        payloadData = this.payloadData,
        responseCreatedAt = this.responseCreatedAt,
    )

/** Rebuilds the flow wire DTO from the public [AdaptyFlow] (lossless round-trip). */
internal fun AdaptyFlow.asAdaptyFlowRequest(): AdaptyFlowRequestResponse =
    AdaptyFlowRequestResponse(
        placement = this.placement.asAdaptyPlacementRequestResponse(),
        flowId = this.instanceIdentity,
        flowName = this.name,
        variationId = this.variationId,
        remoteConfigs = this.remoteConfigs.map { it.asAdaptyPaywallRemoteConfigResponse() }.ifEmpty { null },
        flowVersionId = this.flowVersionId,
        variations = this.variations.map { it.asAdaptyFlowPaywallRequest() },
        payloadData = this.payloadData,
        responseCreatedAt = this.responseCreatedAt,
    )

/**
 * Maps a flow onto the legacy [AdaptyPaywall] (its active variation).
 */
internal fun AdaptyFlowRequestResponse.asAdaptyPaywall(): AdaptyPaywall {
    val variation = selectedVariation()
    val lang = this.remoteConfigs?.firstOrNull()?.locale
    return AdaptyPaywall(
        placement = this.placement.asAdaptyPlacement(),
        instanceIdentity = this.flowId,
        name = this.flowName,
        variationId = this.variationId,
        remoteConfig = this.remoteConfigs?.firstOrNull()?.asAdaptyPaywallRemoteConfig(),
        // Flows are always renderable; expose a view configuration so hasViewConfiguration stays true.
        viewConfiguration = AdaptyPaywallViewConfiguration(
            paywallBuilderId = this.flowId,
            locale = lang ?: "",
        ),
        products = variation?.products?.map { it.asAdaptyPaywallProductReference() } ?: emptyList(),
        payloadData = this.payloadData,
        webPurchaseUrl = variation?.webPurchaseUrl,
        requestLocale = lang,
        responseCreatedAt = this.responseCreatedAt,
    )
}

/**
 * Rebuilds a single-variation flow wire DTO from the legacy [AdaptyPaywall]. Used for the
 * deprecated paywall round-trip calls (`get_paywall_products`, `log_show_flow`,
 * `adapty_ui_create_flow_view`). The paywall represents the active variation, so the produced
 * flow contains exactly that one variation.
 */
internal fun AdaptyPaywall.asAdaptyFlowRequest(): AdaptyFlowRequestResponse {
    val placementDto = this.placement.asAdaptyPlacementRequestResponse()
    return AdaptyFlowRequestResponse(
        placement = placementDto,
        flowId = this.instanceIdentity,
        flowName = this.name,
        variationId = this.variationId,
        remoteConfigs = this.remoteConfig?.let { listOf(it.asAdaptyPaywallRemoteConfigResponse()) },
        flowVersionId = null,
        variations = listOf(this.asAdaptyFlowPaywallRequest()),
        payloadData = this.payloadData,
        responseCreatedAt = this.responseCreatedAt,
    )
}

/** The single flow-paywall variation this legacy paywall represents (for web-paywall calls). */
internal fun AdaptyPaywall.asAdaptyFlowPaywallRequest(): AdaptyFlowPaywallRequestResponse =
    AdaptyFlowPaywallRequestResponse(
        placement = this.placement.asAdaptyPlacementRequestResponse(),
        paywallId = this.instanceIdentity,
        paywallName = this.name,
        variationId = this.variationId,
        products = this.products.map { it.asAdaptyPaywallProductReferenceRequest() },
        webPurchaseUrl = this.webPurchaseUrl,
    )
