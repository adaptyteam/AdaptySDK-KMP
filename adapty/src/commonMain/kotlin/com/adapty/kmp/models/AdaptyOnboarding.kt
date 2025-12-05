package com.adapty.kmp.models

import com.adapty.kmp.internal.AdaptyKMPInternal

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

    @AdaptyKMPInternal
    public val idForNativePlatformView: String = "$PREFIX_NATIVE_PLATFORM_VIEW$id"
}
