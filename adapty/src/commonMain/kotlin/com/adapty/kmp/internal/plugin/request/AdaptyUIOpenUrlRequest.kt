package com.adapty.kmp.internal.plugin.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * `adapty_ui_open_url` request (cross_platform 4.0.0) — opens a URL natively.
 */
@Serializable
internal data class AdaptyUIOpenUrlRequest(
    @SerialName("url") val url: String,
    @SerialName("open_in") val openIn: AdaptyWebPresentationRequest? = null,
)
