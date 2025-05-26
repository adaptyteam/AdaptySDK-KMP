package com.adapty.kmp.internal.plugin

import com.adapty.kmp.internal.plugin.response.AdaptyPluginErrorResponse
import com.adapty.kmp.internal.plugin.response.asAdaptyError
import com.adapty.kmp.internal.utils.createJsonInstance
import com.adapty.kmp.models.AdaptyError
import com.adapty.kmp.models.AdaptyErrorCode
import com.adapty.kmp.models.AdaptyResult
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject

@Serializable
internal sealed interface AdaptyPluginResultJsonResponse<out T> {

    companion object {
        const val KEY_SUCCESS = "success"
        const val KEY_ERROR = "error"
    }

    @Serializable
    data class Success<out T>(@SerialName(KEY_SUCCESS) val data: T) :
        AdaptyPluginResultJsonResponse<T>

    @Serializable
    data class Error(@SerialName(KEY_ERROR) val error: AdaptyPluginErrorResponse) :
        AdaptyPluginResultJsonResponse<Nothing>

    fun getErrorOrNull(): AdaptyError? {
        return when (this) {
            is Error -> error.asAdaptyError()
            else -> null
        }
    }
}

internal inline fun <reified T> String?.asAdaptyResultJsonResponse(): AdaptyPluginResultJsonResponse<T> {
    val json = createJsonInstance()
    val jsonString = this ?: return AdaptyPluginResultJsonResponse.Error(
        AdaptyPluginErrorResponse(
            errorCode = AdaptyErrorCode.INTERNAL_PLUGIN_ERROR.value,
            message = "Internal plugin error while parsing response. Json string is null",
        )
    )

    val jsonElement = json.parseToJsonElement(jsonString).jsonObject
    return try {
        when {
            AdaptyPluginResultJsonResponse.KEY_ERROR in jsonElement -> {
                val error =
                    json.decodeFromJsonElement<AdaptyPluginErrorResponse>(jsonElement[AdaptyPluginResultJsonResponse.KEY_ERROR]!!)
                AdaptyPluginResultJsonResponse.Error(error)
            }

            AdaptyPluginResultJsonResponse.KEY_SUCCESS in jsonElement -> {
                val data =
                    json.decodeFromJsonElement<T>(jsonElement[AdaptyPluginResultJsonResponse.KEY_SUCCESS]!!)
                AdaptyPluginResultJsonResponse.Success(data)
            }

            else -> {
                AdaptyPluginResultJsonResponse.Error(
                    AdaptyPluginErrorResponse(
                        errorCode = AdaptyErrorCode.INTERNAL_PLUGIN_ERROR.value,
                        message = "Internal plugin error while parsing response, null data",
                        detail = null
                    )
                )
            }

        }
    } catch (e: Exception) {
        AdaptyPluginResultJsonResponse.Error(
            AdaptyPluginErrorResponse(
                errorCode = AdaptyErrorCode.INTERNAL_PLUGIN_ERROR.value,
                message = "Internal plugin error while parsing response",
                detail = e.message
            )
        )
    }
}

internal fun <T, R> AdaptyPluginResultJsonResponse<T>?.asAdaptyResult(transform: (T) -> R): AdaptyResult<R> {

    val internalPluginError = AdaptyError(
        code = AdaptyErrorCode.INTERNAL_PLUGIN_ERROR,
        message = "Internal plugin error. Response is null"
    )

    if (this == null) return AdaptyResult.Error(error = internalPluginError)

    return when (this) {
        is AdaptyPluginResultJsonResponse.Success -> AdaptyResult.Success(transform(data))
        is AdaptyPluginResultJsonResponse.Error -> AdaptyResult.Error(error = error.asAdaptyError())
    }
}