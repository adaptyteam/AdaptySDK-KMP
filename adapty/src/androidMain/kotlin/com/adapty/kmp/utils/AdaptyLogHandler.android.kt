package com.adapty.kmp.utils

import com.adapty.utils.AdaptyLogHandler as AdaptyLogHandlerAndroid

internal fun AdaptyLogHandler.asAdaptyLogHandlerAndroid(): AdaptyLogHandlerAndroid {
    return AdaptyLogHandlerAndroid { level, message ->
        this.onLogMessageReceived(level = level.asAdaptyLogLevel(), message = message)
    }
}