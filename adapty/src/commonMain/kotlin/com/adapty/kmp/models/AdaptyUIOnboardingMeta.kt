package com.adapty.kmp.models

/**
 * Contains metadata about the current onboarding flow and screen.
 *
 * This object is included in all onboarding-related callbacks to help you
 * identify the current onboarding session and screen position.
 *
 * @property onboardingId Unique identifier of the onboarding flow.
 * @property screenClientId Identifier of the currently displayed screen.
 * @property screenIndex Index of the current screen within the flow (starting from 0).
 * @property screensTotal Total number of screens in the onboarding flow.
 */
public data class AdaptyUIOnboardingMeta(
    public val onboardingId: String,
    public val screenClientId: String,
    public val screenIndex: Int,
    public val screensTotal: Int,
)