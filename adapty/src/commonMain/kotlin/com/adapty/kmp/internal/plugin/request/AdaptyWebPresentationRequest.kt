package com.adapty.kmp.internal.plugin.request


import com.adapty.kmp.models.AdaptyWebPresentation
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal enum class AdaptyWebPresentationRequest {

    @SerialName("browser_out_app")
    EXTERNAL_BROWSER,

    @SerialName("browser_in_app")
    IN_APP_BROWSER
}

internal fun AdaptyWebPresentation.asAdaptyWebPresentationRequest(): AdaptyWebPresentationRequest{
    return when(this){
        AdaptyWebPresentation.EXTERNAL_BROWSER -> AdaptyWebPresentationRequest.EXTERNAL_BROWSER
        AdaptyWebPresentation.IN_APP_BROWSER -> AdaptyWebPresentationRequest.IN_APP_BROWSER
    }
}



internal fun AdaptyWebPresentationRequest.asAdaptyWebPresentation(): AdaptyWebPresentation {
    return when (this) {
        AdaptyWebPresentationRequest.EXTERNAL_BROWSER -> AdaptyWebPresentation.EXTERNAL_BROWSER
        AdaptyWebPresentationRequest.IN_APP_BROWSER -> AdaptyWebPresentation.IN_APP_BROWSER
    }
}