package com.adapty.kmp.internal.plugin.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** `flow_view_did_answer_permission` request (cross_platform 4.0.0). */
@Serializable
internal data class AdaptyFlowAnswerPermissionRequest(
    @SerialName("event_id") val eventId: String,
    @SerialName("status") val status: String, // "granted" | "denied"
    @SerialName("detail") val detail: String? = null,
)

/**
 * Shared request for the observer-mode handshake methods
 * (`observer_purchase_did_start/finish`, `observer_restore_did_start/finish`).
 */
@Serializable
internal data class AdaptyObserverEventRequest(
    @SerialName("event_id") val eventId: String,
)
