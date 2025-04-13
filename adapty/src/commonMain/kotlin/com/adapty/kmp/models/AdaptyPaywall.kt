package com.adapty.kmp.models


public data class AdaptyPaywall internal constructor(
    public val placementId: String,
    public val instanceIdentity: String,
    public val name: String,
    public val audienceName: String,
    public val abTestName: String,
    public val variationId: String,
    public val revision: Int,
    public val remoteConfig: AdaptyPaywallRemoteConfig? = null,
    internal val viewConfiguration: AdaptyPaywallViewConfiguration? = null,
    internal val products: List<AdaptyPaywallProductReference> = emptyList(),
    internal val payloadData: String? = null,
    internal val version: Long = 0L
) {
    val hasViewConfiguration: Boolean
        get() = viewConfiguration != null

    val vendorProductIds: List<String> get() = products.map { it.vendorId }

}