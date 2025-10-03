package com.adapty.kmp.models

public sealed interface AdaptyOnboardingsAnalyticsEvent

public data object AdaptyOnboardingsAnalyticsEventOnboardingStarted : AdaptyOnboardingsAnalyticsEvent

public data object AdaptyOnboardingsAnalyticsEventScreenPresented : AdaptyOnboardingsAnalyticsEvent

public data class AdaptyOnboardingsAnalyticsEventScreenCompleted(
    val elementId: String? = null,
    val reply: String? = null,
) : AdaptyOnboardingsAnalyticsEvent

public data object AdaptyOnboardingsAnalyticsEventSecondScreenPresented : AdaptyOnboardingsAnalyticsEvent

public data object AdaptyOnboardingsAnalyticsEventRegistrationScreenPresented :
    AdaptyOnboardingsAnalyticsEvent

public data object AdaptyOnboardingsAnalyticsEventProductsScreenPresented :
    AdaptyOnboardingsAnalyticsEvent

public data object AdaptyOnboardingsAnalyticsEventUserEmailCollected : AdaptyOnboardingsAnalyticsEvent

public data object AdaptyOnboardingsAnalyticsEventOnboardingCompleted : AdaptyOnboardingsAnalyticsEvent

public data class AdaptyOnboardingsAnalyticsEventUnknown(val name: String) :
    AdaptyOnboardingsAnalyticsEvent
