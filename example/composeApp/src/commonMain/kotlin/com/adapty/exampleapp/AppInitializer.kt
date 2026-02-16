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
        val adaptyKey:String = BuildConfig.ADAPTY_API_KEY?: "public_live_QzY2YBrm.j0U3MNaKe2HAgeK4XV13"
        MainScope().launch {

            Adapty.activate(
                AdaptyConfig.Builder(adaptyKey)
                    .withLogLevel(AdaptyLogLevel.DEBUG)
                    .withObserverMode(false) //default false
                    .withCustomerUserId(null)
                    .withIpAddressCollectionDisabled(false) //default false
                    .withAppleIdfaCollectionDisabled(false)
                    .withGoogleAdvertisingIdCollectionDisabled(false) // default false
                    .withActivateUI(true)
                    .build()
            )
                .onSuccess {
                    AppLogger.d("Adapty activation success")
                }
                .onError { error ->
                    AppLogger.e("Adapty activation failed", error)
                }

        }
    }
}

