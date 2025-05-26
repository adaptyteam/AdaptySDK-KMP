package com.adapty.kmp.internal.plugin.response

import com.adapty.kmp.models.AdaptyProfile
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class AdaptyAccessLevelResponse(
    @SerialName("id")
    val id: String,

    @SerialName("is_active")
    val isActive: Boolean,

    @SerialName("vendor_product_id")
    val vendorProductId: String,

    @SerialName("store")
    val store: String,

    @SerialName("activated_at")
    val activatedAt: String,

    @SerialName("renewed_at")
    val renewedAt: String?,

    @SerialName("expires_at")
    val expiresAt: String? = null,

    @SerialName("is_lifetime")
    val isLifetime: Boolean,

    @SerialName("active_introductory_offer_type")
    val activeIntroductoryOfferType: String? = null,

    @SerialName("active_promotional_offer_type")
    val activePromotionalOfferType: String? = null,

    @SerialName("active_promotional_offer_id")
    val activePromotionalOfferId: String? = null,

    @SerialName("offer_id")
    val offerId: String? = null,

    @SerialName("will_renew")
    val willRenew: Boolean,

    @SerialName("is_in_grace_period")
    val isInGracePeriod: Boolean,

    @SerialName("unsubscribed_at")
    val unsubscribedAt: String? = null, // ISO 8601 datetime

    @SerialName("billing_issue_detected_at")
    val billingIssueDetectedAt: String? = null, // ISO 8601 datetime

    @SerialName("starts_at")
    val startsAt: String? = null, // ISO 8601 datetime

    @SerialName("cancellation_reason")
    val cancellationReason: String? = null,

    @SerialName("is_refund")
    val isRefund: Boolean
)

internal fun AdaptyAccessLevelResponse.asAdaptyAccessLevel(): AdaptyProfile.AccessLevel {

    return AdaptyProfile.AccessLevel(
        id = id,
        isActive = isActive,
        vendorProductId = vendorProductId,
        offerId = offerId,
        store = store,
        activatedAt = activatedAt,
        startsAt = startsAt,
        renewedAt = renewedAt,
        expiresAt = expiresAt,
        isLifetime = isLifetime,
        cancellationReason = cancellationReason,
        isRefund = isRefund,
        activeIntroductoryOfferType = activeIntroductoryOfferType,
        activePromotionalOfferType = activePromotionalOfferType,
        activePromotionalOfferId = activePromotionalOfferId,
        willRenew = willRenew,
        isInGracePeriod = isInGracePeriod,
        unsubscribedAt = unsubscribedAt,
        billingIssueDetectedAt = billingIssueDetectedAt
    )
}