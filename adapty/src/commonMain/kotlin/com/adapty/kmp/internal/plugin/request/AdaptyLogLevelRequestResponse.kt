package com.adapty.kmp.internal.plugin.request

import com.adapty.kmp.models.AdaptyLogLevel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal enum class AdaptyLogLevelRequestResponse {
    @SerialName("verbose")
    VERBOSE,

    @SerialName("debug")
    DEBUG,

    @SerialName("info")
    INFO,

    @SerialName("warn")
    WARN,

    @SerialName("error")
    ERROR
}

internal fun AdaptyLogLevel.asAdaptyLogLevelRequest(): AdaptyLogLevelRequestResponse {
    return when (this) {
        AdaptyLogLevel.VERBOSE -> AdaptyLogLevelRequestResponse.VERBOSE
        AdaptyLogLevel.INFO -> AdaptyLogLevelRequestResponse.INFO
        AdaptyLogLevel.WARN -> AdaptyLogLevelRequestResponse.WARN
        AdaptyLogLevel.ERROR -> AdaptyLogLevelRequestResponse.ERROR
        AdaptyLogLevel.DEBUG -> AdaptyLogLevelRequestResponse.DEBUG
    }
}

internal fun AdaptyLogLevelRequestResponse.asAdaptyLogLevel(): AdaptyLogLevel {

    return when(this){
        AdaptyLogLevelRequestResponse.VERBOSE -> AdaptyLogLevel.VERBOSE
        AdaptyLogLevelRequestResponse.INFO -> AdaptyLogLevel.INFO
        AdaptyLogLevelRequestResponse.WARN -> AdaptyLogLevel.WARN
        AdaptyLogLevelRequestResponse.ERROR -> AdaptyLogLevel.ERROR
        AdaptyLogLevelRequestResponse.DEBUG -> AdaptyLogLevel.DEBUG
    }
}