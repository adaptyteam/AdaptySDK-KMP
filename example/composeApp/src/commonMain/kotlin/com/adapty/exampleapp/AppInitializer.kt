package com.adapty.exampleapp

import KMPAdapty.example.composeApp.BuildConfig
import com.adapty.kmp.Adapty
import com.adapty.kmp.models.AdaptyConfig
import com.adapty.kmp.models.AdaptyLogLevel
import com.adapty.kmp.models.onError
import com.adapty.kmp.models.onSuccess
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

object AppInitializer {

    fun initialize() {
        AppLogger.initialize()

        val adaptyKey = when (getPlatform()) {
            Platform.Android -> BuildConfig.ADAPTY_ANDROID_API_KEY
            Platform.Ios -> BuildConfig.ADAPTY_IOS_API_KEY
        }

        Adapty.activate(
            AdaptyConfig.Builder(adaptyKey)
                .withLogLevel(AdaptyLogLevel.DEBUG)
                .withObserverMode(false) //default false
                .withCustomerUserId(null)
                .withIpAddressCollectionDisabled(false) //default false
                .withAppleIdfaCollectionDisabled(false)
                .withGoogleAdvertisingIdCollectionDisabled(false) // default false
                .withActivateUI(true)
                .build(),
            onError = { error ->
                if (error == null) {
                    AppLogger.d("Adapty activation success")
                } else {
                    AppLogger.e("Adapty activation failed", error)
                }
            }
        )


    }
}

