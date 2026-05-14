package com.adapty.kmp

import com.adapty.kmp.internal.AdaptyKMPInternal
import com.adapty.kmp.internal.NoOpAdaptyPluginImpl
import com.adapty.kmp.internal.plugin.AdaptyPlugin
import com.adapty.kmp.models.AdaptyWebPresentation
import java.awt.Desktop
import java.net.URI

@OptIn(AdaptyKMPInternal::class)
internal actual val adaptyPlugin: AdaptyPlugin by lazy { NoOpAdaptyPluginImpl() }
internal actual val isAndroidPlatform: Boolean get() = false
internal actual fun openUrl(url: String, openIn: AdaptyWebPresentation) {
    if (Desktop.isDesktopSupported()) Desktop.getDesktop().browse(URI(url))
}