package com.adapty.kmp.internal.plugin.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptyUpdateAttributionRequest(
    @SerialName("attribution") val attribution: String,
    @SerialName("source") val source: String
)