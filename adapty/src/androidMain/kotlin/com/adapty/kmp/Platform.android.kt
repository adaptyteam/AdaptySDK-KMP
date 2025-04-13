package com.adapty.kmp

import android.content.Context

internal lateinit var applicationContext: Context //TODO get application context with startup initializer

internal actual fun adaptyImpl(): AdaptyContract = AdaptyImpl(context = applicationContext)