package com.adapty.kmp.internal.plugin.response

import com.adapty.kmp.models.AdaptyPrice
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptyPriceResponse(
    @SerialName("amount")
    val amount: Double,
    @SerialName("currency_code")
    val currencyCode: String? = null,
    @SerialName("currency_symbol")
    val currencySymbol: String? = null,
    @SerialName("localized_string")
    val localizedString: String? = null,
)

internal fun AdaptyPriceResponse.asAdaptyPrice(): AdaptyPrice {
    return AdaptyPrice(
        amount = amount,
        currencyCode = currencyCode,
        currencySymbol = currencySymbol,
        localizedString = localizedString,
    )
}