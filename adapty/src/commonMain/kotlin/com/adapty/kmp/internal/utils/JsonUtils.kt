package com.adapty.kmp.internal.utils

import com.adapty.kmp.internal.logger
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.double
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.int
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.long
import kotlinx.serialization.json.longOrNull


internal fun getEmptyJsonObjectString(): String = "{}"

internal val jsonInstance = Json {
    encodeDefaults = true
    prettyPrint = true
    ignoreUnknownKeys = true
    explicitNulls = false
}

internal inline fun <reified T> String?.decodeJsonString(): T? {
    return try {
        val json = jsonInstance
        val jsonString = this ?: return null
        val jsonElement = json.parseToJsonElement(jsonString).jsonObject
        json.decodeFromJsonElement<T>(jsonElement)
    } catch (e: Exception) {
        logger.log("JsonUtils decodeJsonString error: $e")
        return null
    }
}

internal fun Map<String, Any?>?.toJsonObject(): JsonObject = buildJsonObject {
    if (this@toJsonObject == null) return@buildJsonObject
    for ((key, value) in this@toJsonObject) {
        put(key, value.toJsonElement())
    }
}

internal fun Any?.toJsonElement(): JsonElement = when (this) {
    null -> JsonNull
    is Boolean -> JsonPrimitive(this)
    is Number -> JsonPrimitive(this)
    is String -> JsonPrimitive(this)
    else -> JsonPrimitive(this.toString())
}

internal fun String.convertJsonToMapOfAny(): Map<String, Any> = runCatching {
    if (this.isEmpty()) return emptyMap()
    jsonInstance.parseToJsonElement(this).jsonObject.toMapOfAny()
}.getOrNull() ?: emptyMap()


internal fun JsonObject.toMapOfAny(): Map<String, Any> = this.mapValues { (_, value) ->
    when (value) {
        is JsonPrimitive -> {
            when {
                value.isString -> value.content
                value.booleanOrNull != null -> value.boolean
                value.intOrNull != null -> value.int
                value.longOrNull != null -> value.long
                value.doubleOrNull != null -> value.double
                else -> value.content
            }
        }

        is JsonObject -> value.toMapOfAny()
        is JsonArray -> value.map { element ->

            when (element) {
                is JsonPrimitive -> element.contentOrNull ?: element.toString()
                is JsonObject -> element.toMapOfAny()
                else -> element.toString()
            }
        }

        else -> value.toString()
    }
}