package com.adapty.kmp

import com.adapty.kmp.models.AdaptyError
import com.adapty.kmp.models.AdaptyPaywallProduct
import com.adapty.kmp.models.AdaptyProfile
import com.adapty.kmp.models.AdaptyPurchaseResult
import com.adapty.kmp.models.AdaptyUIAction
import com.adapty.kmp.models.AdaptyUIFlowView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * Observes lifecycle events and user interactions within an [AdaptyUIFlowView]
 *
 * Compared to the legacy [AdaptyUIPaywallsEventsObserver], the default behavior follows the
 * cross-platform 4.0.0 contract:
 * - The close button dismisses the view, but the Android **system back** does **not** — override
 *   [flowViewDidPerformAction] and dismiss explicitly to restore the old behavior.
 * - A **successful purchase** no longer auto-dismisses the view.
 * - A **successful restore** no longer auto-dismisses the view.
 * - [AdaptyUIAction.OpenUrlAction] opens the URL natively.
 *
 * Override individual methods to customize behavior.
 */
public interface AdaptyUIFlowsEventsObserver {

    /**
     * The main [CoroutineScope] used for UI operations such as dismissing the view.
     */
    public val mainUiScope: CoroutineScope
        get() = MainScope()

    /**
     * Called when the user performs an action on the flow view.
     *
     * By default:
     * - [AdaptyUIAction.CloseAction] dismisses the view.
     * - [AdaptyUIAction.AndroidSystemBackAction] keeps the view open (4.0.0 default).
     * - [AdaptyUIAction.OpenUrlAction] opens the provided URL natively.
     */
    public fun flowViewDidPerformAction(view: AdaptyUIFlowView, action: AdaptyUIAction) {
        when (action) {
            is AdaptyUIAction.CloseAction -> mainUiScope.launch { view.dismiss() }
            is AdaptyUIAction.AndroidSystemBackAction -> Unit
            is AdaptyUIAction.OpenUrlAction -> AdaptyUI.openWebUrl(action.url, action.openIn)
            is AdaptyUIAction.CustomAction -> Unit
        }
    }

    /** Called when the flow view has been presented. */
    public fun flowViewDidAppear(view: AdaptyUIFlowView) {}

    /** Called when the flow view has been dismissed. */
    public fun flowViewDidDisappear(view: AdaptyUIFlowView) {}

    /** Called when a product is selected, either by the user or automatically. */
    public fun flowViewDidSelectProduct(view: AdaptyUIFlowView, productId: String) {}

    /** Called when the user initiates a purchase. */
    public fun flowViewDidStartPurchase(view: AdaptyUIFlowView, product: AdaptyPaywallProduct) {}

    /**
     * Called when a purchase completes or is canceled.
     *
     * By default the view is **not** dismissed (4.0.0 behavior). To restore the old behavior,
     * override and dismiss when `purchaseResult !is AdaptyPurchaseResult.UserCanceled`.
     */
    public fun flowViewDidFinishPurchase(
        view: AdaptyUIFlowView,
        product: AdaptyPaywallProduct,
        purchaseResult: AdaptyPurchaseResult
    ) {
    }

    /** Called when a purchase attempt fails. */
    public fun flowViewDidFailPurchase(
        view: AdaptyUIFlowView,
        product: AdaptyPaywallProduct,
        error: AdaptyError
    ) {
    }

    /** Called when the user initiates a restore. */
    public fun flowViewDidStartRestore(view: AdaptyUIFlowView) {}

    /**
     * Called when a restore completes successfully.
     *
     * By default the view is **not** dismissed (4.0.0 behavior). Inspect [profile] and dismiss
     * explicitly if the user now has access.
     */
    public fun flowViewDidFinishRestore(view: AdaptyUIFlowView, profile: AdaptyProfile) {}

    /** Called when a restore fails. */
    public fun flowViewDidFailRestore(view: AdaptyUIFlowView, error: AdaptyError) {}

    /**
     * Called when an error occurs in the flow (rendering, loading, etc.).
     * Replaces the legacy `paywallViewDidFailRendering`.
     *
     * By default (4.0.0) the view is dismissed on error. Override to keep it open.
     */
    public fun flowViewDidReceiveError(view: AdaptyUIFlowView, error: AdaptyError) {
        mainUiScope.launch { view.dismiss() }
    }

    /** Called when an error occurs while loading products for the flow. */
    public fun flowViewDidFailLoadingProducts(view: AdaptyUIFlowView, error: AdaptyError) {}

    /** Called when web payment navigation completes. */
    public fun flowViewDidFinishWebPaymentNavigation(
        view: AdaptyUIFlowView,
        product: AdaptyPaywallProduct? = null,
        error: AdaptyError? = null
    ) {
    }

    /**
     * Called when the flow reports an analytics event.
     *
     * @param name the analytics event name.
     * @param paramsJsonString the event parameters as a raw JSON object string (may be empty).
     */
    public fun flowViewDidReceiveAnalyticEvent(
        view: AdaptyUIFlowView,
        name: String,
        paramsJsonString: String
    ) {
    }
}
