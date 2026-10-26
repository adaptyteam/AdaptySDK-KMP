package com.adapty.kmp.internal.plugin.request

import com.adapty.kmp.models.AdaptyPromotedProduct
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptyPromotedProductRequest(
    @SerialName("purchase_intent_id")
    val purchaseIntentId: String? = null, // iOS Only (required by native iOS 4.3+)

    @SerialName("vendor_product_id")
    val vendorProductId: String,

    @SerialName("subscription")
    val subscription: AdaptyProductSubscriptionRequest? = null,

    @SerialName("payload_data")
    val payloadData: String? = null
)

internal fun AdaptyPromotedProduct.asAdaptyPromotedProductRequest(): AdaptyPromotedProductRequest =
    AdaptyPromotedProductRequest(
        purchaseIntentId = purchaseIntentId,
        vendorProductId = vendorProductId,
        subscription = subscription?.asAdaptyProductSubscriptionRequest(),
        payloadData = payloadData,
    )
