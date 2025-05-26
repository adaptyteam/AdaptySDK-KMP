package com.adapty.kmp.internal.plugin.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptyIdentifyRequest(
    @SerialName("customer_user_id") val customerUserId: String
)