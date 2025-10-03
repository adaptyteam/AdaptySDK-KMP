package com.adapty.kmp.internal.plugin.response

import com.adapty.kmp.models.AdaptyPurchaseResult
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("type")
internal sealed class AdaptyPurchaseResultResponse {
    @Serializable
    @SerialName("pending")
    data object Pending : AdaptyPurchaseResultResponse()

    @Serializable
    @SerialName("user_cancelled")
    data object UserCancelled : AdaptyPurchaseResultResponse()

    @Serializable
    @SerialName("success")
    data class Success(
        @SerialName("profile") val profile: AdaptyProfileResponse,
        @SerialName("jws_transaction") val jwsTransaction: String? = null
    ) : AdaptyPurchaseResultResponse()
}

internal fun AdaptyPurchaseResultResponse.asAdaptyPurchaseResult(): AdaptyPurchaseResult {
    return when (this) {
        is AdaptyPurchaseResultResponse.Pending -> AdaptyPurchaseResult.Pending
        is AdaptyPurchaseResultResponse.UserCancelled -> AdaptyPurchaseResult.UserCanceled
        is AdaptyPurchaseResultResponse.Success -> AdaptyPurchaseResult.Success(
            profile = profile.asAdaptyProfile(),
            jwsTransaction = jwsTransaction
        )
    }
}

