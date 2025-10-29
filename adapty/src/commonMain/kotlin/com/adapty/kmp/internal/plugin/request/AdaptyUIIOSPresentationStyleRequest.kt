package com.adapty.kmp.internal.plugin.request

import com.adapty.kmp.models.AdaptyUIIOSPresentationStyle
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public enum class AdaptyUIIOSPresentationStyleRequest {
    @SerialName("full_screen")
    FULLSCREEN,

    @SerialName("page_sheet")
    PAGESHEET
}

internal fun AdaptyUIIOSPresentationStyle.asAdaptyUIIOSPresentationStyleRequest(): AdaptyUIIOSPresentationStyleRequest {
    return when (this) {
        AdaptyUIIOSPresentationStyle.FULLSCREEN -> AdaptyUIIOSPresentationStyleRequest.FULLSCREEN
        AdaptyUIIOSPresentationStyle.PAGESHEET -> AdaptyUIIOSPresentationStyleRequest.PAGESHEET
    }
}

