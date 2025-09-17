package com.adapty.kmp.internal.plugin.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptyMakePurchaseRequest(
    @SerialName("product") val paywallProduct: AdaptyPaywallProductRequest,
    @SerialName("subscription_update_params") val subscriptionUpdateParams: AdaptyAndroidSubscriptionUpdateParametersRequest? = null,
    @SerialName("is_offer_personalized") val isOfferPersonalized: Boolean? = null,
)


