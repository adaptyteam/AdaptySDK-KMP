package com.adapty.kmp.models


public data class AdaptyPaywall internal constructor(
    public val placement: AdaptyPlacement,
    public val instanceIdentity: String,
    public val name: String,
    public val variationId: String,
    public val remoteConfig: AdaptyRemoteConfig? = null,
    internal val viewConfiguration: AdaptyPaywallViewConfiguration? = null,
    internal val products: List<AdaptyPaywallProductReference> = emptyList(),
    internal val payloadData: String? = null,
    internal val webPurchaseUrl: String?,
    internal val requestLocale: String?,
    internal val responseCreatedAt: Long = 0L
) {
    val hasViewConfiguration: Boolean
        get() = viewConfiguration != null

    val productIdentifiers: List<AdaptyProductIdentifier>
        get() = products.map {
            AdaptyProductIdentifier(
                vendorProductId = it.vendorId,
                basePlanId = it.basePlanId,
                adaptyProductId = it.adaptyProductId
            )
        }

}