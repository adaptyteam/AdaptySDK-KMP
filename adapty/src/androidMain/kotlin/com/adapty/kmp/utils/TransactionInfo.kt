package com.adapty.kmp.utils

import com.adapty.kmp.models.asGoogleBillingPurchase
import com.adapty.utils.TransactionInfo as AdaptyTransactionInfoAndroid


internal fun TransactionInfo.asAdaptyTransactionInfoAndroid(): AdaptyTransactionInfoAndroid {
    return when (this) {
        is TransactionInfo.Id -> AdaptyTransactionInfoAndroid.fromId(transactionId)
        is TransactionInfo.Purchase -> AdaptyTransactionInfoAndroid.fromPurchase(purchase.asGoogleBillingPurchase())
    }
}