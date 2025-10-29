package com.adapty.kmp.internal.plugin.response

import com.adapty.kmp.models.AdaptyOnboardingsSelectParams
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptyUIOnboardingsSelectParamsResponse(
    @SerialName("id") val id: String,
    @SerialName("value") val value: String,
    @SerialName("label") val label: String
)

internal fun AdaptyUIOnboardingsSelectParamsResponse.asAdaptyUIOnboardingsSelectParams(): AdaptyOnboardingsSelectParams {
    return AdaptyOnboardingsSelectParams(
        id = id,
        value = value,
        label = label
    )
}