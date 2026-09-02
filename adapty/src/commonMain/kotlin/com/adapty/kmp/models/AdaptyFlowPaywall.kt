package com.adapty.kmp.models

/**
 * A single paywall variation contained within an [AdaptyFlow] (cross_platform 4.0.0).
 *
 * @property placement [AdaptyPlacement] the placement this paywall belongs to.
 * @property instanceIdentity a unique identifier of the paywall, configured in Adapty Dashboard.
 * @property name the name of the paywall.
 * @property variationId the variation identifier, used to attribute purchases to this paywall.
 */
public data class AdaptyFlowPaywall internal constructor(
    public val placement: AdaptyPlacement,
    public val instanceIdentity: String,
    public val name: String,
    public val variationId: String,
    internal val products: List<AdaptyPaywallProductReference> = emptyList(),
    internal val webPurchaseUrl: String? = null,
) {

    /**
     * Returns a list of [AdaptyProductIdentifier] for all products in this paywall.
     */
    public val productIdentifiers: List<AdaptyProductIdentifier>
        get() = products.map {
            AdaptyProductIdentifier(
                vendorProductId = it.vendorId,
                basePlanId = it.basePlanId,
                adaptyProductId = it.adaptyProductId
            )
        }
}
