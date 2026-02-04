package com.adapty.kmp.internal.plugin.request

import com.adapty.kmp.internal.AdaptyKMPInternal
import com.adapty.kmp.internal.utils.jsonInstance
import com.adapty.kmp.models.AdaptyOnboarding
import com.adapty.kmp.models.AdaptyWebPresentation
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptyUICreateOnboardingViewRequest(
    @SerialName("onboarding")
    val onboarding: AdaptyOnboardingRequestResponse,
    @SerialName("external_urls_presentation")
    val externalUrlsPresentation: AdaptyWebPresentationRequest? = null
)

@AdaptyKMPInternal
public fun createOnboardingViewRequestJsonString(
    onboarding: AdaptyOnboarding,
    externalUrlsPresentation: AdaptyWebPresentation?
): String {
    val request = AdaptyUICreateOnboardingViewRequest(
        onboarding = onboarding.asAdaptyOnboardingRequest(),
        externalUrlsPresentation = externalUrlsPresentation?.asAdaptyWebPresentationRequest()
    )
    return jsonInstance.encodeToString(request)
}


