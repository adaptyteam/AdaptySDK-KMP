package com.adapty.kmp.models

import com.adapty.models.AdaptyPaywallProduct as AdaptyPaywallProductAndroid

internal fun AdaptyPaywallProductAndroid.asAdaptyPaywallProduct(): AdaptyPaywallProduct{
    return object :AdaptyPaywallProduct {} //TODO fix this
}