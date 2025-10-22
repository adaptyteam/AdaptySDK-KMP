package com.adapty.kmp.models


/**
 * Parameters for updating an active Android subscription.
 *
 * This class is used when you want to replace an existing subscription
 * with a new one, specifying which subscription is being replaced and
 * how the proration should be handled.
 *
 * @property oldSubVendorProductId The product ID of the current subscription
 * that should be updated.
 * @property replacementMode The proration mode for the subscription update.
 * @see AdaptyAndroidSubscriptionUpdateReplacementMode for possible values.
 */
public data class AdaptyAndroidSubscriptionUpdateParameters(
    val oldSubVendorProductId: String,
    val replacementMode: AdaptyAndroidSubscriptionUpdateReplacementMode,
)