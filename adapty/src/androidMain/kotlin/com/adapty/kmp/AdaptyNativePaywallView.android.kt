@file:OptIn(InternalAdaptyApi::class)
@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")

package com.adapty.kmp

import android.view.View
import com.adapty.internal.crossplatform.ui.PaywallUiManager
import com.adapty.internal.utils.InternalAdaptyApi
import com.adapty.ui.AdaptyPaywallView

/**
 * A wrapper around a native Android [AdaptyPaywallView] created by Adapty.
 *
 * Use this to embed a paywall view in your Android view hierarchy (XML layouts,
 * Jetpack Compose via `AndroidView`, etc.) without depending on the `adapty-ui`
 * Compose Multiplatform module.
 *
 * **Important:** You must call [dispose] when the view is removed from the hierarchy
 * to unregister event listeners and release resources.
 *
 * @property paywallView The native Android [AdaptyPaywallView] that can be added to a layout.
 *
 * @see AdaptyUI.createNativePaywallView
 */
public class AdaptyNativePaywallView internal constructor(
    internal val paywallView: AdaptyPaywallView,
    private val viewId: String,
    private val paywallUiManager: PaywallUiManager?,
) {

    public val view: View get() = paywallView

    /**
     * Cleans up the native paywall view by unregistering its event listener
     * and releasing native resources.
     *
     * Call this when the view is removed from the hierarchy (e.g., in `onDestroyView`).
     */
    public fun dispose() {
        AdaptyUI.unregisterPaywallEventsListener(viewId)
        paywallUiManager?.clearPaywallView(paywallView)
    }
}
