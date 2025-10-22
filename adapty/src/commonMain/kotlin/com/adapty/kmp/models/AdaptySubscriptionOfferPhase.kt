package com.adapty.kmp.models

/**
 * Represents a single phase of a subscription offer, such as an introductory or promotional phase.
 *
 * @property price The price for this phase [AdaptyPrice].
 * @property numberOfPeriods The number of subscription periods this phase lasts.
 * @property paymentMode The payment mode for this phase [AdaptySubscriptionOfferPaymentMode] (e.g., pay-as-you-go, pay upfront, free trial).
 * @property subscriptionPeriod The period unit for this phase [AdaptySubscriptionPeriod] (day, week, month, year).
 * @property localizedSubscriptionPeriod Optional localized string of the subscription period for the user's locale.
 * @property localizedNumberOfPeriods Optional localized string of the number of periods for the user's locale.
 */
public data class AdaptySubscriptionOfferPhase internal constructor(
    val price: AdaptyPrice,
    val numberOfPeriods: Int,
    val paymentMode: AdaptySubscriptionOfferPaymentMode,
    val subscriptionPeriod: AdaptySubscriptionPeriod,
    val localizedSubscriptionPeriod: String? = null,
    val localizedNumberOfPeriods: String? = null
)