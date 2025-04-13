package com.adapty.kmp.internal.plugin.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptySetIntegrationIdentifierRequest(
    @SerialName("key_values") val keyValues: Map<String, String>
)

