package com.adapty.kmp.internal.plugin.request

import com.adapty.kmp.models.AdaptyCustomerIdentity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptyCustomerIdentityRequest(
    @SerialName("app_account_token") val appAccountToken: String? = null,
    @SerialName("obfuscated_account_id") val obfuscatedAccountId: String? = null,
    @SerialName("obfuscated_profile_id") val obfuscatedProfileId: String? = null,
)

internal fun AdaptyCustomerIdentity.asAdaptyCustomerIdentityRequest(): AdaptyCustomerIdentityRequest {
    return AdaptyCustomerIdentityRequest(
        appAccountToken = iosAppAccountToken,
        obfuscatedAccountId = androidObfuscatedAccountId,
    )
}
