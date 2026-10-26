package com.adapty.kmp.models

/**
 * Represents detailed subscription information for a product (paywall or promoted).
 *
 * Includes period, renewal type, and optional platform-specific identifiers and offers.
 *
 * @property groupIdentifier The subscription group ID that this subscription belongs to.
 *   iOS only. Will be `null` on iOS versions below 12.0 and macOS below 10.14.
 * @property period [AdaptySubscriptionPeriod] The subscription period details.
 * @property localizedPeriod Localized string representing the subscription period (e.g., "1 month").
 * @property pricingTerms [AdaptySubscriptionPricingTerms] Billing terms of the subscription: billing
 *   plan, price per billing period and the total commitment. iOS only (native iOS SDK 4.3+),
 *   `null` on Android. Commitment billing plans (e.g. [AdaptySubscriptionBillingPlan.MONTHLY]) are
 *   iOS 26.4+ only; on Android and on iOS below 26.4 the product is bought up front. The billing
 *   plan is sent back automatically when the product is purchased.
 * @property offer [AdaptySubscriptionOffer] Optional subscription offer details (introductory, promotional, etc.).
 * @property renewalType [AdaptyRenewalType] The type of subscription renewal (PREPAID, AUTORENEWABLE).
 * @property basePlanId The identifier of the base plan. Android only.
 */
public data class AdaptyProductSubscription internal constructor(
    val groupIdentifier: String? = null,
    val period: AdaptySubscriptionPeriod,
    val localizedPeriod: String? = null,
    val pricingTerms: AdaptySubscriptionPricingTerms? = null,
    val offer: AdaptySubscriptionOffer? = null,
    val renewalType: AdaptyRenewalType,
    val basePlanId: String? = null
)