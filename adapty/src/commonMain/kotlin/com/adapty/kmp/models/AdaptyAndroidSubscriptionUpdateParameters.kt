package com.adapty.kmp.models

public data class AdaptyAndroidSubscriptionUpdateParameters(
    val oldSubVendorProductId: String,
    val replacementMode: AdaptyAndroidSubscriptionUpdateReplacementMode,
)