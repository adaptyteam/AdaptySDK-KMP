package com.adapty.kmp.models

public data class AdaptySubscriptionOffer internal constructor(
    val offerIdentifier: AdaptySubscriptionOfferIdentifier,
    val phases: List<AdaptySubscriptionOfferPhase>,
    val offerTags: List<String>? = null
)