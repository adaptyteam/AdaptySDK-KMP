package com.adapty.nativeuiexample

import com.adapty.kmp.Adapty
import com.adapty.kmp.models.AdaptyConfig
import com.adapty.kmp.models.AdaptyLogLevel
import com.adapty.kmp.models.AdaptyOnboarding
import com.adapty.kmp.models.AdaptyPaywall
import com.adapty.kmp.models.AdaptyResult
import com.adapty.kmp.models.onError
import com.adapty.kmp.models.onSuccess
import com.adapty.nativeuiexample.shared.BuildConfig
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

object AdaptyManager {

    fun initialize() {
        val adaptyKey: String = BuildConfig.ADAPTY_API_KEY ?: "ADAPTY_API_KEY_HERE"
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

    suspend fun getPaywall(placementId: String)
            : AdaptyResult<AdaptyPaywall> = Adapty.getPaywall(placementId)

    suspend fun getOnboarding(placementId: String)
            : AdaptyResult<AdaptyOnboarding> = Adapty.getOnboarding(placementId)
}

