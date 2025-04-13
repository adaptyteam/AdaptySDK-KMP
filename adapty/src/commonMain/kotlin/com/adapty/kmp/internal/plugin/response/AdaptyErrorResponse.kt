package com.adapty.kmp.internal.plugin.response

import com.adapty.kmp.models.AdaptyError
import com.adapty.kmp.models.AdaptyErrorCode
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptyPluginErrorResponse(
    @SerialName("adapty_code") val errorCode: Int,
    @SerialName("message") val message: String,
    @SerialName("detail") val detail: String? = null
)

internal fun AdaptyPluginErrorResponse?.asAdaptyError(): AdaptyError {
    return this?.let { adaptyErrorResponse ->
        val code = AdaptyErrorCode.entries.find { it.value == adaptyErrorResponse.errorCode }
            ?: AdaptyErrorCode.UNKNOWN
        AdaptyError(
            detail = adaptyErrorResponse.detail,
            message = adaptyErrorResponse.message,
            code = code
        )
    } ?: AdaptyError(
        message = "Unknown error",
        code = AdaptyErrorCode.UNKNOWN
    )
}