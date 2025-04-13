package com.adapty.kmp.internal.plugin.response

import com.adapty.kmp.models.AdaptyRenewalType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal enum class AdaptyRenewalTypeResponse {
    @SerialName("prepaid")
    PREPAID,

    @SerialName("autorenewable")
    AUTORENEWABLE
}

internal fun AdaptyRenewalTypeResponse.asAdaptyRenewalType(): AdaptyRenewalType {
    return when (this) {
        AdaptyRenewalTypeResponse.PREPAID -> AdaptyRenewalType.PREPAID
        AdaptyRenewalTypeResponse.AUTORENEWABLE -> AdaptyRenewalType.AUTORENEWABLE
    }
}