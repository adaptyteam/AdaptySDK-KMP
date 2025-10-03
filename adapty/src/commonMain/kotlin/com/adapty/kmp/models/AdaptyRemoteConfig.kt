package com.adapty.kmp.models

public data class AdaptyRemoteConfig(
    public val locale: String,
    public val dataJsonString: String,
    public val dataMap: Map<String, Any>,
) {

    override fun toString(): String {
        return "AdaptyPaywallRemoteConfig(locale=$locale, dataMap=${dataMap})"
    }
}