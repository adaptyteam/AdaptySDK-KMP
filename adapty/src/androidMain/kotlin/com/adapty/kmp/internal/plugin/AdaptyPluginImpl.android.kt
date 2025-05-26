package com.adapty.kmp.internal.plugin

import com.adapty.internal.crossplatform.CrossplatformHelper
import com.adapty.kmp.applicationContext


internal class AdaptyPluginImpl : AdaptyPlugin {

    private val crossplatformHelper by lazy { CrossplatformHelper.shared }
    private var isInitialized: Boolean = false

    override fun initialize() {
        if (isInitialized) return
        isInitialized = true
    }

    override fun executePlatformSpecific(
        method: String,
        data: String,
        onResult: (String?) -> Unit
    ) {
        requireContextAndInitialization()
        crossplatformHelper.onMethodCall(
            methodName = method,
            argument = data,
            onResult = { result ->
                onResult(result)
            }
        )
    }

    private fun requireContextAndInitialization() {
        require(applicationContext != null && isInitialized) {
            """
                Adapty context was not initialized.
            
                Make sure the AdaptyContextInitializer is not removed from the manifest via tools:node="remove".
            
                If you’ve opted out of auto-initialization, you must call:
                
                    Adapty.activate(context, config)
            
                in your Application class before using any Adapty functionality.
    """.trimIndent()
        }
    }
}