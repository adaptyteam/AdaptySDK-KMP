package com.adapty.kmp.internal.plugin.request

import com.adapty.kmp.models.AdaptySubscriptionOfferType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal enum class AdaptySubscriptionOfferTypeRequestResponse {
    @SerialName("introductory")
    INTRODUCTORY,

    @SerialName("promotional")
    PROMOTIONAL,

    @SerialName("win_back")
    WINBACK
}

internal fun AdaptySubscriptionOfferTypeRequestResponse.asAdaptySubscriptionOfferType(): AdaptySubscriptionOfferType {
    return when (this) {
        AdaptySubscriptionOfferTypeRequestResponse.INTRODUCTORY -> AdaptySubscriptionOfferType.INTRODUCTORY
        AdaptySubscriptionOfferTypeRequestResponse.PROMOTIONAL -> AdaptySubscriptionOfferType.PROMOTIONAL
        AdaptySubscriptionOfferTypeRequestResponse.WINBACK -> AdaptySubscriptionOfferType.WINBACK
    }
}

internal fun AdaptySubscriptionOfferType.asAdaptySubscriptionOfferTypeRequest(): AdaptySubscriptionOfferTypeRequestResponse {
    return when (this) {
        AdaptySubscriptionOfferType.INTRODUCTORY -> AdaptySubscriptionOfferTypeRequestResponse.INTRODUCTORY
        AdaptySubscriptionOfferType.PROMOTIONAL -> AdaptySubscriptionOfferTypeRequestResponse.PROMOTIONAL
        AdaptySubscriptionOfferType.WINBACK -> AdaptySubscriptionOfferTypeRequestResponse.WINBACK
    }
}

