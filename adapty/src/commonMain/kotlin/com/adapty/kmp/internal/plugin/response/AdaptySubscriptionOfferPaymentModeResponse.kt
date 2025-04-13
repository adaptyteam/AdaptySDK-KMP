package com.adapty.kmp.internal.plugin.response

import com.adapty.kmp.models.AdaptySubscriptionOfferPaymentMode
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal enum class AdaptySubscriptionOfferPaymentModeResponse {
    @SerialName("pay_as_you_go")
    PAY_AS_YOU_GO,

    @SerialName("pay_up_front")
    PAY_UP_FRONT,

    @SerialName("free_trial")
    FREE_TRIAL,

    @SerialName("unknown")
    UNKNOWN
}

internal fun AdaptySubscriptionOfferPaymentModeResponse.asAdaptySubscriptionOfferPaymentMode(): AdaptySubscriptionOfferPaymentMode {
    return when (this) {
        AdaptySubscriptionOfferPaymentModeResponse.PAY_AS_YOU_GO -> AdaptySubscriptionOfferPaymentMode.PAY_AS_YOU_GO
        AdaptySubscriptionOfferPaymentModeResponse.PAY_UP_FRONT -> AdaptySubscriptionOfferPaymentMode.PAY_UP_FRONT
        AdaptySubscriptionOfferPaymentModeResponse.FREE_TRIAL -> AdaptySubscriptionOfferPaymentMode.FREE_TRIAL
        AdaptySubscriptionOfferPaymentModeResponse.UNKNOWN -> AdaptySubscriptionOfferPaymentMode.UNKNOWN
    }
}
