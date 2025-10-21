package com.adapty.kmp.models

/**
 * Represents detailed subscription information for a paywall product.
 *
 * Includes period, renewal type, and optional platform-specific identifiers and offers.
 *
 * @property groupIdentifier The subscription group ID that this subscription belongs to.
 *   iOS only. Will be `null` on iOS versions below 12.0 and macOS below 10.14.
 * @property period [AdaptySubscriptionPeriod] The subscription period details.
 * @property localizedPeriod Localized string representing the subscription period (e.g., "1 month").
 * @property offer [AdaptySubscriptionOffer] Optional subscription offer details (introductory, promotional, etc.).
 * @property renewalType [AdaptyRenewalType] The type of subscription renewal (PREPAID, AUTORENEWABLE).
 * @property basePlanId The identifier of the base plan. Android only.
 */
public data class AdaptyPaywallProductSubscription internal constructor(
    val groupIdentifier: String? = null,
    val period: AdaptySubscriptionPeriod,
    val localizedPeriod: String? = null,
    val offer: AdaptySubscriptionOffer? = null,
    val renewalType: AdaptyRenewalType,
    val basePlanId: String? = null
)