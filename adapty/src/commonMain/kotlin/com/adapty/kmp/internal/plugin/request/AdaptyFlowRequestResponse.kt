package com.adapty.kmp.internal.plugin.request

import com.adapty.kmp.internal.plugin.response.AdaptyRemoteConfigResponse
import com.adapty.kmp.internal.plugin.response.asAdaptyPaywallRemoteConfig
import com.adapty.kmp.internal.plugin.response.asAdaptyPaywallRemoteConfigResponse
import com.adapty.kmp.models.AdaptyFlow
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Wire representation of `AdaptyFlow` (cross_platform 4.0.0).
 *
 * A flow groups one or more paywall variations plus flow-level metadata. Converted to/from the
 * public [AdaptyFlow] via the `asAdapty*` / `asAdapty*Request` mappers in this package,
 * following the codebase's model <-> wire conversion convention.
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

/** Maps the flow wire DTO onto the public [AdaptyFlow]. */
internal fun AdaptyFlowRequestResponse.asAdaptyFlow(): AdaptyFlow =
    AdaptyFlow(
        placement = this.placement.asAdaptyPlacement(),
        instanceIdentity = this.flowId,
        name = this.flowName,
        variationId = this.variationId,
        remoteConfigs = this.remoteConfigs?.map { it.asAdaptyPaywallRemoteConfig() } ?: emptyList(),
        flowVersionId = this.flowVersionId,
        paywalls = this.variations.map { it.asAdaptyFlowPaywall() },
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
        variations = this.paywalls.map { it.asAdaptyFlowPaywallRequest() },
        payloadData = this.payloadData,
        responseCreatedAt = this.responseCreatedAt,
    )
