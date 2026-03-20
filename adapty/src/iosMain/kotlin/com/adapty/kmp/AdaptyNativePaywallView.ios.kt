package com.adapty.kmp

import platform.UIKit.UIViewController

/**
 * A wrapper around a native iOS paywall [UIViewController] created by Adapty.
 *
 * Use this to embed a paywall view in your UIKit or SwiftUI view hierarchy
 * without depending on the `adapty-ui` Compose Multiplatform module.
 *
 * **Important:** You must call [dispose] when the view is removed from the hierarchy
 * to unregister event listeners and prevent memory leaks.
 *
 * @property viewController The native iOS [UIViewController] containing the paywall.
 *
 * @see AdaptyUI.createNativePaywallView
 */
public class AdaptyNativePaywallView internal constructor(
    public val viewController: UIViewController,
    private val viewId: String,
) {
    /**
     * Cleans up the native paywall view by unregistering its event listener.
     *
     * Call this when the view is removed from the hierarchy (e.g., on dismiss or dealloc).
     */
    public fun dispose() {
        AdaptyUI.unregisterPaywallEventsListener(viewId)
    }
}
