@file:OptIn(AdaptyKMPInternal::class)

package com.adapty.kmp.internal.plugin

import com.adapty.kmp.AdaptySwiftBridge
import com.adapty.kmp.internal.AdaptyKMPInternal
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class)
internal class AdaptyPluginImpl : AdaptyPlugin {
    private var isInitialized: Boolean = false

    override fun initialize() {
        if (isInitialized) return

        AdaptySwiftBridge.initializeOnEvent(onEvent = { eventName, eventDataJsonString ->
            AdaptyPluginEventHandler.onNewEvent(
                eventName = eventName,
                eventDataJsonString = eventDataJsonString ?: ""
            )
        })
        isInitialized = true
    }

    override fun executePlatformSpecific(
        method: String,
        data: String,
        onResult: (String?) -> Unit
    ) {
        require(isInitialized) {
            "Adapty was not initialized. Please call Adapty.activate() in your application start before using any Adapty functionality."
        }
        AdaptySwiftBridge.executeWithMethod(method = method, withJson = data) { result ->
            onResult(result)
        }
    }
}