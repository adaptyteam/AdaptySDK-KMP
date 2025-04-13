package com.adapty.kmp.internal.plugin.response

import com.adapty.kmp.models.AdaptyPaywallViewConfiguration
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptyPaywallViewConfigurationResponse(
    @SerialName("paywall_builder_id") val paywallBuilderId: String,
    @SerialName("lang") val locale: String
)

internal fun AdaptyPaywallViewConfigurationResponse.asAdaptyPaywallViewConfiguration(): AdaptyPaywallViewConfiguration {
    return AdaptyPaywallViewConfiguration(
        paywallBuilderId = this.paywallBuilderId,
        locale = this.locale
    )
}

internal fun AdaptyPaywallViewConfiguration.asAdaptyPaywallViewConfigurationResponse(): AdaptyPaywallViewConfigurationResponse {
    return AdaptyPaywallViewConfigurationResponse(
        paywallBuilderId = this.paywallBuilderId,
        locale = this.locale

    )
}