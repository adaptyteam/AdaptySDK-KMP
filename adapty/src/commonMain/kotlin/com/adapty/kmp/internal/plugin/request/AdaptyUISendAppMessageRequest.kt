package com.adapty.kmp.internal.plugin.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
internal data class AdaptyUISendAppMessageRequest(
    @SerialName("id") val id: String,
    @SerialName("message_id") val messageId: String,
    @SerialName("payload") val payload: JsonObject,
)
