package com.adapty.kmp

import com.adapty.kmp.internal.plugin.AdaptyPlugin
import com.adapty.kmp.internal.plugin.AdaptyPluginImpl


internal actual val adaptyPlugin: AdaptyPlugin by lazy { AdaptyPluginImpl() }
internal actual val isAndroidPlatform: Boolean get() = true