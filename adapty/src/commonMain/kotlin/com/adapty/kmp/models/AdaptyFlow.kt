package com.adapty.kmp.models

import com.adapty.kmp.internal.AdaptyKMPInternal

/**
 * Represents a flow retrieved from Adapty (cross_platform 4.0.0).
 *
 * A flow groups one or more paywall [variations] together with flow-level metadata and
 * per-language remote configs. It replaces the legacy [AdaptyPaywall] as the primary entity
 * returned by `getFlow` / `getFlowForDefaultAudience`.
 *
 * @property placement [AdaptyPlacement] the placement associated with this flow.
 * @property instanceIdentity a unique identifier of the flow, configured in Adapty Dashboard.
 * @property name the name of the flow.
 * @property variationId the active variation identifier for this flow.
 * @property remoteConfigs remote configurations, one per language.
 * @property flowVersionId optional flow version identifier.
 * @property paywalls the paywall variations contained in this flow.
 */
public data class AdaptyFlow internal constructor(
    public val placement: AdaptyPlacement,
    public val instanceIdentity: String,
    public val name: String,
    public val variationId: String,
    public val remoteConfigs: List<AdaptyRemoteConfig> = emptyList(),
    public val flowVersionId: String? = null,
    public val paywalls: List<AdaptyFlowPaywall> = emptyList(),
    internal val payloadData: String? = null,
    internal val responseCreatedAt: Long = 0L,
) {
    internal companion object {
        const val PREFIX_NATIVE_PLATFORM_VIEW = "compose_native_flow_"
    }

    @AdaptyKMPInternal
    public val idForNativePlatformView: String = "$PREFIX_NATIVE_PLATFORM_VIEW$instanceIdentity"
}
