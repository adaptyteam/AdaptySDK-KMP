@file:OptIn(InternalAdaptyApi::class)
@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE", "DEPRECATION")

package com.adapty.kmp

import android.view.View
import com.adapty.internal.crossplatform.ui.FlowUiManager
import com.adapty.internal.utils.InternalAdaptyApi
import com.adapty.ui.AdaptyFlowView

/**
 * A wrapper around a native Android [AdaptyFlowView] created by Adapty.
 *
 * Use this to embed a paywall view in your Android view hierarchy (XML layouts,
 * Jetpack Compose via `AndroidView`, etc.) without depending on the `adapty-ui`
 * Compose Multiplatform module.
 *
 * **Important:** You must call [dispose] when the view is removed from the hierarchy
 * to unregister event listeners and release resources.
 *
 * @property flowView The native Android [AdaptyFlowView] that can be added to a layout.
 *
 * @see AdaptyUI.createNativePaywallView
 */
public class AdaptyNativePaywallView internal constructor(
    internal val flowView: AdaptyFlowView,
    private val viewId: String,
    private val flowUiManager: FlowUiManager?,
) {

    public val view: View get() = flowView

    /**
     * Cleans up the native paywall view by unregistering its event listener
     * and releasing native resources.
     *
     * Call this when the view is removed from the hierarchy (e.g., in `onDestroyView`).
     */
    public fun dispose() {
        AdaptyUI.unregisterPaywallEventsListener(viewId)
        flowUiManager?.clearFlowView(flowView)
    }
}
