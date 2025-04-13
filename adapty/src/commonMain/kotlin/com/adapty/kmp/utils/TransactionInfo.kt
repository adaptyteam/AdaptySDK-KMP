package com.adapty.kmp.utils


public sealed class TransactionInfo {
    internal class Id(val transactionId: String) : TransactionInfo()
    internal class Purchase(val purchase: com.adapty.kmp.models.Purchase) : TransactionInfo()

    public companion object {
        public fun fromId(transactionId: String): TransactionInfo = Id(transactionId)
        public fun fromPurchase(purchase: com.adapty.kmp.models.Purchase): TransactionInfo =
            Purchase(purchase)
    }
}