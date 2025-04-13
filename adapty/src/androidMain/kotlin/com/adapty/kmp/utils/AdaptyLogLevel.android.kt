package com.adapty.kmp.utils

import com.adapty.utils.AdaptyLogLevel as AdaptyLogLevelAndroid

internal fun AdaptyLogLevelAndroid.asAdaptyLogLevel(): AdaptyLogLevel {

    return when (this.toString()) {
        AdaptyLogLevel.NONE.toString() -> AdaptyLogLevel.NONE
        AdaptyLogLevel.ERROR.toString() -> AdaptyLogLevel.ERROR
        AdaptyLogLevel.WARN.toString() -> AdaptyLogLevel.WARN
        AdaptyLogLevel.INFO.toString() -> AdaptyLogLevel.INFO
        AdaptyLogLevel.VERBOSE.toString() -> AdaptyLogLevel.VERBOSE
        else -> AdaptyLogLevel.NONE
    }
}

internal fun AdaptyLogLevel.asAdaptyLogLevelAndroid(): AdaptyLogLevelAndroid {

    return when (this.value) {
        AdaptyLogLevel.NONE.value -> AdaptyLogLevelAndroid.NONE
        AdaptyLogLevel.ERROR.value -> AdaptyLogLevelAndroid.ERROR
        AdaptyLogLevel.WARN.value -> AdaptyLogLevelAndroid.WARN
        AdaptyLogLevel.INFO.value -> AdaptyLogLevelAndroid.INFO
        AdaptyLogLevel.VERBOSE.value -> AdaptyLogLevelAndroid.VERBOSE
        else -> AdaptyLogLevelAndroid.NONE
    }
}