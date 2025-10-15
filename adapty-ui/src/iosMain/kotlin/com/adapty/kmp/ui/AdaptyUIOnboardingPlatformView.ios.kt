package com.adapty.kmp.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import androidx.compose.ui.viewinterop.UIKitViewController
import com.adapty.kmp.internal.AdaptyKMPInternal
import com.adapty.kmp.internal.plugin.AdaptyPluginEventHandler
import com.adapty.kmp.internal.plugin.request.asJsonString
import com.adapty.kmp.models.AdaptyOnboarding
import kotlinx.cinterop.ExperimentalForeignApi


@OptIn(AdaptyKMPInternal::class, ExperimentalForeignApi::class)
@Composable
internal actual fun AdaptyUIOnboardingPlatformView(
    onboarding: AdaptyOnboarding,
    modifier: Modifier,
) {
    val factory = remember { IosNativeViewFactory() }
    val view = remember(factory) {
        factory.createNativeOnboardingView(
            jsonString = onboarding.asJsonString(),
            id = onboarding.idForNativePlatformView,
            onEvent = { eventName, eventDataJsonString ->
                AdaptyPluginEventHandler.onNewEvent(
                    eventName = eventName,
                    eventDataJsonString = eventDataJsonString ?: ""
                )
            }
        )
    }
    UIKitViewController(
        modifier = modifier,
        update = {},
        factory = { view },
        properties = UIKitInteropProperties(
            isInteractive = true,
            isNativeAccessibilityEnabled = true
        )
    )
}