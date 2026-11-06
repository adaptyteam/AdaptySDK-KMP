package com.adapty.kmp.internal.plugin.request

import com.adapty.kmp.models.AdaptyProductSubscription
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * The purchase-relevant subset of a product's subscription details sent back to native
 * (`subscription` of `AdaptyPaywallProduct.Request` / `AdaptyPromotedProduct.Request`,
 * cross_platform 4.3.1): the chosen billing plan (iOS only) and the subscription offer.
 */
@Serializable
internal data class AdaptyProductSubscriptionRequest(
    @SerialName("pricing_terms")
    val pricingTerms: PricingTerms? = null, // iOS Only

    @SerialName("offer")
    val offer: Offer? = null
) {
    @Serializable
    internal data class PricingTerms(
        @SerialName("billing_plan_id")
        val billingPlanId: String
    )

    @Serializable
    internal data class Offer(
        @SerialName("offer_identifier")
        val offerIdentifier: AdaptySubscriptionOfferIdentifierRequestResponse
    )
}

/**
 * Picks the billing plan and the offer identifier out of the subscription details.
 * Returns `null` (the `subscription` key is omitted) when the product has neither.
 */
internal fun AdaptyProductSubscription.asAdaptyProductSubscriptionRequest(): AdaptyProductSubscriptionRequest? {
    val billingPlanId = pricingTerms?.billingPlan?.value
    val offerIdentifier = offer?.offerIdentifier
    if (billingPlanId == null && offerIdentifier == null) return null

    return AdaptyProductSubscriptionRequest(
        pricingTerms = billingPlanId?.let { AdaptyProductSubscriptionRequest.PricingTerms(billingPlanId = it) },
        offer = offerIdentifier?.let {
            AdaptyProductSubscriptionRequest.Offer(offerIdentifier = it.asAdaptySubscriptionOfferIdentifierRequest())
        },
    )
}
