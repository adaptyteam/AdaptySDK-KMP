package com.adapty.kmp

import com.adapty.kmp.internal.plugin.AdaptyPlugin
import com.adapty.kmp.internal.plugin.AdaptyPluginImpl
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

internal actual val adaptyPlugin: AdaptyPlugin by lazy { AdaptyPluginImpl() }
internal actual val isAndroidPlatform: Boolean get() = false
internal actual fun openUrl(url: String) {
    val nsUrl = NSURL.URLWithString(url)
    nsUrl?.let { UIApplication.sharedApplication.openURL(it) }
}