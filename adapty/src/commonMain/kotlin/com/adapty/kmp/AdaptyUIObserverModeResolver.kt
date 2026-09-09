package com.adapty.kmp

import com.adapty.kmp.models.AdaptyPaywallProduct
import com.adapty.kmp.models.AdaptyUIFlowView

/**
 * Drives purchases and restores started from a flow view in **observer mode**
 * (cross_platform 4.0.0).
 *
 * In observer mode Adapty does not perform the purchase — your app does, through its own purchase
 * API. Both methods here hand you a pair of callbacks to drive the flow view's loading state with,
 * so it can show and hide its indicator around your work. Register one with
 * [AdaptyUI.setObserverModeResolver] when you enable observer mode and present flow views; without
 * a resolver the flow view has no way to hand the purchase over and nothing happens when the user
 * taps buy.
 */
public interface AdaptyUIObserverModeResolver {

    /**
     * Called when the user starts a purchase from a flow view.
     *
     * Perform the purchase yourself, calling [onStartPurchase] before you begin and
     * [onFinishPurchase] once it settles — success or failure. The callbacks only drive the flow
     * view's loading state; they do not report the transaction to Adapty.
     *
     * @param view the flow view the purchase was started from.
     * @param product the product the user chose.
     * @param onStartPurchase notifies the flow view the purchase started.
     * @param onFinishPurchase notifies the flow view the purchase finished.
     */
    public fun observerModeDidInitiatePurchase(
        view: AdaptyUIFlowView,
        product: AdaptyPaywallProduct,
        onStartPurchase: () -> Unit,
        onFinishPurchase: () -> Unit,
    )

    /**
     * Called when the user starts a restore from a flow view.
     *
     * Perform the restore yourself, calling [onStartRestore] before you begin and [onFinishRestore]
     * once it settles. The callbacks only drive the flow view's loading state; report any restored
     * transactions to Adapty yourself.
     *
     * @param view the flow view the restore was started from.
     * @param onStartRestore notifies the flow view the restore started.
     * @param onFinishRestore notifies the flow view the restore finished.
     */
    public fun observerModeDidInitiateRestore(
        view: AdaptyUIFlowView,
        onStartRestore: () -> Unit,
        onFinishRestore: () -> Unit,
    )
}
