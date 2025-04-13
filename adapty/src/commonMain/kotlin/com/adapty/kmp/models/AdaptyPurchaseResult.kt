package com.adapty.kmp.models

public sealed class AdaptyPurchaseResult {

    public class Success(
        public val profile: AdaptyProfile,
        public val purchase: Purchase?,
    ) : AdaptyPurchaseResult() {
        override fun toString(): String {
            return "AdaptyPurchaseResult.Success(profile=$profile, purchase=$purchase)"
        }
    }

    public object UserCanceled : AdaptyPurchaseResult() {
        override fun toString(): String {
            return "AdaptyPurchaseResult.UserCanceled"
        }
    }

    public object Pending : AdaptyPurchaseResult() {
        override fun toString(): String {
            return "AdaptyPurchaseResult.Pending"
        }
    }
}