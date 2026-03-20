@file:OptIn(InternalAdaptyApi::class)
@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")

package com.adapty.kmp

import android.view.View
import com.adapty.internal.crossplatform.ui.OnboardingUiManager
import com.adapty.internal.utils.InternalAdaptyApi
import com.adapty.ui.onboardings.AdaptyOnboardingView

/**
 * A wrapper around a native Android [AdaptyOnboardingView] created by Adapty.
 *
 * Use this to embed an onboarding view in your Android view hierarchy (XML layouts,
 * Jetpack Compose via `AndroidView`, etc.) without depending on the `adapty-ui`
 * Compose Multiplatform module.
 *
 * **Important:** You must call [dispose] when the view is removed from the hierarchy
 * to unregister event listeners and release resources.
 *
 * @property view The native Android [AdaptyOnboardingView] that can be added to a layout.
 *
 * @see AdaptyUI.createNativeOnboardingView
 */
public class AdaptyNativeOnboardingView internal constructor(
    public val onboardingView: AdaptyOnboardingView,
    private val viewId: String,
    private val onboardingUiManager: OnboardingUiManager?,
) {

    public val view: View get() = onboardingView

    /**
     * Cleans up the native onboarding view by unregistering its event listener
     * and releasing native resources.
     *
     * Call this when the view is removed from the hierarchy (e.g., in `onDestroyView`).
     */
    public fun dispose() {
        AdaptyUI.unregisterOnboardingEventsListener(viewId)
        onboardingUiManager?.clearOnboardingView(onboardingView)
    }
}
