@file:OptIn(InternalAdaptyApi::class)
@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")

package com.adapty.kmp

import android.view.View
import com.adapty.internal.crossplatform.ui.FlowUiManager
import com.adapty.internal.utils.InternalAdaptyApi
import com.adapty.ui.AdaptyFlowView

/**
 * A wrapper around a native Android flow view created by Adapty (cross_platform 4.0.0).
 *
 * Use this to embed a flow view in your Android view hierarchy (XML layouts, Jetpack Compose
 * via `AndroidView`, etc.) without depending on the `adapty-ui` module.
 *
 * **Important:** Call [dispose] when the view is removed from the hierarchy to unregister
 * event listeners and release resources.
 *
 * @see AdaptyUI.createNativeFlowView
 */
public class AdaptyNativeFlowView internal constructor(
    internal val flowView: AdaptyFlowView,
    private val viewId: String,
    private val flowUiManager: FlowUiManager?,
) {

    public val view: View get() = flowView

    /**
     * Cleans up the native flow view by unregistering its event listener and releasing resources.
     */
    public fun dispose() {
        AdaptyUI.unregisterFlowEventsListener(viewId)
        flowUiManager?.clearFlowView(flowView)
    }
}
