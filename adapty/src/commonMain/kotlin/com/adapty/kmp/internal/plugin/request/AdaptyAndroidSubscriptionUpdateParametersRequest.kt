package com.adapty.kmp.internal.plugin.request

import com.adapty.kmp.models.AdaptyAndroidSubscriptionUpdateParameters
import com.adapty.kmp.models.AdaptyPurchaseParameters
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptyAndroidSubscriptionUpdateParametersRequest(
    @SerialName("old_sub_vendor_product_id")
    val oldSubVendorProductId: String,

    @SerialName("replacement_mode")
    val replacementMode: AdaptyAndroidSubscriptionUpdateReplacementModeRequest
)

internal fun AdaptyAndroidSubscriptionUpdateParameters.asAdaptySubscriptionUpdateParametersRequest(): AdaptyAndroidSubscriptionUpdateParametersRequest {

    return AdaptyAndroidSubscriptionUpdateParametersRequest(
        oldSubVendorProductId = this.oldSubVendorProductId,
        replacementMode = this.replacementMode.asAdaptyAndroidSubscriptionUpdateReplacementModeRequest()
    )
}