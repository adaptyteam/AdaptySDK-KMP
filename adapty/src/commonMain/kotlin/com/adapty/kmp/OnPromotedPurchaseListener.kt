package com.adapty.kmp

import com.adapty.kmp.models.AdaptyPromotedProduct

/**
 * A listener interface for receiving StoreKit 2 promoted purchases (iOS only).
 *
 * Triggered when the user starts a purchase directly from the App Store product page.
 * Register it to take control of *when* the purchase completes — call
 * [Adapty.makePromotedPurchase] with the received product once your app is ready
 * (for example, after onboarding has finished).
 *
 * If no listener is registered, the native SDK completes the purchase itself as soon as the
 * intent arrives.
 */
public fun interface OnPromotedPurchaseListener {
    /**
     * Called when a purchase was initiated from the App Store.
     *
     * @param product the promoted product the purchase was started for.
     */
    public fun onPromotedPurchaseReceived(product: AdaptyPromotedProduct)
}
