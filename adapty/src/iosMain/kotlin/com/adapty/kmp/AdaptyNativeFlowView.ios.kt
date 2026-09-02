package com.adapty.kmp

import platform.UIKit.UIViewController

/**
 * A wrapper around a native iOS flow [UIViewController] created by Adapty (cross_platform 4.0.0).
 *
 * Use this to embed a flow view in your UIKit or SwiftUI view hierarchy without depending on the
 * `adapty-ui` module.
 *
 * **Important:** Call [dispose] when the view is removed from the hierarchy to unregister event
 * listeners and prevent memory leaks.
 *
 * @property viewController The native iOS [UIViewController] containing the flow.
 *
 * @see AdaptyUI.createNativeFlowView
 */
public class AdaptyNativeFlowView internal constructor(
    public val viewController: UIViewController,
    private val viewId: String,
) {
    /**
     * Cleans up the native flow view by unregistering its event listener.
     */
    public fun dispose() {
        AdaptyUI.unregisterFlowEventsListener(viewId)
    }
}
