@file:OptIn(AdaptyKMPInternal::class, ExperimentalForeignApi::class)

package com.adapty.kmp

import com.adapty.kmp.internal.AdaptyKMPInternal
import com.adapty.kmp.internal.plugin.AdaptyPluginEventHandler
import com.adapty.kmp.internal.plugin.request.createOnboardingViewRequestJsonString
import com.adapty.kmp.internal.plugin.request.createPaywallViewRequestJsonString
import com.adapty.kmp.models.AdaptyCustomAsset
import com.adapty.kmp.models.AdaptyOnboarding
import com.adapty.kmp.models.AdaptyPaywall
import com.adapty.kmp.models.AdaptyProductIdentifier
import com.adapty.kmp.models.AdaptyPurchaseParameters
import com.adapty.kmp.models.AdaptyWebPresentation
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.datetime.LocalDateTime
import platform.UIKit.UIViewController

/**
 * Creates a native iOS [UIViewController] containing a paywall view that can be
 * embedded directly in a UIKit or SwiftUI view hierarchy.
 *
 * This allows using Adapty paywalls without depending on the `adapty-ui`
 * Compose Multiplatform module.
 *
 * **Important:**
 * - You must call [AdaptyNativePaywallView.dispose] when the view is removed from the
 *   hierarchy to prevent memory leaks.
 *
 * Example:
 * ```
 * val nativeView = AdaptyUI.createNativePaywallView(
 *     paywall = paywall,
 *     observer = myPaywallObserver,
 *     customTags = mapOf("username" to "John")
 * )
 * // Use nativeView.viewController in UIKit: addChild(nativeView.viewController)
 * // When done:
 * nativeView.dispose()
 * ```
 *
 * @param paywall The [AdaptyPaywall] to display.
 * @param observer An [AdaptyUIPaywallsEventsObserver] to receive paywall lifecycle and interaction events.
 * @param customTags Optional custom tags to inject into the paywall.
 * @param customTimers Optional custom timers to pass for paywall rendering.
 * @param customAssets Optional map of asset identifiers to custom assets.
 * @param productPurchaseParams Optional parameters for product purchase flow.
 *
 * @return [AdaptyNativePaywallView] wrapping the native [UIViewController].
 *
 * @see AdaptyNativePaywallView
 * @see AdaptyUIPaywallsEventsObserver
 */
public fun AdaptyUI.createNativePaywallView(
    paywall: AdaptyPaywall,
    observer: AdaptyUIPaywallsEventsObserver,
    customTags: Map<String, String>? = null,
    customTimers: Map<String, LocalDateTime>? = null,
    customAssets: Map<String, AdaptyCustomAsset>? = null,
    productPurchaseParams: Map<AdaptyProductIdentifier, AdaptyPurchaseParameters>? = null,
): AdaptyNativePaywallView {
    val viewId = paywall.idForNativePlatformView
    val jsonString = createPaywallViewRequestJsonString(
        paywall = paywall,
        customTags = customTags,
        customTimers = customTimers,
        customAssets = customAssets,
        productPurchaseParams = productPurchaseParams
    )

    registerPaywallEventsListener(observer = observer, viewId = viewId)

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

    return AdaptyNativePaywallView(
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
public fun AdaptyUI.createNativeOnboardingView(
    onboarding: AdaptyOnboarding,
    observer: AdaptyUIOnboardingsEventsObserver,
    externalUrlsPresentation: AdaptyWebPresentation = AdaptyWebPresentation.IN_APP_BROWSER,
): AdaptyNativeOnboardingView {
    val viewId = onboarding.idForNativePlatformView
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
