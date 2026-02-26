package com.adapty.kmp.models

/**
 * Represents a product price in the store, including optional localized formatting.
 *
 * @property amount The numeric value of the price in the store's currency.
 * @property currencyCode The ISO 4217 currency code, e.g., "USD". Nullable.
 * @property currencySymbol The currency symbol, e.g., "$". Nullable.
 * @property localizedString Formatted price string for the user's locale, e.g., "$4.99". Nullable.
 */
public data class AdaptyPrice internal constructor(
    val amount: Double,
    val currencyCode: String? = null,
    val currencySymbol: String? = null,
    val localizedString: String? = null
)
