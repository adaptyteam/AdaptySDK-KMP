package com.adapty.kmp.extensions

import android.content.Context
import com.adapty.kmp.Adapty
import com.adapty.kmp.applicationContext
import com.adapty.kmp.models.AdaptyConfig

public fun Adapty.activate(context: Context, config: AdaptyConfig) {
    applicationContext = context
    Adapty.activate(config)
}