package com.adapty.kmp.models

/**
 * Configuration details for a paywall created using the Adapty Paywall Builder.
 *
 * @property paywallBuilderId The identifier of the paywall in the Adapty Dashboard.
 * @property locale The locale used for this paywall, e.g., "en-US".
 */
public data class AdaptyPaywallViewConfiguration internal constructor(
    val paywallBuilderId: String,
    val locale: String
)
