package com.adapty.kmp.internal.plugin.request

import com.adapty.kmp.models.AdaptyIosRefundPreference
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptyIosUpdateRefundPreferenceRequest(
    @SerialName("refund_preference")
    val refundPreference: AdaptyIosRefundPreferenceRequest
)

@Serializable
internal enum class AdaptyIosRefundPreferenceRequest {
    @SerialName("no_preference")
    NO_PREFERENCE,

    @SerialName("grant")
    GRANT,

    @SerialName("decline")
    DECLINE
}

internal fun AdaptyIosRefundPreference.asAdaptyIosRefundPreferenceRequest(): AdaptyIosRefundPreferenceRequest {
    return when (this) {
        AdaptyIosRefundPreference.NO_PREFERENCE -> AdaptyIosRefundPreferenceRequest.NO_PREFERENCE
        AdaptyIosRefundPreference.GRANT -> AdaptyIosRefundPreferenceRequest.GRANT
        AdaptyIosRefundPreference.DECLINE -> AdaptyIosRefundPreferenceRequest.DECLINE
    }
}
