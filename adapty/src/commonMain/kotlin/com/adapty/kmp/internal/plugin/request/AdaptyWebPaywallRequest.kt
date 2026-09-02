package com.adapty.kmp.internal.plugin.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptyWebPaywallRequest private constructor(
    @SerialName("product") val paywallProduct: AdaptyPaywallProductRequest? = null,
    @SerialName("paywall") val paywall: AdaptyFlowPaywallRequestResponse? = null,
    @SerialName("open_in") val webPresentationRequest: AdaptyWebPresentationRequest? = null,
) {
    companion object {
        fun fromPaywall(
            paywall: AdaptyFlowPaywallRequestResponse,
            webPresentationRequest: AdaptyWebPresentationRequest? = null
        ): AdaptyWebPaywallRequest {
            return AdaptyWebPaywallRequest(
                paywall = paywall,
                webPresentationRequest = webPresentationRequest
            )
        }

        fun fromPaywallProduct(
            product: AdaptyPaywallProductRequest,
            webPresentationRequest: AdaptyWebPresentationRequest? = null
        ): AdaptyWebPaywallRequest {
            return AdaptyWebPaywallRequest(
                paywallProduct = product,
                webPresentationRequest = webPresentationRequest
            )
        }
    }
}
