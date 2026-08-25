package com.adapty.kmp.internal.plugin.request

import com.adapty.kmp.models.AdaptySubscriptionOfferIdentifier
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptyProductSubscriptionRequest(
    @SerialName("offer")
    val offer: Offer? = null
) {
    @Serializable
    internal data class Offer(
        @SerialName("offer_identifier")
        val offerIdentifier: AdaptySubscriptionOfferIdentifierRequestResponse
    )
}

internal fun AdaptySubscriptionOfferIdentifier.asAdaptyProductSubscriptionRequest(): AdaptyProductSubscriptionRequest =
    AdaptyProductSubscriptionRequest(
        offer = AdaptyProductSubscriptionRequest.Offer(
            offerIdentifier = asAdaptySubscriptionOfferIdentifierRequest()
        )
    )
