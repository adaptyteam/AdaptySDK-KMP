package com.adapty.kmp

import com.adapty.kmp.models.AdaptyUIFlowView
import com.adapty.kmp.models.AdaptyUIPermission
import com.adapty.kmp.models.AdaptyUIPermissionResult

/**
 * Handles the requests a flow makes of the host app .
 *
 * Unlike [AdaptyUIFlowsEventsObserver], which reports events that have already happened, every
 * method here is a request the flow is waiting on an answer for. Register one with
 * [AdaptyUI.setSystemRequestsHandler] only if your flows use these features.
 */
public interface AdaptyUISystemRequestsHandler {

    /**
     * Called when a flow asks the host app to request an OS-level permission.
     *
     * Request the permission yourself and return the outcome — the flow stays blocked until you
     * do. There is deliberately no default: permissions must be declared in the app bundle, so the
     * SDK cannot request or grant them on your behalf. If no handler is registered at all, the SDK
     * sends no answer and the native side resolves the request as denied when the flow tears down.
     *
     * @param view the flow view the request came from.
     * @param permission the requested permission. Unknown, platform-specific and future
     *   identifiers arrive verbatim — read [AdaptyUIPermission.value] to handle them.
     * @param customArgs custom arguments attached to the request in the dashboard.
     */
    public suspend fun handlePermission(
        view: AdaptyUIFlowView,
        permission: AdaptyUIPermission,
        customArgs: Map<String, String>?,
    ): AdaptyUIPermissionResult

    /**
     * Called when a flow requests an in-app review.
     *
     * Defaults to triggering the native app-review flow via [AdaptyUI.requestAppReview]. Override
     * to run your own review prompt instead. When no handler is registered the SDK triggers the
     * native flow itself.
     *
     * @param view the flow view the request came from.
     */
    public suspend fun handleAppReviewRequest(view: AdaptyUIFlowView) {
        AdaptyUI.requestAppReview()
    }
}
