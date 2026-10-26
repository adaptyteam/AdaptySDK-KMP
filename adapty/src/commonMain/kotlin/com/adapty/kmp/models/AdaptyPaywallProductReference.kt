package com.adapty.kmp.models


/**
 * Internal reference to a product associated with a paywall, as received from the native SDK.
 *
 * Not part of the public API: apps use [AdaptyFlowPaywall.productIdentifiers] / [AdaptyProductIdentifier] instead.
 * It is sent back to native unchanged (`get_paywall_products`, `log_show_flow`,
 * `adapty_ui_create_flow_view`, web paywall), so every wire field is kept here.
 */
internal data class AdaptyPaywallProductReference(
    val vendorId: String,
    internal val adaptyProductId: String,
    val productType: String,
    val accessLevelId: String,
    val basePlanId: String? = null, // Android Only
    val offerId: String? = null, // Android Only
    val pricingTerms: List<PricingTerms>? = null, // iOS Only (cross_platform 4.3.1)
    val flowProductId: String? = null,
) {
    /**
     * A billing plan configured for the product on the paywall, with the offers available for it
     * (cross_platform 4.3.1, iOS only). Replaces the flat `promotional_offer_id` /
     * `win_back_offer_id` of earlier contracts.
     */
    internal data class PricingTerms(
        val billingPlanId: String,
        val promotionalOfferId: String? = null,
        val winBackOfferId: String? = null,
    )
}
