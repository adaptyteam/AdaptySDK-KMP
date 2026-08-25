package com.adapty.kmp.internal.plugin.response

import com.adapty.kmp.models.AdaptyProductSubscription
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptyProductSubscriptionResponse(
    @SerialName("group_identifier")
    val groupIdentifier: String? = null,

    @SerialName("period")
    val period: AdaptySubscriptionPeriodResponse,

    @SerialName("localized_period")
    val localizedPeriod: String? = null,

    @SerialName("offer")
    val offer: AdaptySubscriptionOfferResponse? = null,

    @SerialName("renewal_type")
    val renewalType: AdaptyRenewalTypeResponse = AdaptyRenewalTypeResponse.AUTORENEWABLE,

    @SerialName("base_plan_id")
    val basePlanId: String? = null
)

internal fun AdaptyProductSubscriptionResponse.asAdaptyProductSubscription(): AdaptyProductSubscription {

    return AdaptyProductSubscription(
        groupIdentifier = groupIdentifier,
        period = period.asAdaptySubscriptionPeriod(),
        localizedPeriod = localizedPeriod,
        offer = offer?.asAdaptySubscriptionOffer(),
        renewalType = renewalType.asAdaptyRenewalType(),
        basePlanId = basePlanId
    )
}

