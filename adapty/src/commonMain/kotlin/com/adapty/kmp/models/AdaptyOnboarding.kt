package com.adapty.kmp.models

import com.adapty.kmp.internal.AdaptyKMPInternal
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * Represents an onboarding experience configured in the Adapty Dashboard.
 *
 * Onboarding can include paywalls, tutorials, or custom UI flows that you
 * want to show to a specific audience.
 *
 * @property placement [AdaptyPlacement] The placement associated with this onboarding.
 * @property id The unique identifier for this onboarding.
 * @property name The name of the onboarding as set in the Adapty Dashboard.
 * @property variationId The variation ID of this onboarding, used for A/B testing.
 * @property remoteConfig [AdaptyRemoteConfig] Optional remote configuration for customizing behavior or appearance.
 *
 */
@Deprecated(
    "Onboarding is deprecated as of 4.0.0 and will be removed in a future release. Migrate to the Adapty Flow Builder.",
    level = DeprecationLevel.WARNING
)
public data class AdaptyOnboarding internal constructor(
    public val placement: AdaptyPlacement,
    public val id: String,
    public val name: String,
    public val variationId: String,
    public val remoteConfig: AdaptyRemoteConfig?,
    internal val payloadData: String?,
    internal val requestLocale: String,
    internal val responseCreatedAt: Long,
    internal val onboardingBuilderConfigUrl: String,
) {
    internal companion object {
        const val PREFIX_NATIVE_PLATFORM_VIEW = "compose_native_onboarding_"
    }

    /**
     * Returns a fresh, unique id for one native/embedded view of this onboarding.
     *
     * Call it once per view instance and keep the result for that view's lifetime: the id keys both
     * the per-view events observer and the native view manager, so two views sharing an id would
     * overwrite each other's observer and misroute events. Deriving it from [id] alone is not
     * enough — the same onboarding can be embedded more than once.
     */
    @AdaptyKMPInternal
    @OptIn(ExperimentalUuidApi::class)
    public fun createNativePlatformViewId(): String =
        "$PREFIX_NATIVE_PLATFORM_VIEW${id}_${Uuid.random()}"
}
