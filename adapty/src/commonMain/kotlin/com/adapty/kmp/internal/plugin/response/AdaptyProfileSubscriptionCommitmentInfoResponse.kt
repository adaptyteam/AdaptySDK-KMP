package com.adapty.kmp.internal.plugin.response

import com.adapty.kmp.models.AdaptyProfile
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Wire representation of `AdaptyProfile.SubscriptionCommitmentInfo` (cross_platform 4.3.1, iOS only). */
@Serializable
internal data class AdaptyProfileSubscriptionCommitmentInfoResponse(
    @SerialName("billing_period_number")
    val billingPeriodNumber: Int,

    @SerialName("total_billing_periods")
    val totalBillingPeriods: Int,

    @SerialName("expires_at")
    val expiresAt: String, // ISO 8601 datetime
)

internal fun AdaptyProfileSubscriptionCommitmentInfoResponse.asAdaptyProfileSubscriptionCommitmentInfo(): AdaptyProfile.SubscriptionCommitmentInfo =
    AdaptyProfile.SubscriptionCommitmentInfo(
        billingPeriodNumber = billingPeriodNumber,
        totalBillingPeriods = totalBillingPeriods,
        expiresAt = expiresAt,
    )
