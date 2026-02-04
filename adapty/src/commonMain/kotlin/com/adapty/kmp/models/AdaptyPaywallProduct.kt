package com.adapty.kmp.models


/**
 * Represents a product displayed on a paywall.
 *
 * Contains information about the product itself, its pricing,
 * subscription details (if applicable), and its relation to the paywall.
 *
 * @property vendorProductId Unique identifier of a product from the App Store Connect or Google Play Console.
 * @property adaptyProductId Internal Adapty product identifier.
 * @property paywallProductIndex Index of the product in the paywall.
 * @property paywallVariationId Variation ID of the parent paywall.
 * @property paywallABTestName A/B test name of the parent paywall.
 * @property paywallName Name of the parent paywall.
 * @property audienceName Optional audience name for targeted paywalls.
 * @property localizedDescription Description of the product in the user's storefront language.
 * The description's language is determined by the storefront that the user's device is connected to,
 * not the preferred language set on the device.
 * @property localizedTitle Title of the product in the user's storefront language.
 * The title's language is determined by the storefront that the user's device is connected to,
 * not the preferred language set on the device.
 * @property isFamilyShareable A Boolean value that indicates whether the product is available
 * for family sharing in App Store Connect. (Will be `false` for iOS version below 14.0 and macOS version below 11.0)
 * @property regionCode Optional region code used to format the price.
 * @property price [AdaptyPrice] Main price information for the product.
 * @property subscription Optional subscription-specific details (intro offers, trials, etc.).
 * @property payloadData Internal Optional custom payload data.
 */
public data class AdaptyPaywallProduct internal constructor(
    val vendorProductId: String,
    internal val adaptyProductId: String,
    val paywallProductIndex: Int,
    val productType: String?,
    val accessLevelId: String?,
    val paywallVariationId: String,
    val paywallABTestName: String,
    val paywallName: String,
    val audienceName: String? = null,
    val localizedDescription: String,
    val localizedTitle: String,
    val isFamilyShareable: Boolean,
    val regionCode: String? = null,
    val price: AdaptyPrice,
    val subscription: AdaptyPaywallProductSubscription? = null,
    internal val payloadData: String? = null
)


