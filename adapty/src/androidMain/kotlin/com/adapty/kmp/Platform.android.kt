package com.adapty.kmp

import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import com.adapty.kmp.internal.plugin.AdaptyPlugin
import com.adapty.kmp.internal.plugin.AdaptyPluginImpl
import com.adapty.kmp.models.AdaptyWebPresentation


internal actual val adaptyPlugin: AdaptyPlugin by lazy { AdaptyPluginImpl() }
internal actual val isAndroidPlatform: Boolean get() = true
internal actual fun openUrl(url: String, openIn: AdaptyWebPresentation) {
    val uri = runCatching { Uri.parse(url) }.getOrNull() ?: return
    when (openIn) {
        AdaptyWebPresentation.EXTERNAL_BROWSER -> {
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            applicationContext?.startActivity(intent)
        }
        AdaptyWebPresentation.IN_APP_BROWSER -> {
            val context = applicationContext ?: return
            val customTabsIntent = CustomTabsIntent.Builder().build()
            customTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            customTabsIntent.launchUrl(context, uri)
        }
    }
}