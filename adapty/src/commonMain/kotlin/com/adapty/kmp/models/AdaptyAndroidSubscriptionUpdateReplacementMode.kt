package com.adapty.kmp.models

/**
 * Defines the proration mode to use when updating an active Android subscription.
 *
 * When replacing an existing subscription, this determines how Google Play
 * charges the user and handles the remaining time of the old subscription.
 */
public enum class AdaptyAndroidSubscriptionUpdateReplacementMode {
    /** Immediately replace the old subscription and charge the full price of the new one. */
    CHARGE_FULL_PRICE,

    /** Defer the subscription change until the current subscription expires. */
    DEFERRED,

    /** Immediately replace the subscription without any proration. */
    WITHOUT_PRORATION,

    /** Immediately replace the subscription and charge a prorated price for the remaining period. */
    CHARGE_PRORATED_PRICE,

    /** Immediately replace the subscription and prorate the remaining time without adjusting the price. */
    WITH_TIME_PRORATION
}