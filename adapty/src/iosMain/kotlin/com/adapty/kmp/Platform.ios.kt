package com.adapty.kmp

import com.adapty.kmp.internal.plugin.AdaptyPlugin
import com.adapty.kmp.internal.plugin.AdaptyPluginImpl
import com.adapty.kmp.models.AdaptyWebPresentation
import platform.Foundation.NSURL
import platform.SafariServices.SFSafariViewController
import platform.UIKit.UIApplication
import platform.UIKit.UISceneActivationStateForegroundActive
import platform.UIKit.UIViewController
import platform.UIKit.UIWindow
import platform.UIKit.UIWindowScene

internal actual val adaptyPlugin: AdaptyPlugin by lazy { AdaptyPluginImpl() }
internal actual val isAndroidPlatform: Boolean get() = false
internal actual fun openUrl(url: String, openIn: AdaptyWebPresentation) {
    val nsUrl = NSURL.URLWithString(url) ?: return
    when (openIn) {
        AdaptyWebPresentation.EXTERNAL_BROWSER -> {
            UIApplication.sharedApplication.openURL(nsUrl, emptyMap<Any?, Any>(), null)
        }
        AdaptyWebPresentation.IN_APP_BROWSER -> {
            val presenter = findTopViewController()
            if (presenter != null) {
                val safariVC = SFSafariViewController(uRL = nsUrl)
                presenter.presentViewController(safariVC, animated = true, completion = null)
            } else {
                // Fallback if no root view controller is available (e.g., during app launch)
                UIApplication.sharedApplication.openURL(nsUrl, emptyMap<Any?, Any>(), null)
            }
        }
    }
}

private fun findTopViewController(): UIViewController? {
    val rootVC = findKeyWindow()?.rootViewController ?: return null
    return findTopPresented(rootVC)
}

private fun findKeyWindow(): UIWindow? {
    val foregroundScenes = UIApplication.sharedApplication.connectedScenes
        .filterIsInstance<UIWindowScene>()
        .filter { it.activationState == UISceneActivationStateForegroundActive }
        .ifEmpty {
            UIApplication.sharedApplication.connectedScenes.filterIsInstance<UIWindowScene>()
        }

    for (scene in foregroundScenes) {
        val windows = scene.windows.filterIsInstance<UIWindow>()
        windows.firstOrNull { it.isKeyWindow() }?.let { return it }
    }
    return foregroundScenes.firstOrNull()?.windows?.filterIsInstance<UIWindow>()?.firstOrNull()
}

private fun findTopPresented(controller: UIViewController): UIViewController {
    val presented = controller.presentedViewController
    return if (presented != null) findTopPresented(presented) else controller
}