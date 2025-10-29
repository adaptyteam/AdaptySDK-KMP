package com.adapty.kmp.models

public class AdaptyPurchaseParameters private constructor(
    /** Used to upgrade or downgrade a subscription (use for Android). */
    internal var subscriptionUpdateParams: AdaptyAndroidSubscriptionUpdateParameters? = null,

    /** Specifies whether the offer is personalized to the buyer (use for Android). */
    internal var isOfferPersonalized: Boolean? = null,
) {
    public class Builder {
        private val parameters = AdaptyPurchaseParameters()

        public fun setSubscriptionUpdateParams(value: AdaptyAndroidSubscriptionUpdateParameters?): Builder =
            apply { parameters.subscriptionUpdateParams = value }

        public fun setIsOfferPersonalized(value: Boolean?): Builder =
            apply { parameters.isOfferPersonalized = value }

        public fun build(): AdaptyPurchaseParameters = parameters
    }
}


