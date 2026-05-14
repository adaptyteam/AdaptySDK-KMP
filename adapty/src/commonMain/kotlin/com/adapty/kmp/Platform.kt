package com.adapty.kmp

import com.adapty.kmp.internal.plugin.AdaptyPlugin
import com.adapty.kmp.models.AdaptyWebPresentation

internal expect val adaptyPlugin: AdaptyPlugin
internal expect val isAndroidPlatform: Boolean
internal expect fun openUrl(url: String, openIn: AdaptyWebPresentation = AdaptyWebPresentation.EXTERNAL_BROWSER)