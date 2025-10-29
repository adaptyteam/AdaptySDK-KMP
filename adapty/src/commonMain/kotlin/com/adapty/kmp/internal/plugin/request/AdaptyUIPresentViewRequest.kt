package com.adapty.kmp.internal.plugin.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptyUIPresentViewRequest(
    @SerialName("id") val id: String,
    @SerialName("ios_presentation_style") val iosPresentationStyle: AdaptyUIIOSPresentationStyleRequest
)