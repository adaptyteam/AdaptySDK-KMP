package com.adapty.kmp.models

import com.adapty.kmp.internal.AdaptyKMPInternal


/**
 * Represents a paywall retrieved from Adapty.
 *
 * A paywall can include multiple products, a variation ID, remote configuration,
 * and optionally a view configuration if the paywall was created using the
 * Adapty Paywall Builder.
 *
 * @property placement [AdaptyPlacement] The placement information associated with this paywall.
 * @property instanceIdentity A unique identifier of the paywall instance, configured in Adapty Dashboard.
 * @property name The name of the paywall.
 * @property variationId The variation identifier, used to attribute purchases to this paywall.
 * @property remoteConfig Optional custom dictionary configured in Adapty Dashboard for this paywall.
 */
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
    internal companion object {
        const val PREFIX_NATIVE_PLATFORM_VIEW = "compose_native_paywall_"
    }

    /**
     * Returns `true` if the paywall is built using the Adapty Paywall Builder.
     */
    val hasViewConfiguration: Boolean
        get() = viewConfiguration != null

    /**
     * Returns a list of [AdaptyProductIdentifier] for all products associated with this paywall.
     *
     */
    val productIdentifiers: List<AdaptyProductIdentifier>
        get() = products.map {
            AdaptyProductIdentifier(
                vendorProductId = it.vendorId,
                basePlanId = it.basePlanId,
                adaptyProductId = it.adaptyProductId
            )
        }

    @AdaptyKMPInternal
    val idForNativePlatformView: String = "${AdaptyOnboarding.Companion.PREFIX_NATIVE_PLATFORM_VIEW}$instanceIdentity"

}