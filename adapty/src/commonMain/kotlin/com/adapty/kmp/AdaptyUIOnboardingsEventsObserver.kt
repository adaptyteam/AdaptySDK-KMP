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

public interface AdaptyUIOnboardingsEventsObserver {

    public val mainUiScope: CoroutineScope
        get() = MainScope()

    public fun onboardingViewDidFinishLoading(
        view: AdaptyUIOnboardingView,
        meta: AdaptyUIOnboardingMeta,
    ) {
    }

    public fun onboardingViewDidFailWithError(
        view: AdaptyUIOnboardingView,
        error: AdaptyError,
    ) {
    }

    public fun onboardingViewOnCloseAction(
        view: AdaptyUIOnboardingView,
        meta: AdaptyUIOnboardingMeta,
        actionId: String,
    ) {
        mainUiScope.launch {
            if (view.isStandaloneView) view.dismiss()
        }
    }

    public fun onboardingViewOnPaywallAction(
        view: AdaptyUIOnboardingView,
        meta: AdaptyUIOnboardingMeta,
        actionId: String,
    ) {
        mainUiScope.launch { presentPaywall(placementId = actionId) }
    }

    public fun onboardingViewOnCustomAction(
        view: AdaptyUIOnboardingView,
        meta: AdaptyUIOnboardingMeta,
        actionId: String,
    ) {
    }

    public fun onboardingViewOnStateUpdatedAction(
        view: AdaptyUIOnboardingView,
        meta: AdaptyUIOnboardingMeta,
        elementId: String,
        params: AdaptyOnboardingsStateUpdatedParams,
    ) {
    }

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
