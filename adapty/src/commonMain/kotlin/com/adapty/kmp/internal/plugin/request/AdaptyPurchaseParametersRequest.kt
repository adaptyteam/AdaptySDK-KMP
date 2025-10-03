package com.adapty.kmp.internal.plugin.request

import com.adapty.kmp.models.AdaptyPurchaseParameters
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptyPurchaseParametersRequest(
    @SerialName("subscription_update_params")
    val androidSubscriptionUpdateParams: AdaptyAndroidSubscriptionUpdateParametersRequest?,
    @SerialName("is_offer_personalized")
    val isOfferPersonalized: Boolean?,
    @SerialName("obfuscated_account_id")
    val obfuscatedAccountId: String?,
    @SerialName("obfuscated_profile_id")
    val obfuscatedProfileId: String?
)

internal fun AdaptyPurchaseParameters.asAdaptyPurchaseParametersRequest(): AdaptyPurchaseParametersRequest {

    return AdaptyPurchaseParametersRequest(
        isOfferPersonalized = isOfferPersonalized,
        obfuscatedAccountId = obfuscatedAccountId,
        obfuscatedProfileId = obfuscatedProfileId,
        androidSubscriptionUpdateParams = subscriptionUpdateParams?.asAdaptySubscriptionUpdateParametersRequest()
    )
}