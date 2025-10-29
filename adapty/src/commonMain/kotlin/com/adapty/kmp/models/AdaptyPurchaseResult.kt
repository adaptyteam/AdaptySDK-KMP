package com.adapty.kmp.models

public sealed interface AdaptyPurchaseResult {
    public data class Success(
        public val profile: AdaptyProfile,
        public val appleJwsTransaction: String? = null,
        public val googlePurchaseToken: String? = null
    ) : AdaptyPurchaseResult

    public data object UserCanceled : AdaptyPurchaseResult
    public data object Pending : AdaptyPurchaseResult
}