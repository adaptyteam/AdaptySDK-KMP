package com.adapty.kmp.internal.plugin.response

import com.adapty.kmp.models.AdaptyUIAction
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("type")
internal sealed interface AdaptyUIActionResponse {
    @Serializable
    @SerialName("close")
    data object Close : AdaptyUIActionResponse

    @Serializable
    @SerialName("system_back")
    data object SystemBack : AdaptyUIActionResponse

    @Serializable
    @SerialName("open_url")
    data class OpenUrl(val value: String) : AdaptyUIActionResponse

    @Serializable
    @SerialName("custom")
    data class Custom(val value: String) : AdaptyUIActionResponse
}

internal fun AdaptyUIActionResponse.asAdaptyUIAction(): AdaptyUIAction {
    return when (this) {
        AdaptyUIActionResponse.Close -> AdaptyUIAction.CloseAction
        is AdaptyUIActionResponse.Custom -> AdaptyUIAction.CustomAction(this.value)
        is AdaptyUIActionResponse.OpenUrl -> AdaptyUIAction.OpenUrlAction(this.value)
        AdaptyUIActionResponse.SystemBack -> AdaptyUIAction.AndroidSystemBackAction
    }
}
