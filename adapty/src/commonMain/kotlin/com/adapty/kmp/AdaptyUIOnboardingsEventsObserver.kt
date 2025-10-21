package com.adapty.kmp

import com.adapty.kmp.internal.logger
import com.adapty.kmp.models.AdaptyError
import com.adapty.kmp.models.AdaptyOnboardingsAnalyticsEvent
import com.adapty.kmp.models.AdaptyOnboardingsStateUpdatedParams
import com.adapty.kmp.models.AdaptyUIOnboardingMeta
import com.adapty.kmp.models.AdaptyUIOnboardingView
import com.adapty.kmp.models.onError
import com.adapty.kmp.models.onSuccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * Observes events from AdaptyUI onboardings and handles user interactions or analytics callbacks.
 *
 * Implement this interface to monitor onboarding lifecycle events such as loading,
 * closing, paywall openings, and analytics tracking. Use it to customize behavior
 * or integrate analytics when users progress through your onboarding flow.
 *
 * Example:
 * ```
 * Adapty.setOnboardingsEventsObserver(object : AdaptyUIOnboardingsEventsObserver {
 *     override fun onboardingViewOnPaywallAction(
 *         view: AdaptyUIOnboardingView,
 *         meta: AdaptyUIOnboardingMeta,
 *         actionId: String
 *     ) {
 *         // Open a paywall or handle a custom action
 *     }
 * })
 * ```
 *
 * @see AdaptyUIOnboardingView
 */
public interface AdaptyUIOnboardingsEventsObserver {

    /** Main [CoroutineScope] used for onboarding UI actions. */
    public val mainUiScope: CoroutineScope
        get() = MainScope()

    /** Called when the onboarding view finishes loading. */
    public fun onboardingViewDidFinishLoading(
        view: AdaptyUIOnboardingView,
        meta: AdaptyUIOnboardingMeta,
    ) {
    }

    /**
     * Called when the onboarding view fails to load due to an [AdaptyError].
     */
    public fun onboardingViewDidFailWithError(
        view: AdaptyUIOnboardingView,
        error: AdaptyError,
    ) {
    }

    /**
     * Called when the user performs a close action (e.g., tapping a close button) on the onboarding view.
     * By default, dismisses the view if it is standalone.
     */
    public fun onboardingViewOnCloseAction(
        view: AdaptyUIOnboardingView,
        meta: AdaptyUIOnboardingMeta,
        actionId: String,
    ) {
        mainUiScope.launch {
            if (view.isStandaloneView) view.dismiss()
        }
    }

    /**
     * Called when a button or element with a paywall action is triggered.
     * By default, opens the paywall using the provided [actionId] as the placement ID.
     */
    public fun onboardingViewOnPaywallAction(
        view: AdaptyUIOnboardingView,
        meta: AdaptyUIOnboardingMeta,
        actionId: String,
    ) {
        mainUiScope.launch { presentPaywall(placementId = actionId) }
    }

    /**
     * Called when a custom action is triggered from the onboarding.
     * Use the [actionId] to identify and perform your custom logic.
     */
    public fun onboardingViewOnCustomAction(
        view: AdaptyUIOnboardingView,
        meta: AdaptyUIOnboardingMeta,
        actionId: String,
    ) {
    }

    /**
     * Called when an element's state is updated (e.g., user interaction with input fields or toggles).
     */
    public fun onboardingViewOnStateUpdatedAction(
        view: AdaptyUIOnboardingView,
        meta: AdaptyUIOnboardingMeta,
        elementId: String,
        params: AdaptyOnboardingsStateUpdatedParams,
    ) {
    }

    /**
     * Called whenever an analytics event occurs during the onboarding flow.
     * Use this to track onboarding progress or user behavior.
     */
    public fun onboardingViewOnAnalyticsEvent(
        view: AdaptyUIOnboardingView,
        meta: AdaptyUIOnboardingMeta,
        event: AdaptyOnboardingsAnalyticsEvent,
    ) {
    }

    private suspend fun presentPaywall(placementId: String) {
        val paywallResult = Adapty.getPaywall(placementId)
        paywallResult.onSuccess { paywall ->
            val paywallViewResult = AdaptyUI.createPaywallView(paywall)
            paywallViewResult
                .onSuccess { paywallView -> paywallView.present() }
                .onError { logger.log("Error while presenting paywall: $it") }
        }.onError { error ->
            logger.log("Error while presenting paywall: $error")
        }
    }
}
