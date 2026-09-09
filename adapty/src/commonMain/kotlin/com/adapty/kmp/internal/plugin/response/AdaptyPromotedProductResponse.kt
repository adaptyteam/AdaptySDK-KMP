package com.adapty.kmp.internal.plugin.response

import com.adapty.kmp.models.AdaptyPromotedProduct
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptyPromotedProductResponse(
    @SerialName("vendor_product_id")
    val vendorProductId: String,

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
    val subscription: AdaptyProductSubscriptionResponse? = null,

    @SerialName("payload_data")
    val payloadData: String? = null
)

internal fun AdaptyPromotedProductResponse.asAdaptyPromotedProduct(): AdaptyPromotedProduct =
    AdaptyPromotedProduct(
        vendorProductId = vendorProductId,
        localizedDescription = localizedDescription,
        localizedTitle = localizedTitle,
        isFamilyShareable = isFamilyShareable ?: false,
        regionCode = regionCode,
        price = price.asAdaptyPrice(),
        subscription = subscription?.asAdaptyProductSubscription(),
        payloadData = payloadData,
    )
