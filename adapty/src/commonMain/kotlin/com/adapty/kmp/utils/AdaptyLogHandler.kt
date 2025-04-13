package com.adapty.kmp.utils

public fun interface AdaptyLogHandler {
    public fun onLogMessageReceived(level: AdaptyLogLevel, message: String)
}