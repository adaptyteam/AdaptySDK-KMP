package com.adapty.kmp.internal.plugin

import com.adapty.kmp.internal.logger
import com.adapty.kmp.internal.plugin.constants.AdaptyPluginMethod
import com.adapty.kmp.internal.plugin.response.AdaptyPluginErrorResponse
import com.adapty.kmp.internal.utils.createJsonInstance
import com.adapty.kmp.internal.utils.getEmptyJsonObjectString
import com.adapty.kmp.models.AdaptyErrorCode
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

internal interface AdaptyPlugin {
    fun initialize()
    fun executePlatformSpecific(
        method: String,
        data: String,
        onResult: (String?) -> Unit
    )
}

internal inline fun <reified Request, reified Response> AdaptyPlugin.execute(
    method: AdaptyPluginMethod,
    request: Request,
    crossinline onResult: (AdaptyPluginResultJsonResponse<Response>) -> Unit
) {
    try {

        val requestJson =
            if (request is Unit) getEmptyJsonObjectString()
            else createJsonInstance().encodeToString(request)

        logger.log("AdaptyPlugin sending request for ${method.methodName}, requestJson: $requestJson")
        executePlatformSpecific(
            method = method.methodName,
            data = requestJson
        ) { resultJson ->
            logger.log("AdaptyPlugin got response for ${method.methodName}, resultJson: $resultJson")
            onResult(resultJson.asAdaptyResultJsonResponse<Response>())
        }
    } catch (e: Exception) {
        onResult(
            AdaptyPluginResultJsonResponse.Error(
                AdaptyPluginErrorResponse(
                    errorCode = AdaptyErrorCode.INTERNAL_PLUGIN_ERROR.value,
                    message = "Internal plugin error in Adapty.${method.methodName}()",
                    detail = e.stackTraceToString()
                )
            )
        )
    }
}

internal suspend inline fun <reified Request, reified Response> AdaptyPlugin.awaitExecute(
    method: AdaptyPluginMethod,
    request: Request
): AdaptyPluginResultJsonResponse<Response> = suspendCoroutine { cont ->
    execute(method, request) { result ->
        cont.resume(result)
    }
}