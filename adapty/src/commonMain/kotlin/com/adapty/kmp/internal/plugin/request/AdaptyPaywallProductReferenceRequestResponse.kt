package com.adapty.kmp.internal.plugin.request

import com.adapty.kmp.models.AdaptyPaywallProductReference
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptyPaywallProductReferenceRequestResponse(
    @SerialName("vendor_product_id")
    val vendorProductId: String,

    @SerialName("adapty_product_id")
    val adaptyProductId: String,

    @SerialName("product_type")
    val productType: String,

    @SerialName("access_level_id")
    val accessLevelId: String,

    @SerialName("promotional_offer_id")
    val promotionalOfferId: String? = null, // iOS Only

    @SerialName("win_back_offer_id")
    val winBackOfferId: String? = null, // iOS Only

    @SerialName("base_plan_id")
    val basePlanId: String? = null, // Android Only

    @SerialName("offer_id")
    val offerId: String? = null // Android Only
)

internal fun AdaptyPaywallProductReferenceRequestResponse.asAdaptyPaywallProductReference(): AdaptyPaywallProductReference {
    return AdaptyPaywallProductReference(
        vendorId = this.vendorProductId,
        adaptyProductId = this.adaptyProductId,
        productType = this.productType,
        accessLevelId = this.accessLevelId,
        promotionalOfferId = this.promotionalOfferId,
        winBackOfferId = this.winBackOfferId,
        basePlanId = this.basePlanId,
        offerId = this.offerId
    )
}

internal fun AdaptyPaywallProductReference.asAdaptyPaywallProductReferenceRequest(): AdaptyPaywallProductReferenceRequestResponse {
    return AdaptyPaywallProductReferenceRequestResponse(
        vendorProductId = this.vendorId,
        adaptyProductId = this.adaptyProductId,
        productType = this.productType,
        accessLevelId = this.accessLevelId,
        promotionalOfferId = this.promotionalOfferId,
        winBackOfferId = this.winBackOfferId,
        basePlanId = this.basePlanId,
        offerId = this.offerId
    )
}