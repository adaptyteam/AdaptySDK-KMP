package com.adapty.kmp.models


/**
 * Internal reference to a product associated with a paywall, as received from the native SDK.
 *
 * Not part of the public API: apps use [AdaptyFlowPaywall.productIdentifiers] / [AdaptyProductIdentifier] instead.
 */
internal data class AdaptyPaywallProductReference(
    val vendorId: String,
    internal val adaptyProductId: String,
    val productType: String,
    val accessLevelId: String,
    val promotionalOfferId: String? = null, // iOS Only
    val winBackOfferId: String? = null, // iOS Only
    val basePlanId: String? = null, // Android Only
    val offerId: String? = null, // Android Only
    val flowProductId: String? = null,
)