package com.adapty.kmp.internal.plugin.response

import com.adapty.kmp.models.AdaptyProfile
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class AdaptySubscriptionResponse(
    @SerialName("store")
    val store: String,

    @SerialName("vendor_product_id")
    val vendorProductId: String,

    @SerialName("vendor_transaction_id")
    val vendorTransactionId: String,

    @SerialName("vendor_original_transaction_id")
    val vendorOriginalTransactionId: String,

    @SerialName("is_active")
    val isActive: Boolean,

    @SerialName("is_lifetime")
    val isLifetime: Boolean,

    @SerialName("activated_at")
    val activatedAt: String,

    @SerialName("renewed_at")
    val renewedAt: String?,

    @SerialName("expires_at")
    val expiresAt: String?,

    @SerialName("starts_at")
    val startsAt: String?,

    @SerialName("unsubscribed_at")
    val unsubscribedAt: String?, // ISO 8601 datetime

    @SerialName("billing_issue_detected_at")
    val billingIssueDetectedAt: String?, // ISO 8601 datetime

    @SerialName("is_in_grace_period")
    val isInGracePeriod: Boolean,

    @SerialName("is_refund")
    val isRefund: Boolean,

    @SerialName("is_sandbox")
    val isSandbox: Boolean,

    @SerialName("will_renew")
    val willRenew: Boolean,

    @SerialName("active_introductory_offer_type")
    val activeIntroductoryOfferType: String?,

    @SerialName("active_promotional_offer_type")
    val activePromotionalOfferType: String?,

    @SerialName("active_promotional_offer_id")
    val activePromotionalOfferId: String?,

    @SerialName("offer_id")
    val offerId: String?,

    @SerialName("cancellation_reason")
    val cancellationReason: String?
)

internal fun AdaptySubscriptionResponse.asAdaptySubscription(): AdaptyProfile.Subscription {
    return AdaptyProfile.Subscription(
        isActive = isActive,
        vendorProductId = vendorProductId,
        vendorTransactionId = vendorTransactionId,
        vendorOriginalTransactionId = vendorOriginalTransactionId,
        offerId = offerId,
        store = store,
        activatedAt = activatedAt,
        renewedAt = renewedAt,
        expiresAt = expiresAt,
        startsAt = startsAt,
        isLifetime = isLifetime,
        activeIntroductoryOfferType = activeIntroductoryOfferType,
        activePromotionalOfferType = activePromotionalOfferType,
        activePromotionalOfferId = activePromotionalOfferId,
        willRenew = willRenew,
        isInGracePeriod = isInGracePeriod,
        unsubscribedAt = unsubscribedAt,
        billingIssueDetectedAt = billingIssueDetectedAt,
        isSandbox = isSandbox,
        isRefund = isRefund,
        cancellationReason = cancellationReason
    )
}