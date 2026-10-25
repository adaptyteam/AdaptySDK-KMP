package com.adapty.kmp.models

/**
 * Billing plan of a subscription (cross_platform 4.3.1).
 *
 * Billing plans are reported for App Store products and purchases only (native iOS SDK 4.3+),
 * never on Android. Commitment plans such as [MONTHLY] come from App Store commitment
 * subscriptions and are iOS 26.4+ only; on Android and on iOS below 26.4 the subscription is
 * bought up front.
 *
 * The known plans are exposed as constants below, but any [value] is valid: plans added by future
 * StoreKit or native SDK releases arrive unchanged rather than collapsing into an "unknown"
 * constant. Compare against the constants, and fall back to [value] for anything else:
 *
 * ```
 * when (pricingTerms.billingPlan) {
 *     AdaptySubscriptionBillingPlan.UP_FRONT -> showFullPrice()
 *     AdaptySubscriptionBillingPlan.MONTHLY -> showMonthlyInstallments()
 *     else -> showGenericPrice(pricingTerms.billingPlan.value)
 * }
 * ```
 *
 * @property value the raw billing plan identifier as sent by the native SDK (`billing_plan_id`).
 */
public class AdaptySubscriptionBillingPlan(public val value: String) {

    public companion object {
        /** The whole subscription period is charged at once. */
        public val UP_FRONT: AdaptySubscriptionBillingPlan = AdaptySubscriptionBillingPlan("up_front")

        /** The commitment is charged in monthly installments. */
        public val MONTHLY: AdaptySubscriptionBillingPlan = AdaptySubscriptionBillingPlan("monthly")
    }

    override fun equals(other: Any?): Boolean =
        this === other || (other is AdaptySubscriptionBillingPlan && other.value == value)

    override fun hashCode(): Int = value.hashCode()

    override fun toString(): String = "AdaptySubscriptionBillingPlan($value)"
}
