package com.adapty.kmp

import com.adapty.kmp.internal.plugin.AdaptyPlugin

internal expect val adaptyPlugin: AdaptyPlugin
internal expect val isAndroidPlatform: Boolean
internal expect fun openUrl(url: String)