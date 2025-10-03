package com.adapty.kmp.models

import com.adapty.kmp.internal.AdaptyKMPInternal

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
        const val PREFIX_NATIVE_PLATFORM_VIEW = "compose_native_"
    }

    @AdaptyKMPInternal
    public val idForNativePlatformView: String = "$PREFIX_NATIVE_PLATFORM_VIEW$id"
}
