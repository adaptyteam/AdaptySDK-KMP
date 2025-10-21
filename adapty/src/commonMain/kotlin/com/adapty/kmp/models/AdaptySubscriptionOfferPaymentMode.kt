package com.adapty.kmp.models

/**
 * The payment mode of a subscription offer.
 */
public enum class AdaptySubscriptionOfferPaymentMode {
    /** User pays periodically, e.g., monthly or yearly. */
    PAY_AS_YOU_GO,

    /** User pays the full amount upfront for some period. */
    PAY_UP_FRONT,

    /** Free trial period before a paid subscription starts. */
    FREE_TRIAL,

    /** Unknown or unspecified payment mode. */
    UNKNOWN
}
