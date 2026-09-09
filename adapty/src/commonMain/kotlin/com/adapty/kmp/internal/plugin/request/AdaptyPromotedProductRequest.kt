package com.adapty.kmp.internal.plugin.request

import com.adapty.kmp.models.AdaptyPromotedProduct
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptyPromotedProductRequest(
    @SerialName("vendor_product_id")
    val vendorProductId: String,

    @SerialName("subscription")
    val subscription: AdaptyProductSubscriptionRequest? = null,

    @SerialName("payload_data")
    val payloadData: String? = null
)

internal fun AdaptyPromotedProduct.asAdaptyPromotedProductRequest(): AdaptyPromotedProductRequest =
    AdaptyPromotedProductRequest(
        vendorProductId = vendorProductId,
        subscription = subscription?.offer?.offerIdentifier?.asAdaptyProductSubscriptionRequest(),
        payloadData = payloadData,
    )
