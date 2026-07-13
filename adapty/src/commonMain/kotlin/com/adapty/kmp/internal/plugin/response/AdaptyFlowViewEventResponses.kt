package com.adapty.kmp.internal.plugin.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/** `flow_view_did_ask_permission` event payload (cross_platform 4.0.0). */
@Serializable
internal data class AdaptyFlowViewDidAskPermissionResponse(
    @SerialName("view")
    val view: AdaptyUIPaywallViewResponse,

    @SerialName("event_id")
    val eventId: String,

    @SerialName("permission")
    val permission: String,

    @SerialName("custom_args")
    val customArgs: Map<String, String>? = null,
)

/** `flow_view_observer_did_initiate_purchase` event payload (cross_platform 4.0.0). */
@Serializable
internal data class AdaptyFlowViewObserverDidInitiatePurchaseResponse(
    @SerialName("view")
    val view: AdaptyUIPaywallViewResponse,

    @SerialName("event_id")
    val eventId: String,

    @SerialName("product")
    val product: AdaptyPaywallProductResponse,
)

/** `flow_view_observer_did_initiate_restore` event payload (cross_platform 4.0.0). */
@Serializable
internal data class AdaptyFlowViewObserverDidInitiateRestoreResponse(
    @SerialName("view")
    val view: AdaptyUIPaywallViewResponse,

    @SerialName("event_id")
    val eventId: String,
)

/** `flow_view_did_receive_analytic_event` event payload (cross_platform 4.0.0). */
@Serializable
internal data class AdaptyFlowViewDidReceiveAnalyticEventResponse(
    @SerialName("view")
    val view: AdaptyUIPaywallViewResponse,

    @SerialName("name")
    val name: String,

    @SerialName("params")
    val params: JsonObject? = null,
)
