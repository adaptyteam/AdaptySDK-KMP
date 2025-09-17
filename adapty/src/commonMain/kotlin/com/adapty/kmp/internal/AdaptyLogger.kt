package com.adapty.kmp.internal

import com.adapty.kmp.isAndroidPlatform
import com.adapty.kmp.models.AdaptyConfig

internal fun interface AdaptyLogger {
    fun log(message: String)
}

internal object EmptyLogger : AdaptyLogger {
    override fun log(message: String) {}
}

internal object ConsoleLogger : AdaptyLogger {
    override fun log(message: String) {
        val platFormName = if (isAndroidPlatform) "Android" else "iOS"
        val platformVersion = AdaptyConfig.SDK_VERSION
        val platformSDKName = AdaptyConfig.SDK_NAME
        println("AdaptyLog - $platformSDKName($platFormName) - v$platformVersion : $message")
    }
}

internal var logger: AdaptyLogger = ConsoleLogger