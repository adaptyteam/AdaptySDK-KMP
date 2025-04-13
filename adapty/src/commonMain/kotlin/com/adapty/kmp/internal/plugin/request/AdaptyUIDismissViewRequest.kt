package com.adapty.kmp.internal.plugin.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptyUIDismissViewRequest(
    @SerialName("id") val id: String,
    @SerialName("destroy") val destroy: Boolean = false,
)