package com.adapty.kmp.internal.plugin.response

import com.adapty.kmp.models.AdaptyPeriodUnit
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal enum class AdaptyPeriodUnitResponse {
    @SerialName("day")
    DAY,

    @SerialName("week")
    WEEK,

    @SerialName("month")
    MONTH,

    @SerialName("year")
    YEAR,

    @SerialName("unknown")
    UNKNOWN
}

internal fun AdaptyPeriodUnitResponse.asAdaptyPeriodUnit(): AdaptyPeriodUnit {
    return when (this) {
        AdaptyPeriodUnitResponse.DAY -> AdaptyPeriodUnit.DAY
        AdaptyPeriodUnitResponse.WEEK -> AdaptyPeriodUnit.WEEK
        AdaptyPeriodUnitResponse.MONTH -> AdaptyPeriodUnit.MONTH
        AdaptyPeriodUnitResponse.YEAR -> AdaptyPeriodUnit.YEAR
        AdaptyPeriodUnitResponse.UNKNOWN -> AdaptyPeriodUnit.UNKNOWN
    }
}

