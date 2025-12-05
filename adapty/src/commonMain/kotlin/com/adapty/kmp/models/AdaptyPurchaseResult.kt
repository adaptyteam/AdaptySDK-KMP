package com.adapty.kmp.models

/**
 * Represents the result of a purchase operation in Adapty.
 */
public sealed interface AdaptyPurchaseResult {

    /**
     * The purchase was successful.
     *
     * @property profile The updated [AdaptyProfile] after the purchase.
     * @property jwsTransaction Optional JWS-encoded transaction string
     */
    public data class Success(
        public val profile: AdaptyProfile,
        public val appleJwsTransaction: String? = null,
        public val googlePurchaseToken: String? = null
    ) : AdaptyPurchaseResult

    /** The purchase was canceled by the user. */
    public data object UserCanceled : AdaptyPurchaseResult

    /** The purchase is pending (e.g., waiting for approval or external processing). */
    public data object Pending : AdaptyPurchaseResult
}