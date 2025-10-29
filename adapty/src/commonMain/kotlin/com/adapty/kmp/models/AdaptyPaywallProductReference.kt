package com.adapty.kmp.models

public data class AdaptyPaywallProductReference(
    val vendorId: String,
    internal val adaptyProductId: String,
    val productType: String?,
    val accessLevelId: String?,
    val promotionalOfferId: String? = null, // iOS Only
    val winBackOfferId: String? = null, // iOS Only
    val basePlanId: String? = null, // Android Only
    val offerId: String? = null, // Android Only
)