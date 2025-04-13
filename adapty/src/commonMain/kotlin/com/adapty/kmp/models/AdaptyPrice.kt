package com.adapty.kmp.models

public data class AdaptyPrice internal constructor(
    val amount: Float,
    val currencyCode: String? = null,
    val currencySymbol: String? = null,
    val localizedString: String? = null
)
