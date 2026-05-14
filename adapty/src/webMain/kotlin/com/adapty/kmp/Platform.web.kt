package com.adapty.kmp

import com.adapty.kmp.internal.AdaptyKMPInternal
import com.adapty.kmp.internal.NoOpAdaptyPluginImpl
import com.adapty.kmp.internal.plugin.AdaptyPlugin
import com.adapty.kmp.models.AdaptyWebPresentation
import kotlinx.browser.window

@OptIn(AdaptyKMPInternal::class)
internal actual val adaptyPlugin: AdaptyPlugin by lazy { NoOpAdaptyPluginImpl() }
internal actual val isAndroidPlatform: Boolean get() = false
internal actual fun openUrl(url: String, openIn: AdaptyWebPresentation) {
    val target = when (openIn) {
        AdaptyWebPresentation.IN_APP_BROWSER -> "_self"
        AdaptyWebPresentation.EXTERNAL_BROWSER -> "_blank"
    }
    window.open(url, target)
}