package com.adapty.kmp.models

public data class AdaptyPaywallProductSubscription internal constructor(
    val groupIdentifier: String? = null,
    val period: AdaptySubscriptionPeriod,
    val localizedPeriod: String? = null,
    val offer: AdaptySubscriptionOffer? = null,
    val renewalType: AdaptyRenewalType,
    val basePlanId: String? = null
)