package com.adapty.kmp.internal.plugin.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptyMakePurchaseRequest(
    @SerialName("product") val paywallProduct: AdaptyPaywallProductRequest,
    @SerialName("parameters") val parameters: AdaptyPurchaseParametersRequest? = null,
)


