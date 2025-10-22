package com.adapty.kmp.models

/**
 * Represents a unique identifier for a product across platforms.
 *
 * @property vendorProductId The product identifier from the App Store or Google Play Console.
 * @property basePlanId The identifier of the base plan (Android only). Nullable.
 * @property adaptyProductId The internal Adapty product identifier.
 */
public data class AdaptyProductIdentifier internal constructor(
    val vendorProductId: String,
    val basePlanId: String?,
    internal val adaptyProductId: String
)