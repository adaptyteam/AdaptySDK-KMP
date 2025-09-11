package com.adapty.kmp

import android.content.Intent
import android.net.Uri
import com.adapty.kmp.internal.plugin.AdaptyPlugin
import com.adapty.kmp.internal.plugin.AdaptyPluginImpl


internal actual val adaptyPlugin: AdaptyPlugin by lazy { AdaptyPluginImpl() }
internal actual val isAndroidPlatform: Boolean get() = true
internal actual fun openUrl(url: String) {
    val uri = runCatching { Uri.parse(url) }.getOrNull() ?: return
    val intent = Intent(Intent.ACTION_VIEW, uri)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    applicationContext?.startActivity(intent)
}