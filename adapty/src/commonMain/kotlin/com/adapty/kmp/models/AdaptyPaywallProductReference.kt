package com.adapty.kmp.models


/**
 * Represents a reference to a product associated with a paywall.
 *
 * Contains minimal identifying information required to map
 * to a full product object, including platform-specific offer identifiers.
 *
 * @property vendorId Unique product identifier from App Store Connect or Google Play Console.
 * @property adaptyProductId Internal Adapty product identifier.
 * @property promotionalOfferId Optional promotional offer ID for iOS (e.g., introductory or promotional pricing).
 * @property winBackOfferId Optional win-back offer ID for iOS.
 * @property basePlanId Optional base plan ID for Android subscriptions.
 * @property offerId Optional offer ID for Android subscriptions.
 */
public data class AdaptyPaywallProductReference(
    val vendorId: String,
    internal val adaptyProductId: String,
    val productType: String,
    val accessLevelId: String,
    val promotionalOfferId: String? = null, // iOS Only
    val winBackOfferId: String? = null, // iOS Only
    val basePlanId: String? = null, // Android Only
    val offerId: String? = null, // Android Only
)