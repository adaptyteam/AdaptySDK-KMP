package com.adapty.kmp.models

/**
 * Represents analytics events occurring during an onboarding flow.
 *
 * These events are sent through
 * @see com.adapty.kmp.AdaptyUIOnboardingsEventsObserver
 * to track user progress, interactions, and completion of onboarding screens.
 */
public sealed interface AdaptyOnboardingsAnalyticsEvent

/** Triggered when the onboarding flow is first loaded. */
public data object AdaptyOnboardingsAnalyticsEventOnboardingStarted : AdaptyOnboardingsAnalyticsEvent

/** Triggered when any onboarding screen is presented. */
public data object AdaptyOnboardingsAnalyticsEventScreenPresented : AdaptyOnboardingsAnalyticsEvent

/**
 * Triggered when a screen is completed.
 *
 * @property elementId Optional identifier of the completed element.
 * @property reply Optional user response associated with the element.
 */
public data class AdaptyOnboardingsAnalyticsEventScreenCompleted(
    val elementId: String? = null,
    val reply: String? = null,
) : AdaptyOnboardingsAnalyticsEvent

/** Triggered when the second screen of an onboarding flow is presented. */
public data object AdaptyOnboardingsAnalyticsEventSecondScreenPresented : AdaptyOnboardingsAnalyticsEvent

/** Triggered when the registration screen is presented. */
public data object AdaptyOnboardingsAnalyticsEventRegistrationScreenPresented :
    AdaptyOnboardingsAnalyticsEvent

/** Triggered when the products screen is presented. */
public data object AdaptyOnboardingsAnalyticsEventProductsScreenPresented :
    AdaptyOnboardingsAnalyticsEvent

/** Triggered when the user's email is collected during onboarding. */
public data object AdaptyOnboardingsAnalyticsEventUserEmailCollected : AdaptyOnboardingsAnalyticsEvent

/** Triggered when the onboarding flow is completed. */
public data object AdaptyOnboardingsAnalyticsEventOnboardingCompleted : AdaptyOnboardingsAnalyticsEvent

/**
 * Represents an unrecognized or unknown analytics event.
 *
 * @property name The name of the unknown event.
 */
public data class AdaptyOnboardingsAnalyticsEventUnknown(val name: String) :
    AdaptyOnboardingsAnalyticsEvent
