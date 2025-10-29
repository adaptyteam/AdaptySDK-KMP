package com.adapty.kmp.internal.plugin.request

import com.adapty.kmp.internal.AdaptyKMPInternal
import com.adapty.kmp.internal.plugin.response.AdaptyRemoteConfigResponse
import com.adapty.kmp.internal.plugin.response.asAdaptyPaywallRemoteConfig
import com.adapty.kmp.internal.plugin.response.asAdaptyPaywallRemoteConfigResponse
import com.adapty.kmp.internal.utils.jsonInstance
import com.adapty.kmp.models.AdaptyOnboarding
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptyOnboardingRequestResponse(
    @SerialName("placement")
    val placement: AdaptyPlacementRequestResponse,
    @SerialName("onboarding_id")
    val onboardingId: String,
    @SerialName("onboarding_name")
    val onboardingName: String,
    @SerialName("variation_id")
    val variationId: String,
    @SerialName("onboarding_builder")
    val viewConfiguration: AdaptyOnboardingBuilderRequest,
    @SerialName("response_created_at")
    val responseCreatedAt: Long,
    @SerialName("request_locale")
    val locale: String,
    @SerialName("payload_data")
    val payloadData: String? = null,
    @SerialName("remote_config")
    val remoteConfig: AdaptyRemoteConfigResponse? = null

)

internal fun AdaptyOnboardingRequestResponse.asAdaptyOnboarding(): AdaptyOnboarding {
    return AdaptyOnboarding(
        placement = this.placement.asAdaptyPlacement(),
        id = this.onboardingId,
        name = this.onboardingName,
        variationId = this.variationId,
        remoteConfig = this.remoteConfig?.asAdaptyPaywallRemoteConfig(),
        payloadData = this.payloadData,
        requestLocale = this.locale,
        responseCreatedAt = this.responseCreatedAt,
        onboardingBuilderConfigUrl = this.viewConfiguration.configUrl
    )
}

internal fun AdaptyOnboarding.asAdaptyOnboardingRequest(): AdaptyOnboardingRequestResponse {

    return AdaptyOnboardingRequestResponse(
        placement = this.placement.asAdaptyPlacementRequestResponse(),
        onboardingId = this.id,
        onboardingName = this.name,
        variationId = this.variationId,
        viewConfiguration = AdaptyOnboardingBuilderRequest(
            configUrl = this.onboardingBuilderConfigUrl
        ),
        responseCreatedAt = this.responseCreatedAt,
        locale = this.requestLocale,
        payloadData = this.payloadData,
        remoteConfig = this.remoteConfig?.asAdaptyPaywallRemoteConfigResponse()
    )
}

@AdaptyKMPInternal
public fun AdaptyOnboarding.asJsonString(): String {
    return jsonInstance.encodeToString(asAdaptyOnboardingRequest())
}
