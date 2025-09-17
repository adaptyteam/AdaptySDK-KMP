package com.adapty.kmp.models

public data class AdaptySubscriptionOfferPhase internal constructor(
    val price: AdaptyPrice,
    val numberOfPeriods: Int,
    val paymentMode: AdaptySubscriptionOfferPaymentMode,
    val subscriptionPeriod: AdaptySubscriptionPeriod,
    val localizedSubscriptionPeriod: String? = null,
    val localizedNumberOfPeriods: String? = null
)