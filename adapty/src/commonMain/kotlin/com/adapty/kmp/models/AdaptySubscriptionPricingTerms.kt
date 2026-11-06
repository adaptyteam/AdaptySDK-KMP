package com.adapty.kmp.models

/**
 * Billing terms of a subscription product: the billing plan, the price charged per billing
 * period, and the total commitment (cross_platform 4.3.1).
 *
 * iOS only (native iOS SDK 4.3+); never reported on Android. Commitment billing plans such as
 * [AdaptySubscriptionBillingPlan.MONTHLY] are iOS 26.4+ only (App Store commitment
 * subscriptions); on Android and on iOS below 26.4 the product is bought up front — where terms
 * are reported for such a product they describe the [AdaptySubscriptionBillingPlan.UP_FRONT] plan.
 *
 * The billing plan is sent back to the native SDK automatically when the product is purchased
 * with [com.adapty.kmp.Adapty.makePurchase] — no extra parameter is needed.
 *
 * @property billingPlan [AdaptySubscriptionBillingPlan] the billing plan of the subscription,
 * e.g. [AdaptySubscriptionBillingPlan.UP_FRONT] or [AdaptySubscriptionBillingPlan.MONTHLY].
 * @property billingPrice [AdaptyPrice] the price charged for each billing period.
 * @property billingPeriod [AdaptySubscriptionPeriod] the period between charges.
 * @property commitmentInfo [AdaptySubscriptionCommitmentInfo] the total price and period the user
 * commits to.
 */
@ConsistentCopyVisibility
public data class AdaptySubscriptionPricingTerms internal constructor(
    val billingPlan: AdaptySubscriptionBillingPlan,
    val billingPrice: AdaptyPrice,
    val billingPeriod: AdaptySubscriptionPeriod,
    val commitmentInfo: AdaptySubscriptionCommitmentInfo,
)

/**
 * Total price and period of a subscription commitment (cross_platform 4.3.1).
 *
 * iOS 26.4+ only (App Store commitment subscriptions). On Android, and on iOS below 26.4, no
 * commitment is reported and the product is bought up front.
 *
 * @property price [AdaptyPrice] the total price of the commitment.
 * @property period [AdaptySubscriptionPeriod] the total period of the commitment.
 */
@ConsistentCopyVisibility
public data class AdaptySubscriptionCommitmentInfo internal constructor(
    val price: AdaptyPrice,
    val period: AdaptySubscriptionPeriod,
)
