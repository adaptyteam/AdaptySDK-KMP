package com.adapty.kmp.models

/**
 * Represents a product whose purchase was initiated from the App Store
 * (StoreKit 2 promoted purchase, cross_platform 4.1.2).
 *
 * Delivered through [com.adapty.kmp.OnPromotedPurchaseListener]. Pass the instance back to
 * [com.adapty.kmp.Adapty.makePromotedPurchase] to complete the purchase once your app is ready
 * for it (for example, after onboarding has finished).
 *
 * Unlike [AdaptyPaywallProduct] a promoted product carries no paywall context — the purchase was
 * started outside of any Adapty flow.
 *
 * @property vendorProductId Unique identifier of a product from the App Store Connect or Google Play Console.
 * @property localizedDescription Description of the product in the user's storefront language.
 * @property localizedTitle Title of the product in the user's storefront language.
 * @property isFamilyShareable A Boolean value that indicates whether the product is available
 * for family sharing in App Store Connect. iOS only.
 * @property regionCode Optional region code used to format the price.
 * @property price [AdaptyPrice] Main price information for the product.
 * @property subscription Optional subscription-specific details (intro offers, trials, etc.).
 * @property payloadData Internal optional custom payload data.
 */
public data class AdaptyPromotedProduct internal constructor(
    val vendorProductId: String,
    val localizedDescription: String,
    val localizedTitle: String,
    val isFamilyShareable: Boolean,
    val regionCode: String? = null,
    val price: AdaptyPrice,
    val subscription: AdaptyProductSubscription? = null,
    internal val payloadData: String? = null
)
