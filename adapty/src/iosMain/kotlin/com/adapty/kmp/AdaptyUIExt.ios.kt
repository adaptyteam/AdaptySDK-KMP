@file:OptIn(AdaptyKMPInternal::class, ExperimentalForeignApi::class)
@file:Suppress("DEPRECATION") // deprecated native paywall/onboarding ext bridge deprecated APIs

package com.adapty.kmp

import com.adapty.kmp.internal.AdaptyKMPInternal
import com.adapty.kmp.internal.plugin.AdaptyPluginEventHandler
import com.adapty.kmp.internal.plugin.request.createFlowViewRequestJsonString
import com.adapty.kmp.internal.plugin.request.createOnboardingViewRequestJsonString
import com.adapty.kmp.models.AdaptyCustomAsset
import com.adapty.kmp.models.AdaptyFlow
import com.adapty.kmp.models.AdaptyOnboarding
import com.adapty.kmp.models.AdaptyProductIdentifier
import com.adapty.kmp.models.AdaptyPurchaseParameters
import com.adapty.kmp.models.AdaptyWebPresentation
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.datetime.LocalDateTime
import platform.UIKit.UIViewController

/**
 * Creates a native iOS [UIViewController] containing a flow view (cross_platform 4.0.0) that can
 * be embedded directly in a UIKit or SwiftUI view hierarchy, without depending on the `adapty-ui`
 * module.
 *
 * Call [AdaptyNativeFlowView.dispose] when the view is removed from the hierarchy.
 *
 * @param flow The [AdaptyFlow] to display.
 * @param observer An [AdaptyUIFlowsEventsObserver] to receive flow lifecycle and interaction events.
 *
 * @return [AdaptyNativeFlowView] wrapping the native [UIViewController].
 */
public fun AdaptyUI.createNativeFlowView(
    flow: AdaptyFlow,
    observer: AdaptyUIFlowsEventsObserver,
    customTags: Map<String, String>? = null,
    customTimers: Map<String, LocalDateTime>? = null,
    customAssets: Map<String, AdaptyCustomAsset>? = null,
    productPurchaseParams: Map<AdaptyProductIdentifier, AdaptyPurchaseParameters>? = null,
): AdaptyNativeFlowView {
    val viewId = flow.createNativePlatformViewId()
    val jsonString = createFlowViewRequestJsonString(
        flow = flow,
        customTags = customTags,
        customTimers = customTimers,
        customAssets = customAssets,
        productPurchaseParams = productPurchaseParams
    )

    registerFlowEventsListener(observer = observer, viewId = viewId)

    val viewController = AdaptySwiftBridge.createNativePaywallViewWithJsonString(
        jsonString = jsonString,
        id = viewId,
        onEvent = { eventName, eventDataJsonString ->
            AdaptyPluginEventHandler.onNewEvent(
                eventName = eventName,
                eventDataJsonString = eventDataJsonString ?: ""
            )
        }
    ) as UIViewController

    return AdaptyNativeFlowView(
        viewController = viewController,
        viewId = viewId
    )
}

/**
 * Creates a native iOS [UIViewController] containing an onboarding view that can be
 * embedded directly in a UIKit or SwiftUI view hierarchy.
 *
 * This allows using Adapty onboardings without depending on the `adapty-ui`
 * Compose Multiplatform module.
 *
 * - You must call [AdaptyNativeOnboardingView.dispose] when the view is removed from the
 *   hierarchy to prevent memory leaks.
 *
 * Example:
 * ```
 * val nativeView = AdaptyUI.createNativeOnboardingView(
 *     onboarding = onboarding,
 *     observer = myOnboardingObserver
 * )
 * // Use nativeView.viewController in UIKit: addChild(nativeView.viewController)
 * // When done:
 * nativeView.dispose()
 * ```
 *
 * @param onboarding The [AdaptyOnboarding] to display.
 * @param observer An [AdaptyUIOnboardingsEventsObserver] to receive onboarding lifecycle and interaction events.
 * @param externalUrlsPresentation Specifies how external URLs should be presented.
 *   Defaults to [AdaptyWebPresentation.IN_APP_BROWSER].
 *
 * @return [AdaptyNativeOnboardingView] wrapping the native [UIViewController].
 *
 * @see AdaptyNativeOnboardingView
 * @see AdaptyUIOnboardingsEventsObserver
 */
@Deprecated(
    "Onboarding is deprecated as of 4.0.0 and will be removed in a future release. Migrate to the Adapty Flow Builder.",
    level = DeprecationLevel.WARNING
)
public fun AdaptyUI.createNativeOnboardingView(
    onboarding: AdaptyOnboarding,
    observer: AdaptyUIOnboardingsEventsObserver,
    externalUrlsPresentation: AdaptyWebPresentation = AdaptyWebPresentation.IN_APP_BROWSER,
): AdaptyNativeOnboardingView {
    val viewId = onboarding.createNativePlatformViewId()
    val jsonString = createOnboardingViewRequestJsonString(
        onboarding = onboarding,
        externalUrlsPresentation = externalUrlsPresentation
    )

    registerOnboardingEventsListener(observer = observer, viewId = viewId)

    val viewController = AdaptySwiftBridge.createNativeOnboardingViewWithJsonString(
        jsonString = jsonString,
        id = viewId,
        onEvent = { eventName, eventDataJsonString ->
            AdaptyPluginEventHandler.onNewEvent(
                eventName = eventName,
                eventDataJsonString = eventDataJsonString ?: ""
            )
        }
    ) as UIViewController

    return AdaptyNativeOnboardingView(
        viewController = viewController,
        viewId = viewId
    )
}
