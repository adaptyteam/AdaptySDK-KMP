package com.adapty.kmp.models

/**
 * Parameters used when making a purchase or updating a subscription in Adapty.
 *
 * Primarily used for Android to configure subscription updates, personalized offers, and obfuscated identifiers.
 */
public class AdaptyPurchaseParameters private constructor(
    /** Used to upgrade or downgrade a subscription (use for Android). */
    internal var subscriptionUpdateParams: AdaptyAndroidSubscriptionUpdateParameters? = null,

    /** Specifies whether the offer is personalized to the buyer (use for Android). */
    internal var isOfferPersonalized: Boolean? = null,
) {

    /**
     * Builder for [AdaptyPurchaseParameters].
     *
     * Provides a convenient way to incrementally configure purchase parameters before building the object.
     */
    public class Builder {
        private val parameters = AdaptyPurchaseParameters()

        /**
         * Sets subscription update parameters for upgrading/downgrading a subscription
         * @param value The parameters for the subscription update. See [AdaptyAndroidSubscriptionUpdateParameters] for more info.
         * */
        public fun setSubscriptionUpdateParams(value: AdaptyAndroidSubscriptionUpdateParameters?): Builder =
            apply { parameters.subscriptionUpdateParams = value }

        /** Sets whether the offer is personalized for the buyer. */
        public fun setIsOfferPersonalized(value: Boolean?): Builder =
            apply { parameters.isOfferPersonalized = value }

        /** Sets the obfuscated account identifier. */
        public fun setObfuscatedAccountId(value: String?): Builder =
            apply { parameters.obfuscatedAccountId = value }

        /** Sets the obfuscated profile identifier. */
        public fun setObfuscatedProfileId(value: String?): Builder =
            apply { parameters.obfuscatedProfileId = value }

        /** Builds and returns the configured [AdaptyPurchaseParameters] instance. */
        public fun build(): AdaptyPurchaseParameters = parameters
    }
}


