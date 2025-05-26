package com.adapty.kmp

import com.adapty.kmp.internal.AdaptyUIImpl
import com.adapty.kmp.models.AdaptyPaywall
import com.adapty.kmp.models.AdaptyUIDialogActionType
import com.adapty.kmp.models.AdaptyUIView
import kotlin.time.Duration

public object AdaptyUI : AdaptyUIContract by AdaptyUIImpl(adaptyPlugin = adaptyPlugin)

internal interface AdaptyUIContract {

    fun setObserver(observer: AdaptyUIObserver)

    suspend fun createPaywallView(
        paywall: AdaptyPaywall,
        loadTimeout: Duration? = null,
        preloadProducts: Boolean = false,
        customTags: Map<String, String>? = null,
        customTimers: Map<String, String>? = null, //TODO update time with kotlinx-localdatetime if needed
        androidPersonalizedOffers: Map<String, Boolean>? = null
    ): AdaptyUIView?

    fun presentPaywallView(view: AdaptyUIView)
    fun dismissPaywallView(view: AdaptyUIView)
    suspend fun showDialog(
        view: AdaptyUIView,
        title: String,
        content: String,
        primaryActionTitle: String,
        secondaryActionTitle: String? = null
    ): AdaptyUIDialogActionType
}