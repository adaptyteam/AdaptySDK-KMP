package com.adapty.kmp.internal.plugin.response

import com.adapty.kmp.models.AdaptyPaywallProduct
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptyPaywallProductResponse(
    @SerialName("vendor_product_id")
    val vendorProductId: String,

    @SerialName("adapty_product_id")
    val adaptyProductId: String,

    @SerialName("paywall_product_index")
    val paywallProductIndex: Int,

    @SerialName("paywall_variation_id")
    val paywallVariationId: String,

    @SerialName("product_type")
    val productType: String,

    @SerialName("access_level_id")
    val accessLevelId: String,

    @SerialName("paywall_ab_test_name")
    val paywallAbTestName: String,

    @SerialName("paywall_name")
    val paywallName: String,

    @SerialName("audience_name")
    val audienceName: String? = null,

    @SerialName("localized_description")
    val localizedDescription: String,

    @SerialName("localized_title")
    val localizedTitle: String,

    @SerialName("is_family_shareable")
    val isFamilyShareable: Boolean? = false, //iOS only

    @SerialName("region_code")
    val regionCode: String? = null,

    @SerialName("price")
    val price: AdaptyPriceResponse,

    @SerialName("subscription")
    val subscription: AdaptyPaywallProductSubscriptionResponse? = null,

    @SerialName("payload_data")
    val payloadData: String? = null
)

internal fun AdaptyPaywallProductResponse.asAdaptyPaywallProduct(): AdaptyPaywallProduct {
    return AdaptyPaywallProduct(
        vendorProductId = vendorProductId,
        adaptyProductId = adaptyProductId,
        productType = productType,
        accessLevelId = accessLevelId,
        paywallProductIndex = paywallProductIndex,
        paywallABTestName = paywallAbTestName,
        paywallName = paywallName,
        audienceName = audienceName,
        paywallVariationId = paywallVariationId,
        localizedDescription = localizedDescription,
        localizedTitle = localizedTitle,
        isFamilyShareable = isFamilyShareable ?: false,
        regionCode = regionCode,
        price = price.asAdaptyPrice(),
        subscription = subscription?.asAdaptyPaywallProductSubscription(),
        payloadData = payloadData,
    )
}