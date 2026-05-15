package com.adapty.kmp

import com.adapty.internal.crossplatform.CrossplatformHelper
import com.adapty.kmp.internal.plugin.AdaptyPlugin
import com.adapty.kmp.internal.plugin.AdaptyPluginImpl
import com.adapty.kmp.models.AdaptyWebPresentation
import com.adapty.models.AdaptyWebPresentation as AdaptyNativeSdkWebPresentation


internal actual val adaptyPlugin: AdaptyPlugin by lazy { AdaptyPluginImpl() }
internal actual val isAndroidPlatform: Boolean get() = true
internal actual fun openUrl(url: String, openIn: AdaptyWebPresentation) {
    CrossplatformHelper.shared.openUrl(
        url = url,
        presentation = when(openIn){
            AdaptyWebPresentation.EXTERNAL_BROWSER -> AdaptyNativeSdkWebPresentation.ExternalBrowser
            AdaptyWebPresentation.IN_APP_BROWSER -> AdaptyNativeSdkWebPresentation. InAppBrowser
        }
    )
}