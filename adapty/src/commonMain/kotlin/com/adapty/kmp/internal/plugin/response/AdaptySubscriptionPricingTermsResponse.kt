package com.adapty.kmp.internal.plugin.response

import com.adapty.kmp.models.AdaptySubscriptionBillingPlan
import com.adapty.kmp.models.AdaptySubscriptionCommitmentInfo
import com.adapty.kmp.models.AdaptySubscriptionPricingTerms
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Wire representation of `AdaptySubscriptionPricingTerms` (cross_platform 4.3.1, iOS only). */
@Serializable
internal data class AdaptySubscriptionPricingTermsResponse(
    @SerialName("billing_plan_id")
    val billingPlanId: String,

    @SerialName("billing_price")
    val billingPrice: AdaptyPriceResponse,

    @SerialName("billing_period")
    val billingPeriod: AdaptySubscriptionPeriodResponse,

    @SerialName("commitment_info")
    val commitmentInfo: AdaptySubscriptionCommitmentInfoResponse,
)

/** Wire representation of `AdaptySubscriptionCommitmentInfo` (cross_platform 4.3.1, iOS only). */
@Serializable
internal data class AdaptySubscriptionCommitmentInfoResponse(
    @SerialName("price")
    val price: AdaptyPriceResponse,

    @SerialName("period")
    val period: AdaptySubscriptionPeriodResponse,
)

internal fun AdaptySubscriptionPricingTermsResponse.asAdaptySubscriptionPricingTerms(): AdaptySubscriptionPricingTerms =
    AdaptySubscriptionPricingTerms(
        billingPlan = AdaptySubscriptionBillingPlan(billingPlanId),
        billingPrice = billingPrice.asAdaptyPrice(),
        billingPeriod = billingPeriod.asAdaptySubscriptionPeriod(),
        commitmentInfo = commitmentInfo.asAdaptySubscriptionCommitmentInfo(),
    )

internal fun AdaptySubscriptionCommitmentInfoResponse.asAdaptySubscriptionCommitmentInfo(): AdaptySubscriptionCommitmentInfo =
    AdaptySubscriptionCommitmentInfo(
        price = price.asAdaptyPrice(),
        period = period.asAdaptySubscriptionPeriod(),
    )
