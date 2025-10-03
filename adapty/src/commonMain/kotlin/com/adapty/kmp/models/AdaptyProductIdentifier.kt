package com.adapty.kmp.models

public data class AdaptyProductIdentifier internal constructor(
    val vendorProductId: String,
    val basePlanId: String?,
    internal val adaptyProductId: String
)