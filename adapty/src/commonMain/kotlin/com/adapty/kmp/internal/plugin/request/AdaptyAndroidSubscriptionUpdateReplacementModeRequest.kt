package com.adapty.kmp.internal.plugin.request

import com.adapty.kmp.models.AdaptyAndroidSubscriptionUpdateReplacementMode
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal enum class AdaptyAndroidSubscriptionUpdateReplacementModeRequest {
    @SerialName("charge_full_price")
    CHARGE_FULL_PRICE,

    @SerialName("deferred")
    DEFERRED,

    @SerialName("without_proration")
    WITHOUT_PRORATION,

    @SerialName("charge_prorated_price")
    CHARGE_PRORATED_PRICE,

    @SerialName("with_time_proration")
    WITH_TIME_PRORATION
}

internal fun AdaptyAndroidSubscriptionUpdateReplacementMode.asAdaptyAndroidSubscriptionUpdateReplacementModeRequest(): AdaptyAndroidSubscriptionUpdateReplacementModeRequest {
    return when (this) {
        AdaptyAndroidSubscriptionUpdateReplacementMode.CHARGE_FULL_PRICE -> AdaptyAndroidSubscriptionUpdateReplacementModeRequest.CHARGE_FULL_PRICE
        AdaptyAndroidSubscriptionUpdateReplacementMode.DEFERRED -> AdaptyAndroidSubscriptionUpdateReplacementModeRequest.DEFERRED
        AdaptyAndroidSubscriptionUpdateReplacementMode.WITHOUT_PRORATION -> AdaptyAndroidSubscriptionUpdateReplacementModeRequest.WITHOUT_PRORATION
        AdaptyAndroidSubscriptionUpdateReplacementMode.CHARGE_PRORATED_PRICE -> AdaptyAndroidSubscriptionUpdateReplacementModeRequest.CHARGE_PRORATED_PRICE
        AdaptyAndroidSubscriptionUpdateReplacementMode.WITH_TIME_PRORATION -> AdaptyAndroidSubscriptionUpdateReplacementModeRequest.WITH_TIME_PRORATION

    }
}