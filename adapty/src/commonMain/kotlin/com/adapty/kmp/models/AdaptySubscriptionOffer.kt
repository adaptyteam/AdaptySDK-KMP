package com.adapty.kmp.models

/**
 * Represents an offer for a subscription product.
 *
 * @property offerIdentifier Unique identifier for the subscription offer [AdaptySubscriptionOfferIdentifier].
 * @property phases A list of offer phases [AdaptySubscriptionOfferPhase]
 * @property offerTags Optional tags assigned to the offer, can be used for internal organization or filtering.
 */
public data class AdaptySubscriptionOffer internal constructor(
    val offerIdentifier: AdaptySubscriptionOfferIdentifier,
    val phases: List<AdaptySubscriptionOfferPhase>,
    val offerTags: List<String>? = null
)