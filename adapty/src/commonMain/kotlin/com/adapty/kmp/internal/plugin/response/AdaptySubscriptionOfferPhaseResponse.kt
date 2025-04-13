package com.adapty.kmp.internal.plugin.response

import com.adapty.kmp.models.AdaptySubscriptionOfferPhase
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptySubscriptionOfferPhaseResponse(
    @SerialName("price")
    val price: AdaptyPriceResponse,

    @SerialName("number_of_periods")
    val numberOfPeriods: Int,

    @SerialName("payment_mode")
    val paymentMode: AdaptySubscriptionOfferPaymentModeResponse,

    @SerialName("subscription_period")
    val subscriptionPeriod: AdaptySubscriptionPeriodResponse,

    @SerialName("localized_subscription_period")
    val localizedSubscriptionPeriod: String? = null,

    @SerialName("localized_number_of_periods")
    val localizedNumberOfPeriods: String? = null
)

internal fun AdaptySubscriptionOfferPhaseResponse.asAdaptySubscriptionOfferPhase(): AdaptySubscriptionOfferPhase {
    return AdaptySubscriptionOfferPhase(
        price = price.asAdaptyPrice(),
        numberOfPeriods = numberOfPeriods,
        paymentMode = paymentMode.asAdaptySubscriptionOfferPaymentMode(),
        subscriptionPeriod = subscriptionPeriod.asAdaptySubscriptionPeriod(),
        localizedSubscriptionPeriod = localizedSubscriptionPeriod,
        localizedNumberOfPeriods = localizedNumberOfPeriods
    )
}
