@file:Suppress("DEPRECATION") // this file *is* the deprecated onboarding surface

package com.adapty.kmp.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.adapty.kmp.models.AdaptyWebPresentation
import kotlinx.coroutines.CoroutineScope


/**
 * Displays an embedded onboarding as a platform view in your Compose UI hierarchy.
 *
 * This Composable allows you to embed an onboarding directly in your UI rather than
 * presenting it as a full-screen modal. It provides inline callbacks for various
 * events, such as loading completion, errors, user actions, state updates, and analytics.
 *
 * @param onboarding The onboarding instance to display.
 * @param externalUrlsPresentation Defines where the web  should be opened. Defaults to [AdaptyWebPresentation.IN_APP_BROWSER].
 * @param modifier Optional [Modifier] for styling and layout.
 * @param onDidFinishLoading Callback invoked when the onboarding finishes loading successfully.
 * @param onDidFailWithError Callback invoked when the onboarding fails to load.
 * @param onCloseAction Callback invoked when a close action is triggered by the user.
 * @param onPaywallAction Callback invoked when a paywall action is triggered by the user.
 * @param onCustomAction Callback invoked when a custom action is triggered by the user.
 * @param onStateUpdatedAction Callback invoked when the state of an onboarding element is updated.
 * @param onAnalyticsEvent Callback invoked when an analytics event occurs within the onboarding.
 */
@Deprecated(
    "Onboarding is deprecated as of 4.0.0 and will be removed in a future release. Migrate to the Adapty Flow Builder.",
    level = DeprecationLevel.WARNING
)
@OptIn(AdaptyKMPInternal::class)
@Composable
public fun AdaptyUIOnboardingPlatformView(
    onboarding: AdaptyOnboarding,
    modifier: Modifier = Modifier,
    externalUrlsPresentation: AdaptyWebPresentation = AdaptyWebPresentation.IN_APP_BROWSER,
    onDidFinishLoading: (meta: AdaptyUIOnboardingMeta) -> Unit = {},
    onDidFailWithError: (error: AdaptyError) -> Unit = {},
    onCloseAction: (meta: AdaptyUIOnboardingMeta, actionId: String) -> Unit = { _, _ -> },
    onPaywallAction: (meta: AdaptyUIOnboardingMeta, actionId: String) -> Unit = { _, _ -> },
    onCustomAction: (meta: AdaptyUIOnboardingMeta, actionId: String) -> Unit = { _, _ -> },
    onStateUpdatedAction: (meta: AdaptyUIOnboardingMeta, elementId: String, params: AdaptyOnboardingsStateUpdatedParams) -> Unit = { _, _, _ -> },
    onAnalyticsEvent: (meta: AdaptyUIOnboardingMeta, event: AdaptyOnboardingsAnalyticsEvent) -> Unit = { _, _ -> },
) {
    val coroutineScope = rememberCoroutineScope()

    val viewId = rememberSaveable(onboarding, externalUrlsPresentation) {
        onboarding.createNativePlatformViewId()
    }

    val currentOnDidFinishLoading by rememberUpdatedState(onDidFinishLoading)
    val currentOnDidFailWithError by rememberUpdatedState(onDidFailWithError)
    val currentOnCloseAction by rememberUpdatedState(onCloseAction)
    val currentOnPaywallAction by rememberUpdatedState(onPaywallAction)
    val currentOnCustomAction by rememberUpdatedState(onCustomAction)
    val currentOnStateUpdatedAction by rememberUpdatedState(onStateUpdatedAction)
    val currentOnAnalyticsEvent by rememberUpdatedState(onAnalyticsEvent)

    LaunchedEffect(viewId) {
        AdaptyUI.registerOnboardingEventsListener(
            object : AdaptyUIOnboardingsEventsObserver {
                override val mainUiScope: CoroutineScope = coroutineScope
                override fun onboardingViewDidFinishLoading(
                    view: AdaptyUIOnboardingView,
                    meta: AdaptyUIOnboardingMeta
                ) {
                    currentOnDidFinishLoading(meta)
                }

                override fun onboardingViewDidFailWithError(
                    view: AdaptyUIOnboardingView,
                    error: AdaptyError
                ) {
                    currentOnDidFailWithError(error)
                }

                override fun onboardingViewOnCloseAction(
                    view: AdaptyUIOnboardingView,
                    meta: AdaptyUIOnboardingMeta,
                    actionId: String
                ) {
                    currentOnCloseAction(meta, actionId)
                }

                override fun onboardingViewOnPaywallAction(
                    view: AdaptyUIOnboardingView,
                    meta: AdaptyUIOnboardingMeta,
                    actionId: String
                ) {
                    currentOnPaywallAction(meta, actionId)
                }

                override fun onboardingViewOnCustomAction(
                    view: AdaptyUIOnboardingView,
                    meta: AdaptyUIOnboardingMeta,
                    actionId: String
                ) {
                    currentOnCustomAction(meta, actionId)
                }

                override fun onboardingViewOnStateUpdatedAction(
                    view: AdaptyUIOnboardingView,
                    meta: AdaptyUIOnboardingMeta,
                    elementId: String,
                    params: AdaptyOnboardingsStateUpdatedParams
                ) {
                    currentOnStateUpdatedAction(meta, elementId, params)
                }

                override fun onboardingViewOnAnalyticsEvent(
                    view: AdaptyUIOnboardingView,
                    meta: AdaptyUIOnboardingMeta,
                    event: AdaptyOnboardingsAnalyticsEvent
                ) {
                    currentOnAnalyticsEvent(meta, event)
                }
            },
            viewId = viewId

        )
    }
    DisposableEffect(viewId) {
        onDispose {
            AdaptyUI.unregisterOnboardingEventsListener(viewId)
        }
    }

    AdaptyUIOnboardingPlatformView(
        onboarding = onboarding,
        viewId = viewId,
        externalUrlsPresentation = externalUrlsPresentation,
        modifier = modifier,
    )
}

@Composable
internal expect fun AdaptyUIOnboardingPlatformView(
    onboarding: AdaptyOnboarding,
    viewId: String,
    externalUrlsPresentation: AdaptyWebPresentation,
    modifier: Modifier = Modifier
)