package com.adapty.kmp.internal.plugin.request

import com.adapty.kmp.models.AdaptyPaywallProductReference
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Wire representation of `AdaptyFlowPaywall.ProductReference` (cross_platform 4.3.1). */
@Serializable
internal data class AdaptyPaywallProductReferenceRequestResponse(
    @SerialName("flow_product_id")
    val flowProductId: String? = null,

    @SerialName("vendor_product_id")
    val vendorProductId: String,

    @SerialName("adapty_product_id")
    val adaptyProductId: String,

    @SerialName("product_type")
    val productType: String,

    @SerialName("access_level_id")
    val accessLevelId: String,

    @SerialName("base_plan_id")
    val basePlanId: String? = null, // Android Only

    @SerialName("offer_id")
    val offerId: String? = null, // Android Only

    @SerialName("pricing_terms")
    val pricingTerms: List<PricingTerms>? = null, // iOS Only
) {
    @Serializable
    internal data class PricingTerms(
        @SerialName("billing_plan_id")
        val billingPlanId: String,

        @SerialName("promotional_offer_id")
        val promotionalOfferId: String? = null,

        @SerialName("win_back_offer_id")
        val winBackOfferId: String? = null,
    )
}

internal fun AdaptyPaywallProductReferenceRequestResponse.asAdaptyPaywallProductReference(): AdaptyPaywallProductReference {
    return AdaptyPaywallProductReference(
        vendorId = this.vendorProductId,
        adaptyProductId = this.adaptyProductId,
        productType = this.productType,
        accessLevelId = this.accessLevelId,
        basePlanId = this.basePlanId,
        offerId = this.offerId,
        pricingTerms = this.pricingTerms?.map {
            AdaptyPaywallProductReference.PricingTerms(
                billingPlanId = it.billingPlanId,
                promotionalOfferId = it.promotionalOfferId,
                winBackOfferId = it.winBackOfferId,
            )
        },
        flowProductId = this.flowProductId,
    )
}

internal fun AdaptyPaywallProductReference.asAdaptyPaywallProductReferenceRequest(): AdaptyPaywallProductReferenceRequestResponse {
    return AdaptyPaywallProductReferenceRequestResponse(
        flowProductId = this.flowProductId,
        vendorProductId = this.vendorId,
        adaptyProductId = this.adaptyProductId,
        productType = this.productType,
        accessLevelId = this.accessLevelId,
        basePlanId = this.basePlanId,
        offerId = this.offerId,
        pricingTerms = this.pricingTerms?.map {
            AdaptyPaywallProductReferenceRequestResponse.PricingTerms(
                billingPlanId = it.billingPlanId,
                promotionalOfferId = it.promotionalOfferId,
                winBackOfferId = it.winBackOfferId,
            )
        },
    )
}
