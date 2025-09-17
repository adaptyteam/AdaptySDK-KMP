package com.adapty.kmp.internal.plugin.response

import com.adapty.kmp.models.AdaptyUIDialogActionType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal enum class AdaptyUIDialogActionTypeResponse {
    @SerialName("primary")
    PRIMARY,

    @SerialName("secondary")
    SECONDARY,
}

internal fun AdaptyUIDialogActionTypeResponse.asAdaptyUIDialogActionType(): AdaptyUIDialogActionType {
    return when (this) {
        AdaptyUIDialogActionTypeResponse.PRIMARY -> AdaptyUIDialogActionType.PRIMARY
        AdaptyUIDialogActionTypeResponse.SECONDARY -> AdaptyUIDialogActionType.SECONDARY
    }
}