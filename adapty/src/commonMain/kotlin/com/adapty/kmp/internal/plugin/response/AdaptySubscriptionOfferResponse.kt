package com.adapty.kmp.internal.plugin.response

import com.adapty.kmp.models.AdaptySubscriptionOffer
import com.adapty.kmp.internal.plugin.request.AdaptySubscriptionOfferIdentifierRequestResponse
import com.adapty.kmp.internal.plugin.request.asAdaptySubscriptionOfferIdentifier
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptySubscriptionOfferResponse(
    @SerialName("offer_identifier")
    val offerIdentifier: AdaptySubscriptionOfferIdentifierRequestResponse,

    @SerialName("phases")
    val phases: List<AdaptySubscriptionOfferPhaseResponse>,

    @SerialName("offer_tags")
    val offerTags: List<String>? = null // Android only
)

internal fun AdaptySubscriptionOfferResponse.asAdaptySubscriptionOffer(): AdaptySubscriptionOffer {
    return AdaptySubscriptionOffer(
        offerIdentifier = offerIdentifier.asAdaptySubscriptionOfferIdentifier(),
        phases = phases.map { it.asAdaptySubscriptionOfferPhase() },
        offerTags = offerTags
    )
}
