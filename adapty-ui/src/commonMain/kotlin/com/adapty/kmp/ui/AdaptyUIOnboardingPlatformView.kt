package com.adapty.kmp.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.adapty.kmp.AdaptyUI
import com.adapty.kmp.AdaptyUIOnboardingsEventsObserver
import com.adapty.kmp.internal.AdaptyKMPInternal
import com.adapty.kmp.models.AdaptyError
import com.adapty.kmp.models.AdaptyOnboarding
import com.adapty.kmp.models.AdaptyOnboardingsAnalyticsEvent
import com.adapty.kmp.models.AdaptyOnboardingsStateUpdatedParams
import com.adapty.kmp.models.AdaptyUIOnboardingMeta
import com.adapty.kmp.models.AdaptyUIOnboardingView
import kotlinx.coroutines.CoroutineScope


/**
 * Displays an embedded onboarding as a platform view in your Compose UI hierarchy.
 *
 * This Composable allows you to embed an onboarding directly in your UI rather than
 * presenting it as a full-screen modal. It provides inline callbacks for various
 * events, such as loading completion, errors, user actions, state updates, and analytics.
 *
 * @param onboarding The onboarding instance to display.
 * @param modifier Optional [Modifier] for styling and layout.
 * @param onDidFinishLoading Callback invoked when the onboarding finishes loading successfully.
 * @param onDidFailWithError Callback invoked when the onboarding fails to load.
 * @param onCloseAction Callback invoked when a close action is triggered by the user.
 * @param onPaywallAction Callback invoked when a paywall action is triggered by the user.
 * @param onCustomAction Callback invoked when a custom action is triggered by the user.
 * @param onStateUpdatedAction Callback invoked when the state of an onboarding element is updated.
 * @param onAnalyticsEvent Callback invoked when an analytics event occurs within the onboarding.
 */
@OptIn(AdaptyKMPInternal::class)
@Composable
public fun AdaptyUIOnboardingPlatformView(
    onboarding: AdaptyOnboarding,
    modifier: Modifier = Modifier,
    onDidFinishLoading: (meta: AdaptyUIOnboardingMeta) -> Unit = {},
    onDidFailWithError: (error: AdaptyError) -> Unit = {},
    onCloseAction: (meta: AdaptyUIOnboardingMeta, actionId: String) -> Unit = { _, _ -> },
    onPaywallAction: (meta: AdaptyUIOnboardingMeta, actionId: String) -> Unit = { _, _ -> },
    onCustomAction: (meta: AdaptyUIOnboardingMeta, actionId: String) -> Unit = { _, _ -> },
    onStateUpdatedAction: (meta: AdaptyUIOnboardingMeta, elementId: String, params: AdaptyOnboardingsStateUpdatedParams) -> Unit = { _, _, _ -> },
    onAnalyticsEvent: (meta: AdaptyUIOnboardingMeta, event: AdaptyOnboardingsAnalyticsEvent) -> Unit = { _, _ -> },
) {
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(Unit) {

        AdaptyUI.registerOnboardingEventsListener(
            object : AdaptyUIOnboardingsEventsObserver {
                override val mainUiScope: CoroutineScope = coroutineScope
                override fun onboardingViewDidFinishLoading(
                    view: AdaptyUIOnboardingView,
                    meta: AdaptyUIOnboardingMeta
                ) {
                    onDidFinishLoading(meta)
                }

                override fun onboardingViewDidFailWithError(
                    view: AdaptyUIOnboardingView,
                    error: AdaptyError
                ) {
                    onDidFailWithError(error)
                }

                override fun onboardingViewOnCloseAction(
                    view: AdaptyUIOnboardingView,
                    meta: AdaptyUIOnboardingMeta,
                    actionId: String
                ) {
                    onCloseAction(meta, actionId)
                }

                override fun onboardingViewOnPaywallAction(
                    view: AdaptyUIOnboardingView,
                    meta: AdaptyUIOnboardingMeta,
                    actionId: String
                ) {
                    onPaywallAction(meta, actionId)
                }

                override fun onboardingViewOnCustomAction(
                    view: AdaptyUIOnboardingView,
                    meta: AdaptyUIOnboardingMeta,
                    actionId: String
                ) {
                    onCustomAction(meta, actionId)
                }

                override fun onboardingViewOnStateUpdatedAction(
                    view: AdaptyUIOnboardingView,
                    meta: AdaptyUIOnboardingMeta,
                    elementId: String,
                    params: AdaptyOnboardingsStateUpdatedParams
                ) {
                    onStateUpdatedAction(meta, elementId, params)
                }

                override fun onboardingViewOnAnalyticsEvent(
                    view: AdaptyUIOnboardingView,
                    meta: AdaptyUIOnboardingMeta,
                    event: AdaptyOnboardingsAnalyticsEvent
                ) {
                    onAnalyticsEvent(meta, event)
                }
            },
            viewId = onboarding.idForNativePlatformView

        )
    }
    DisposableEffect(Unit) {
        onDispose {
            AdaptyUI.unregisterOnboardingEventsListener(onboarding.idForNativePlatformView)
        }
    }

    AdaptyUIOnboardingPlatformView(
        onboarding = onboarding,
        modifier = modifier,
    )
}

@Composable
internal expect fun AdaptyUIOnboardingPlatformView(
    onboarding: AdaptyOnboarding,
    modifier: Modifier = Modifier
)