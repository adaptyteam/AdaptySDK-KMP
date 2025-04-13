package com.adapty.kmp.models

import com.adapty.internal.utils.InternalAdaptyApi
import com.adapty.models.AdaptyConfig as AdaptyConfigAndroid

@OptIn(InternalAdaptyApi::class)
internal fun AdaptyConfig.asAdaptyConfigAndroid(): AdaptyConfigAndroid {
    return AdaptyConfigAndroid.Builder(apiKey)
        .withCustomerUserId(customerUserId)
        .withObserverMode(observerMode)
        .withIpAddressCollectionDisabled(ipAddressCollectionDisabled)
        .withAdIdCollectionDisabled(adIdCollectionDisabled)
        .withBackendBaseUrl(backendBaseUrl)
        .build()
}