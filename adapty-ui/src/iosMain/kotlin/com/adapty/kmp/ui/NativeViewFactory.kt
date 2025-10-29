package com.adapty.kmp.ui

import com.adapty.kmp.AdaptySwiftBridge
import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIViewController

/**
This factory is used to help to use swift views in compose. It can be used both for UIKit and SwiftUI.
 */
internal interface NativeViewFactory {
    fun createNativeOnboardingView(
        jsonString: String,
        id: String,
        onEvent: (String?, String?) -> Unit
    ): UIViewController

    fun createNativePaywallView(
        jsonString: String,
        id: String,
        onEvent: (String?, String?) -> Unit
    ): UIViewController
}

internal class IosNativeViewFactory : NativeViewFactory {
    @OptIn(ExperimentalForeignApi::class)
    override fun createNativeOnboardingView(
        jsonString: String,
        id: String,
        onEvent: (String?, String?) -> Unit
    ): UIViewController {
        return AdaptySwiftBridge.createNativeOnboardingViewWithJsonString(
            jsonString = jsonString,
            id = id,
            onEvent = onEvent
        ) as UIViewController
    }

    @OptIn(ExperimentalForeignApi::class)
    override fun createNativePaywallView(
        jsonString: String,
        id: String,
        onEvent: (String?, String?) -> Unit
    ): UIViewController {
        //TODO fix below
        return AdaptySwiftBridge.createNativePaywallViewWithJsonString(
            jsonString = jsonString,
            id = id,
            onEvent = onEvent
        ) as UIViewController
    }



}