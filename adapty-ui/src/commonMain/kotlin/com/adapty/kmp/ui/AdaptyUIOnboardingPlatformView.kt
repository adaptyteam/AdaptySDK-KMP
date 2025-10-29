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