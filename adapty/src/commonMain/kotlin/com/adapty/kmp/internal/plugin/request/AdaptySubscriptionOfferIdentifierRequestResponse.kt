package com.adapty.kmp.internal.plugin.request

import com.adapty.kmp.models.AdaptySubscriptionOfferIdentifier
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptySubscriptionOfferIdentifierRequestResponse(
    @SerialName("id")
    val id: String? = null,

    @SerialName("type")
    val type: AdaptySubscriptionOfferTypeRequestResponse
)

internal fun AdaptySubscriptionOfferIdentifierRequestResponse.asAdaptySubscriptionOfferIdentifier(): AdaptySubscriptionOfferIdentifier {
    return AdaptySubscriptionOfferIdentifier(
        id = id,
        type = type.asAdaptySubscriptionOfferType()
    )
}

internal fun AdaptySubscriptionOfferIdentifier.asAdaptySubscriptionOfferIdentifierRequest(): AdaptySubscriptionOfferIdentifierRequestResponse {
    return AdaptySubscriptionOfferIdentifierRequestResponse(
        id = id,
        type = type.asAdaptySubscriptionOfferTypeRequest()
    )
}


