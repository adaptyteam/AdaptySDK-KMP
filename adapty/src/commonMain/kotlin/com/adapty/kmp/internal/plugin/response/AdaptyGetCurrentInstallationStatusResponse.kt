package com.adapty.kmp.internal.plugin.response

import com.adapty.kmp.internal.utils.convertJsonToMapOfAny
import com.adapty.kmp.internal.utils.parseAdaptyDateTimeToLocalDateTime
import com.adapty.kmp.models.AdaptyInstallationDetails
import com.adapty.kmp.models.AdaptyInstallationStatus
import com.adapty.kmp.models.AdaptyInstallationStatusDetermined
import com.adapty.kmp.models.AdaptyInstallationStatusNotAvailable
import com.adapty.kmp.models.AdaptyInstallationStatusNotDetermined
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("status")
internal sealed interface AdaptyGetCurrentInstallationStatusResponse {

    @Serializable
    @SerialName("not_available")
    data object NotAvailable : AdaptyGetCurrentInstallationStatusResponse

    @Serializable
    @SerialName("not_determined")
    data object NotDetermined : AdaptyGetCurrentInstallationStatusResponse

    @Serializable
    @SerialName("determined")
    data class Determined(@SerialName("details") val details: AdaptyInstallationDetailsResponse) :
        AdaptyGetCurrentInstallationStatusResponse
}

@Serializable
internal data class AdaptyInstallationDetailsResponse(
    @SerialName("install_id")
    val installId: String? = null,

    @SerialName("install_time")
    val installTime: String,

    @SerialName("app_launch_count")
    val appLaunchCount: Int,

    @SerialName("payload")
    val payload: String? = null
)

internal fun AdaptyInstallationDetailsResponse.asAdaptyInstallationDetails(): AdaptyInstallationDetails {
    val payloadData = payload?.let {
        AdaptyInstallationDetails.Payload(
            jsonString = it,
            dataMap = it.convertJsonToMapOfAny()
        )
    }
    return AdaptyInstallationDetails(
        installId = installId,
        installTime = installTime.parseAdaptyDateTimeToLocalDateTime(),
        appLaunchCount = appLaunchCount,
        payload = payloadData
    )
}


internal fun AdaptyGetCurrentInstallationStatusResponse.asAdaptyInstallationStatus(): AdaptyInstallationStatus {
    return when (this) {
        is AdaptyGetCurrentInstallationStatusResponse.Determined -> AdaptyInstallationStatusDetermined(
            details = details.asAdaptyInstallationDetails()
        )

        AdaptyGetCurrentInstallationStatusResponse.NotAvailable -> AdaptyInstallationStatusNotAvailable
        AdaptyGetCurrentInstallationStatusResponse.NotDetermined -> AdaptyInstallationStatusNotDetermined
    }


}