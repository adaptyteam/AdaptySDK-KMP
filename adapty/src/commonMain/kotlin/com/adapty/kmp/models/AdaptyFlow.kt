package com.adapty.kmp.models

import com.adapty.kmp.internal.AdaptyKMPInternal
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * Represents a flow retrieved from Adapty (cross_platform 4.0.0).
 *
 * A flow groups one or more [paywalls] together with flow-level metadata and per-language remote
 * configs. It is the primary entity returned by `getFlow` / `getFlowForDefaultAudience`.
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

    /**
     * Returns a fresh, unique id for one native/embedded view of this flow.
     *
     * Call it once per view instance and keep the result for that view's lifetime: the id keys both
     * the per-view events observer and the native view manager, so two views sharing an id would
     * overwrite each other's observer and misroute events. Deriving it from [instanceIdentity] alone is not
     * enough — the same flow can be embedded more than once.
     */
    @AdaptyKMPInternal
    @OptIn(ExperimentalUuidApi::class)
    public fun createNativePlatformViewId(): String =
        "$PREFIX_NATIVE_PLATFORM_VIEW${instanceIdentity}_${Uuid.random()}"
}
