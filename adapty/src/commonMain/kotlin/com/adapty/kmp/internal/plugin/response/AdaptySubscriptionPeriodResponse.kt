package com.adapty.kmp.internal.plugin.response

import com.adapty.kmp.models.AdaptySubscriptionPeriod
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptySubscriptionPeriodResponse(
    @SerialName("unit")
    val unit: AdaptyPeriodUnitResponse,

    @SerialName("number_of_units")
    val numberOfUnits: Int
)

internal fun AdaptySubscriptionPeriodResponse.asAdaptySubscriptionPeriod(): AdaptySubscriptionPeriod {
    return AdaptySubscriptionPeriod(
        unit = unit.asAdaptyPeriodUnit(),
        numberOfUnits = numberOfUnits
    )
}
