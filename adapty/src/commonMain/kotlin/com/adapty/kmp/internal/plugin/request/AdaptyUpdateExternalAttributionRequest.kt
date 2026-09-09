package com.adapty.kmp.internal.plugin.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptyUpdateExternalAttributionRequest(
    @SerialName("attribution") val attribution: String,
    @SerialName("provider") val provider: String
)
