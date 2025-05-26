package com.adapty.kmp.internal.plugin.request

import com.adapty.kmp.models.AdaptyPaywallProduct
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptyPaywallProductRequest(
    @SerialName("vendor_product_id")
    val vendorProductId: String,

    @SerialName("adapty_product_id")
    val adaptyProductId: String,

    @SerialName("subscription_offer_identifier")
    val subscriptionOfferIdentifier: AdaptySubscriptionOfferIdentifierRequestResponse? = null,

    @SerialName("paywall_variation_id")
    val paywallVariationId: String,

    @SerialName("paywall_ab_test_name")
    val paywallABTestName: String,

    @SerialName("paywall_name")
    val paywallName: String,

    @SerialName("payload_data")
    val payloadData: String? = null
)

internal fun AdaptyPaywallProduct.asAdaptyPaywallProductRequest(): AdaptyPaywallProductRequest {

    return AdaptyPaywallProductRequest(
        vendorProductId = vendorProductId,
        adaptyProductId = adaptyProductId,
        subscriptionOfferIdentifier = subscription?.offer?.offerIdentifier?.asAdaptySubscriptionOfferIdentifierRequest(),
        paywallVariationId = paywallVariationId,
        paywallABTestName = paywallABTestName,
        paywallName = paywallName,
        payloadData = payloadData
    )
}