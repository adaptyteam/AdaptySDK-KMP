package com.adapty.kmp

import android.content.Context
import com.adapty.kmp.models.AdaptyConfig

/**
 * Activates the Adapty SDK using the provided [context] and [config].
 *
 * By default, the SDK automatically retrieves the application context
 * using Android Startup Initializer. However, if the automatic mechanism
 * is disabled or removed from the manifest, you can call this method
 * manually from your [Application.onCreate] to ensure proper initialization.
 *
 * @param context Android Application context to initialize the SDK.
 * @param config Configuration for Adapty SDK.
 *
 * @see AdaptyConfig
 */
public suspend fun Adapty.activate(context: Context, config: AdaptyConfig) {
    AdaptyContextInitializer().create(context)
    Adapty.activate(config)
}