package com.adapty.kmp.internal.plugin.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptyReportTransactionRequest(
    @SerialName("transaction_id") val transactionId: String,
    @SerialName("variation_id") val variationId: String? = null,
    @SerialName("apple_jws_transaction") val appleJwsTransaction: String? = null, // iOS Only
)
