package com.adapty.kmp.models

public class AdaptyPurchaseParameters private constructor(
    /** Used to upgrade or downgrade a subscription (use for Android). */
    internal var subscriptionUpdateParams: AdaptyAndroidSubscriptionUpdateParameters? = null,

    /** Specifies whether the offer is personalized to the buyer (use for Android). */
    internal var isOfferPersonalized: Boolean? = null,

    /** The obfuscated account identifier (use for Android). */
    internal var obfuscatedAccountId: String? = null,

    /** The obfuscated profile identifier (use for Android). */
    internal var obfuscatedProfileId: String? = null
) {
    public class Builder {
        private val parameters = AdaptyPurchaseParameters()

        public fun setSubscriptionUpdateParams(value: AdaptyAndroidSubscriptionUpdateParameters?): Builder =
            apply { parameters.subscriptionUpdateParams = value }

        public fun setIsOfferPersonalized(value: Boolean?): Builder =
            apply { parameters.isOfferPersonalized = value }

        public fun setObfuscatedAccountId(value: String?): Builder =
            apply { parameters.obfuscatedAccountId = value }

        public fun setObfuscatedProfileId(value: String?): Builder =
            apply { parameters.obfuscatedProfileId = value }

        public fun build(): AdaptyPurchaseParameters = parameters
    }
}


