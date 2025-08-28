package com.adapty.kmp.internal.plugin.request

import com.adapty.kmp.models.AdaptyPaywall
import com.adapty.kmp.internal.plugin.response.AdaptyPaywallRemoteConfigResponse
import com.adapty.kmp.internal.plugin.response.AdaptyPaywallViewConfigurationResponse
import com.adapty.kmp.internal.plugin.response.asAdaptyPaywallRemoteConfig
import com.adapty.kmp.internal.plugin.response.asAdaptyPaywallRemoteConfigResponse
import com.adapty.kmp.internal.plugin.response.asAdaptyPaywallViewConfiguration
import com.adapty.kmp.internal.plugin.response.asAdaptyPaywallViewConfigurationResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptyPaywallRequestResponse(
    @SerialName("developer_id")
    val developerId: String,

    @SerialName("audience_name")
    val audienceName: String? = null,

    @SerialName("paywall_id")
    val paywallId: String,

    @SerialName("paywall_name")
    val paywallName: String,

    @SerialName("response_created_at")
    val responseCreatedAt: Long,

    @SerialName("revision")
    val revision: Int,

    @SerialName("variation_id")
    val variationId: String,

    @SerialName("ab_test_name")
    val abTestName: String,

    @SerialName("remote_config")
    val remoteConfig: AdaptyPaywallRemoteConfigResponse? = null,

    @SerialName("paywall_builder")
    val viewConfiguration: AdaptyPaywallViewConfigurationResponse? = null,

    @SerialName("products")
    val products: List<AdaptyPaywallProductReferenceRequestResponse>,

    @SerialName("payload_data")
    val payloadData: String? = null
)

internal fun AdaptyPaywallRequestResponse.asAdaptyPaywall(): AdaptyPaywall {
    return AdaptyPaywall(
        placementId = this.developerId,
        instanceIdentity = this.paywallId,
        name = this.paywallName,
        audienceName = this.audienceName ?: "",
        abTestName = this.abTestName,
        variationId = this.variationId,
        revision = this.revision,
        remoteConfig = this.remoteConfig?.asAdaptyPaywallRemoteConfig(),
        viewConfiguration = this.viewConfiguration?.asAdaptyPaywallViewConfiguration(),
        products = this.products.map { it.asAdaptyPaywallProductReference() },
        version = this.responseCreatedAt,
    )
}

internal fun AdaptyPaywall.asAdaptyPaywallRequest(): AdaptyPaywallRequestResponse {

    return AdaptyPaywallRequestResponse(
        developerId = this.placementId,
        paywallId = this.instanceIdentity,
        paywallName = this.name,
        audienceName = this.audienceName,
        abTestName = this.abTestName,
        variationId = this.variationId,
        revision = this.revision,
        remoteConfig = this.remoteConfig?.asAdaptyPaywallRemoteConfigResponse(),
        viewConfiguration = this.viewConfiguration?.asAdaptyPaywallViewConfigurationResponse(),
        products = this.products.map { it.asAdaptyPaywallProductReferenceRequest() },
        payloadData = this.payloadData,
        responseCreatedAt = version
    )
}
