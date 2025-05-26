package com.adapty.kmp.internal.plugin.request

import com.adapty.kmp.models.AdaptyProfileParameters
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
internal data class AdaptyUpdateProfileRequest(
    @SerialName("params") val params: AdaptyProfileParametersRequestData
)

@Serializable
internal data class AdaptyProfileParametersRequestData(
    @SerialName("first_name") val firstName: String? = null,
    @SerialName("last_name") val lastName: String? = null,
    @SerialName("gender") val gender: String? = null,
    @SerialName("birthday") val birthday: String? = null, // format: "YYYY-MM-dd"
    @SerialName("email") val email: String? = null,
    @SerialName("phone_number") val phoneNumber: String? = null,
    @SerialName("att_status") val attStatus: Int? = null, // iOS only
    @SerialName("custom_attributes") val customAttributes: AdaptyCustomAttributesRequestResponse? = null,
    @SerialName("analytics_disabled") val analyticsDisabled: Boolean? = null
)

internal fun AdaptyProfileParameters.asAdaptyUpdateProfileRequest(): AdaptyUpdateProfileRequest {
    val paramsData = AdaptyProfileParametersRequestData(
        firstName = firstName,
        lastName = lastName,
        email = email,
        phoneNumber = phoneNumber,
        gender = gender,
        birthday = birthday,
        attStatus = attStatus,
        customAttributes = customAttributes.toAdaptyCustomAttributesRequest(),
        analyticsDisabled = analyticsDisabled
    )
    return AdaptyUpdateProfileRequest(params = paramsData)
}