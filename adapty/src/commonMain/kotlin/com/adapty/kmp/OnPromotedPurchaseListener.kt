package com.adapty.kmp

import com.adapty.kmp.models.AdaptyPromotedProduct

/**
 * A listener interface for receiving StoreKit 2 promoted purchases (iOS only).
 *
 * Triggered when the user starts a purchase directly from the App Store product page. The
 * purchase is **not** completed for you — call [Adapty.makePromotedPurchase] with the received
 * product once your app is ready (for example, after onboarding has finished).
 *
 * Registering a listener is effectively required to support promoted purchases.
 *
 * A promoted purchase typically cold-launches the app, so the intent can arrive before you get a
 * chance to register. One such purchase is held and delivered as soon as you register, rather
 * than being dropped; only the most recent is kept. Register as early as you can — a warning is
 * logged whenever a purchase has to be held.
 */
public fun interface OnPromotedPurchaseListener {
    /**
     * Called when a purchase was initiated from the App Store.
     *
     * @param product the promoted product the purchase was started for.
     */
    public fun onPromotedPurchaseReceived(product: AdaptyPromotedProduct)
}
