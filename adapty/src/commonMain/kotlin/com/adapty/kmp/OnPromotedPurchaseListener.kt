package com.adapty.kmp

import com.adapty.kmp.models.AdaptyPromotedProduct

/**
 * A listener interface for receiving StoreKit 2 promoted purchases (iOS only).
 *
 * Triggered when the user starts a purchase directly from the App Store product page. The
 * purchase is **not** completed for you — call [Adapty.makePromotedPurchase] with the received
 * product once your app is ready (for example, after onboarding has finished).
 *
 * Registering a listener is effectively required to support promoted purchases. With no listener registered
 * the promoted purchase is dropped (a warning is logged) and the user sees nothing happen after
 * tapping Buy on the App Store page.
 */
public fun interface OnPromotedPurchaseListener {
    /**
     * Called when a purchase was initiated from the App Store.
     *
     * @param product the promoted product the purchase was started for.
     */
    public fun onPromotedPurchaseReceived(product: AdaptyPromotedProduct)
}
