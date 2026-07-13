package com.adapty.kmp.models

/**
 * A permission request surfaced by a flow (cross_platform 4.0.0).
 *
 * Call [answer] once the user has responded to the OS permission prompt so the flow can continue.
 *
 * @property permission the requested [AdaptyUIPermission].
 * @property customArgs optional custom arguments attached to the permission request.
 */
public class AdaptyUIPermissionRequest internal constructor(
    public val permission: AdaptyUIPermission,
    public val customArgs: Map<String, String>? = null,
    private val onAnswer: (granted: Boolean, detail: String?) -> Unit,
) {
    /**
     * Reports the user's response to the permission prompt back to the flow.
     *
     * @param granted whether the permission was granted.
     * @param detail optional platform-specific detail.
     */
    public fun answer(granted: Boolean, detail: String? = null) {
        onAnswer(granted, detail)
    }
}

/**
 * A handle for reporting the lifecycle of an observer-mode purchase initiated from a flow
 * (cross_platform 4.0.0). Call [reportStarted] before you begin the purchase and [reportFinished]
 * once it completes, so the flow can update its UI accordingly.
 */
public class AdaptyUIObserverPurchaseHandle internal constructor(
    private val onStart: () -> Unit,
    private val onFinish: () -> Unit,
) {
    public fun reportStarted() {
        onStart()
    }

    public fun reportFinished() {
        onFinish()
    }
}

/**
 * A handle for reporting the lifecycle of an observer-mode restore initiated from a flow
 * (cross_platform 4.0.0).
 */
public class AdaptyUIObserverRestoreHandle internal constructor(
    private val onStart: () -> Unit,
    private val onFinish: () -> Unit,
) {
    public fun reportStarted() {
        onStart()
    }

    public fun reportFinished() {
        onFinish()
    }
}
