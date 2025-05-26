package com.adapty.kmp.internal.plugin.request

import com.adapty.kmp.internal.utils.toJsonElement
import com.adapty.kmp.internal.utils.toMapOfAny
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

internal typealias AdaptyCustomAttributesRequestResponse = Map<String, JsonElement>

internal fun Map<String, Any?>?.toAdaptyCustomAttributesRequest(): AdaptyCustomAttributesRequestResponse {
    return this?.mapValues { (_, value) -> value.toJsonElement() } ?: emptyMap()
}


internal fun AdaptyCustomAttributesRequestResponse?.toTypedMap(): Map<String, Any> {
    if (this == null) return emptyMap()
    return JsonObject(this).toMapOfAny()
}


