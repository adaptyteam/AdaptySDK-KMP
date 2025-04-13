package com.adapty.kmp

import android.content.Context
import com.adapty.kmp.models.AdaptyConfig

public suspend fun Adapty.activate(context: Context, config: AdaptyConfig) {
    AdaptyContextInitializer().create(context)
    Adapty.activate(config)
}