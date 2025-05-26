package com.adapty.kmp.internal.plugin.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptyWebPaywallRequest private constructor(
    @SerialName("product") val paywallProduct: AdaptyPaywallProductRequest? = null,
    @SerialName("paywall") val paywall: AdaptyPaywallRequestResponse? = null,
) {
    companion object {
        fun fromPaywall(paywall: AdaptyPaywallRequestResponse): AdaptyWebPaywallRequest {
            return AdaptyWebPaywallRequest(paywall = paywall)
        }

        fun fromPaywallProduct(product: AdaptyPaywallProductRequest): AdaptyWebPaywallRequest {
            return AdaptyWebPaywallRequest(paywallProduct = product)
        }
    }
}


