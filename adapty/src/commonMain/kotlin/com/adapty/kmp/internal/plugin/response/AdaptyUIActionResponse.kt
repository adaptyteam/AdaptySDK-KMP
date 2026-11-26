package com.adapty.kmp.internal.plugin.response

import com.adapty.kmp.internal.plugin.request.AdaptyWebPresentationRequest
import com.adapty.kmp.internal.plugin.request.asAdaptyWebPresentation
import com.adapty.kmp.internal.utils.toMapOfAnyOrNull
import com.adapty.kmp.models.AdaptyUIAction
import com.adapty.kmp.models.AdaptyWebPresentation
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator
import kotlinx.serialization.json.JsonObject

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
    data class OpenUrl(
        val value: String,
        @SerialName("open_in")
        val openIn: AdaptyWebPresentationRequest? = null,
    ) : AdaptyUIActionResponse

    @Serializable
    @SerialName("custom")
    data class Custom(
        val value: String,
        val arguments: JsonObject? = null,
    ) : AdaptyUIActionResponse
}

internal fun AdaptyUIActionResponse.asAdaptyUIAction(): AdaptyUIAction {
    return when (this) {
        AdaptyUIActionResponse.Close -> AdaptyUIAction.CloseAction
        is AdaptyUIActionResponse.Custom -> AdaptyUIAction.CustomAction(
            action = this.value,
            arguments = this.arguments?.toMapOfAnyOrNull(),
        )
        is AdaptyUIActionResponse.OpenUrl -> AdaptyUIAction.OpenUrlAction(
            this.value,
            this.openIn?.asAdaptyWebPresentation() ?: AdaptyWebPresentation.EXTERNAL_BROWSER,
        )
        AdaptyUIActionResponse.SystemBack -> AdaptyUIAction.AndroidSystemBackAction
    }
}
