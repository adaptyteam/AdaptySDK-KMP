@file:OptIn(AdaptyKMPInternal::class, InternalAdaptyApi::class)
@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")

package com.adapty.kmp

import android.content.Context
import androidx.lifecycle.ViewModelStoreOwner
import com.adapty.internal.crossplatform.ui.Dependencies.safeInject
import com.adapty.internal.crossplatform.ui.OnboardingUiManager
import com.adapty.internal.crossplatform.ui.PaywallUiManager
import com.adapty.internal.utils.InternalAdaptyApi
import com.adapty.kmp.internal.AdaptyKMPInternal
import com.adapty.kmp.internal.plugin.request.createOnboardingViewRequestJsonString
import com.adapty.kmp.internal.plugin.request.createPaywallViewRequestJsonString
import com.adapty.kmp.models.AdaptyCustomAsset
import com.adapty.kmp.models.AdaptyOnboarding
import com.adapty.kmp.models.AdaptyPaywall
import com.adapty.kmp.models.AdaptyProductIdentifier
import com.adapty.kmp.models.AdaptyPurchaseParameters
import com.adapty.kmp.models.AdaptyWebPresentation
import com.adapty.ui.AdaptyPaywallView
import com.adapty.ui.onboardings.AdaptyOnboardingView
import kotlinx.datetime.LocalDateTime

/**
 * Creates a native Android [AdaptyPaywallView] that can be embedded directly in an
 * Android view hierarchy (XML layouts, Jetpack Compose via `AndroidView`, etc.)
 * without depending on the `adapty-ui` Compose Multiplatform module.
 *
 * **Important:**
 * - You must call [AdaptyNativePaywallView.dispose] when the view is removed from the
 *   hierarchy (e.g., in `onDestroyView`) to prevent memory leaks and release resources.
 *
 * Example:
 * ```
 * val nativeView = AdaptyUI.createNativePaywallView(
 *     context = requireContext(),
 *     viewModelStoreOwner = this,
 *     paywall = paywall,
 *     observer = myPaywallObserver,
 *     customTags = mapOf("username" to "John")
 * )
 * // Add to layout: container.addView(nativeView.view)
 * // When done (e.g., onDestroyView):
 * nativeView.dispose()
 * ```
 *
 * @param context The Android [Context] for creating the view.
 * @param viewModelStoreOwner A [ViewModelStoreOwner] (typically an Activity or Fragment).
 * @param paywall The [AdaptyPaywall] to display.
 * @param observer An [AdaptyUIPaywallsEventsObserver] to receive paywall lifecycle and interaction events.
 * @param customTags Optional custom tags to inject into the paywall.
 * @param customTimers Optional custom timers to pass for paywall rendering.
 * @param customAssets Optional map of asset identifiers to custom assets.
 * @param productPurchaseParams Optional parameters for product purchase flow.
 *
 * @return [AdaptyNativePaywallView] wrapping the native Android view.
 *
 * @see AdaptyNativePaywallView
 * @see AdaptyUIPaywallsEventsObserver
 */
public fun AdaptyUI.createNativePaywallView(
    context: Context,
    viewModelStoreOwner: ViewModelStoreOwner?,
    paywall: AdaptyPaywall,
    observer: AdaptyUIPaywallsEventsObserver,
    customTags: Map<String, String>? = null,
    customTimers: Map<String, LocalDateTime>? = null,
    customAssets: Map<String, AdaptyCustomAsset>? = null,
    productPurchaseParams: Map<AdaptyProductIdentifier, AdaptyPurchaseParameters>? = null,
): AdaptyNativePaywallView {
    val viewId = paywall.idForNativePlatformView
    val paywallUiManager: PaywallUiManager? by safeInject<PaywallUiManager>()

    registerPaywallEventsListener(observer = observer, viewId = viewId)

    val paywallView = AdaptyPaywallView(context).apply {
        paywallUiManager?.setupPaywallView(
            paywallView = this,
            viewModelStoreOwner = viewModelStoreOwner,
            args = createPaywallViewRequestJsonString(
                paywall = paywall,
                customTags = customTags,
                customTimers = customTimers,
                customAssets = customAssets,
                productPurchaseParams = productPurchaseParams
            ),
            id = viewId,
        )
    }

    return AdaptyNativePaywallView(
        paywallView = paywallView,
        viewId = viewId,
        paywallUiManager = paywallUiManager
    )
}

/**
 * Creates a native Android [AdaptyOnboardingView] that can be embedded directly in an
 * Android view hierarchy (XML layouts, Jetpack Compose via `AndroidView`, etc.)
 * without depending on the `adapty-ui` Compose Multiplatform module.
 *
 * **Important:**
 * - You must call [AdaptyNativeOnboardingView.dispose] when the view is removed from the
 *   hierarchy (e.g., in `onDestroyView`) to prevent memory leaks and release resources.
 *
 * Example:
 * ```
 * val nativeView = AdaptyUI.createNativeOnboardingView(
 *     context = requireContext(),
 *     viewModelStoreOwner = this,
 *     onboarding = onboarding,
 *     observer = myOnboardingObserver
 * )
 * // Add to layout: container.addView(nativeView.view)
 * // When done (e.g., onDestroyView):
 * nativeView.dispose()
 * ```
 *
 * @param context The Android [Context] for creating the view.
 * @param viewModelStoreOwner A [ViewModelStoreOwner] (typically an Activity or Fragment).
 * @param onboarding The [AdaptyOnboarding] to display.
 * @param observer An [AdaptyUIOnboardingsEventsObserver] to receive onboarding lifecycle and interaction events.
 * @param externalUrlsPresentation Specifies how external URLs should be presented.
 *   Defaults to [AdaptyWebPresentation.IN_APP_BROWSER].
 *
 * @return [AdaptyNativeOnboardingView] wrapping the native Android view.
 *
 * @see AdaptyNativeOnboardingView
 * @see AdaptyUIOnboardingsEventsObserver
 */
public fun AdaptyUI.createNativeOnboardingView(
    context: Context,
    viewModelStoreOwner: ViewModelStoreOwner,
    onboarding: AdaptyOnboarding,
    observer: AdaptyUIOnboardingsEventsObserver,
    externalUrlsPresentation: AdaptyWebPresentation = AdaptyWebPresentation.IN_APP_BROWSER,
): AdaptyNativeOnboardingView {
    val viewId = onboarding.idForNativePlatformView
    val onboardingUiManager: OnboardingUiManager? by safeInject<OnboardingUiManager>()

    registerOnboardingEventsListener(observer = observer, viewId = viewId)

    val onboardingView = AdaptyOnboardingView(context).apply {
        onboardingUiManager?.setupOnboardingView(
            onboardingView = this,
            viewModelStoreOwner = viewModelStoreOwner,
            args = createOnboardingViewRequestJsonString(
                onboarding = onboarding,
                externalUrlsPresentation = externalUrlsPresentation
            ),
            id = viewId,
        )
    }

    return AdaptyNativeOnboardingView(
        onboardingView = onboardingView,
        viewId = viewId,
        onboardingUiManager = onboardingUiManager
    )
}
