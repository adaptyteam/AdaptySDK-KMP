package com.adapty.kmp.internal.plugin.request

import com.adapty.kmp.models.AdaptyStoreMessageType
import com.adapty.kmp.models.AdaptyStoreMessagesHandling
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * `StoreMessagesHandling` (cross_platform 4.3.1). The wire enum also accepts `default`, which native
 * resolves to `auto`; the KMP API leaves the field out instead, so `default` is never sent.
 */
@Serializable
internal enum class AdaptyStoreMessagesHandlingRequest {
    @SerialName("auto")
    AUTO,

    @SerialName("manual")
    MANUAL
}

internal fun AdaptyStoreMessagesHandling.asAdaptyStoreMessagesHandlingRequest(): AdaptyStoreMessagesHandlingRequest =
    when (this) {
        AdaptyStoreMessagesHandling.AUTO -> AdaptyStoreMessagesHandlingRequest.AUTO
        AdaptyStoreMessagesHandling.MANUAL -> AdaptyStoreMessagesHandlingRequest.MANUAL
    }

/**
 * `ShowStoreMessage.Request` (cross_platform 4.3.1). A `null` [filter] is omitted from the JSON
 * (`explicitNulls = false`), which native reads as "show every pending message"; an empty list is
 * sent as `[]` and shows nothing.
 */
@Serializable
internal data class AdaptyShowStoreMessagesRequest(
    @SerialName("filter") val filter: List<String>? = null
)

/** `AdaptyStoreMessageType` is an open string on the wire; the raw [AdaptyStoreMessageType.value] is sent verbatim. */
internal fun AdaptyStoreMessageType.asAdaptyStoreMessageTypeRequest(): String = value
