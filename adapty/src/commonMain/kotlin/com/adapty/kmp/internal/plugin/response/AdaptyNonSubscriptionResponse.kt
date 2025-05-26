package com.adapty.kmp.internal.plugin.response

import com.adapty.kmp.models.AdaptyProfile
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptyNonSubscriptionResponse(
    @SerialName("purchase_id")
    val purchaseId: String,

    @SerialName("store")
    val store: String,

    @SerialName("vendor_product_id")
    val vendorProductId: String,

    @SerialName("vendor_transaction_id")
    val vendorTransactionId: String?,

    @SerialName("purchased_at")
    val purchasedAt: String,

    @SerialName("is_sandbox")
    val isSandbox: Boolean,

    @SerialName("is_refund")
    val isRefund: Boolean,

    @SerialName("is_consumable")
    val isConsumable: Boolean
)

internal fun AdaptyNonSubscriptionResponse.asAdaptyNonSubscription(): AdaptyProfile.NonSubscription {
    return AdaptyProfile.NonSubscription(
        purchaseId = purchaseId,
        vendorProductId = vendorProductId,
        vendorTransactionId = vendorTransactionId,
        store = store,
        purchasedAt = purchasedAt,
        isConsumable = isConsumable,
        isSandbox = isSandbox,
        isRefund = isRefund
    )
}