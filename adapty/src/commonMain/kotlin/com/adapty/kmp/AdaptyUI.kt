package com.adapty.kmp

import com.adapty.kmp.internal.AdaptyUIImpl
import com.adapty.kmp.models.AdaptyCustomAsset
import com.adapty.kmp.models.AdaptyOnboarding
import com.adapty.kmp.models.AdaptyPaywall
import com.adapty.kmp.models.AdaptyProductIdentifier
import com.adapty.kmp.models.AdaptyPurchaseParameters
import com.adapty.kmp.models.AdaptyResult
import com.adapty.kmp.models.AdaptyUIDialogActionType
import com.adapty.kmp.models.AdaptyUIIOSPresentationStyle
import com.adapty.kmp.models.AdaptyUIOnboardingView
import com.adapty.kmp.models.AdaptyUIPaywallView
import kotlinx.datetime.LocalDateTime
import kotlin.time.Duration

public object AdaptyUI : AdaptyUIContract by AdaptyUIImpl(adaptyPlugin = adaptyPlugin)

internal interface AdaptyUIContract {


    fun registerOnboardingEventsListener(
        observer: AdaptyUIOnboardingsEventsObserver,
        viewId: String
    )

    fun unregisterOnboardingEventsListener(viewId: String)

    fun registerPaywallEventsListener(
        observer: AdaptyUIPaywallsEventsObserver,
        viewId: String
    )

    fun unregisterPaywallEventsListener(viewId: String)


    fun setPaywallsEventsObserver(observer: AdaptyUIPaywallsEventsObserver)
    fun setOnboardingsEventsObserver(observer: AdaptyUIOnboardingsEventsObserver)

    suspend fun createPaywallView(
        paywall: AdaptyPaywall,
        loadTimeout: Duration? = null,
        preloadProducts: Boolean = false,
        customTags: Map<String, String>? = null,
        customTimers: Map<String, LocalDateTime>? = null,
        customAssets: Map<String, AdaptyCustomAsset>? = null,
        productPurchaseParams: Map<AdaptyProductIdentifier, AdaptyPurchaseParameters>? = null
    ): AdaptyResult<AdaptyUIPaywallView>

    suspend fun createOnboardingView(onboarding: AdaptyOnboarding): AdaptyResult<AdaptyUIOnboardingView>
    suspend fun presentOnboardingView(
        view: AdaptyUIOnboardingView,
        iosPresentationStyle: AdaptyUIIOSPresentationStyle = AdaptyUIIOSPresentationStyle.FULLSCREEN
    ): AdaptyResult<Unit>

    suspend fun presentPaywallView(
        view: AdaptyUIPaywallView,
        iosPresentationStyle: AdaptyUIIOSPresentationStyle = AdaptyUIIOSPresentationStyle.FULLSCREEN
    ): AdaptyResult<Unit>

    suspend fun dismissPaywallView(view: AdaptyUIPaywallView): AdaptyResult<Unit>
    suspend fun dismissOnboardingView(view: AdaptyUIOnboardingView): AdaptyResult<Unit>
    suspend fun showDialog(
        viewId: String,
        title: String,
        content: String,
        primaryActionTitle: String,
        secondaryActionTitle: String? = null
    ): AdaptyResult<AdaptyUIDialogActionType>
}