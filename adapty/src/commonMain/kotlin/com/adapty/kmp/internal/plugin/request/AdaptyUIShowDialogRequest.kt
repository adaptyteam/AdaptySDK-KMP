package com.adapty.kmp.internal.plugin.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptyUIShowDialogRequest(
    @SerialName("id")
    val id: String,

    @SerialName("configuration")
    val configuration: AdaptyUIDialogRequest
)


