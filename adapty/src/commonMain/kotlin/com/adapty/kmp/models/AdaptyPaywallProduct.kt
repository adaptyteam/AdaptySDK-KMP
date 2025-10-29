package com.adapty.kmp.models


public data class AdaptyPaywallProduct internal constructor(
    val vendorProductId: String,
    internal val adaptyProductId: String,
    val paywallProductIndex: Int,
    val productType: String,
    val accessLevelId: String,
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


