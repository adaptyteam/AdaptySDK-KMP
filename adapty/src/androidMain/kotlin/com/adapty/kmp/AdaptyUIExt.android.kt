@file:OptIn(AdaptyKMPInternal::class, InternalAdaptyApi::class)
@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE", "DEPRECATION")

package com.adapty.kmp

import android.content.Context
import androidx.lifecycle.ViewModelStoreOwner
import com.adapty.internal.crossplatform.ui.Dependencies.safeInject
import com.adapty.internal.crossplatform.ui.OnboardingUiManager
import com.adapty.internal.crossplatform.ui.FlowUiManager
import com.adapty.internal.utils.InternalAdaptyApi
import com.adapty.kmp.internal.AdaptyKMPInternal
import com.adapty.kmp.internal.plugin.request.createFlowViewRequestJsonString
import com.adapty.kmp.internal.plugin.request.createOnboardingViewRequestJsonString
import com.adapty.kmp.models.AdaptyCustomAsset
import com.adapty.kmp.models.AdaptyFlow
import com.adapty.kmp.models.AdaptyOnboarding
import com.adapty.kmp.models.AdaptyProductIdentifier
import com.adapty.kmp.models.AdaptyPurchaseParameters
import com.adapty.kmp.models.AdaptyWebPresentation
import com.adapty.ui.AdaptyFlowView
import com.adapty.ui.onboardings.AdaptyOnboardingView
import kotlinx.datetime.LocalDateTime

/**
 * Creates a native Android flow view (cross_platform 4.0.0) that can be embedded directly in an
 * Android view hierarchy without depending on the `adapty-ui` module.
 *
 * Call [AdaptyNativeFlowView.dispose] when the view is removed from the hierarchy.
 *
 * @param context The Android [Context] for creating the view.
 * @param viewModelStoreOwner A [ViewModelStoreOwner] (typically an Activity or Fragment).
 * @param flow The [AdaptyFlow] to display.
 * @param observer An [AdaptyUIFlowsEventsObserver] to receive flow lifecycle and interaction events.
 *
 * @return [AdaptyNativeFlowView] wrapping the native Android view.
 */
public fun AdaptyUI.createNativeFlowView(
    context: Context,
    viewModelStoreOwner: ViewModelStoreOwner?,
    flow: AdaptyFlow,
    observer: AdaptyUIFlowsEventsObserver,
    customTags: Map<String, String>? = null,
    customTimers: Map<String, LocalDateTime>? = null,
    customAssets: Map<String, AdaptyCustomAsset>? = null,
    productPurchaseParams: Map<AdaptyProductIdentifier, AdaptyPurchaseParameters>? = null,
): AdaptyNativeFlowView {
    val viewId = flow.createNativePlatformViewId()
    val flowUiManager: FlowUiManager? by safeInject<FlowUiManager>()

    registerFlowEventsListener(observer = observer, viewId = viewId)

    val flowView = AdaptyFlowView(context).apply {
        flowUiManager?.setupFlowView(
            flowView = this,
            viewModelStoreOwner = viewModelStoreOwner,
            args = createFlowViewRequestJsonString(
                flow = flow,
                customTags = customTags,
                customTimers = customTimers,
                customAssets = customAssets,
                productPurchaseParams = productPurchaseParams
            ),
            id = viewId,
        )
    }

    return AdaptyNativeFlowView(
        flowView = flowView,
        viewId = viewId,
        flowUiManager = flowUiManager
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
@Deprecated(
    "Onboarding is deprecated as of 4.0.0 and will be removed in a future release. Migrate to the Adapty Flow Builder.",
    level = DeprecationLevel.WARNING
)
public fun AdaptyUI.createNativeOnboardingView(
    context: Context,
    viewModelStoreOwner: ViewModelStoreOwner,
    onboarding: AdaptyOnboarding,
    observer: AdaptyUIOnboardingsEventsObserver,
    externalUrlsPresentation: AdaptyWebPresentation = AdaptyWebPresentation.IN_APP_BROWSER,
): AdaptyNativeOnboardingView {
    val viewId = onboarding.createNativePlatformViewId()
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
