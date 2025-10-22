package com.adapty.kmp.models

/**
 * Represents the unique identifier and type of a subscription offer.
 *
 * @property id The unique identifier of the offer. Can be null if not assigned.
 * @property type The type of the subscription offer of [AdaptySubscriptionOfferType].
 */
public data class AdaptySubscriptionOfferIdentifier internal constructor(
    val id: String?,
    val type: AdaptySubscriptionOfferType
)