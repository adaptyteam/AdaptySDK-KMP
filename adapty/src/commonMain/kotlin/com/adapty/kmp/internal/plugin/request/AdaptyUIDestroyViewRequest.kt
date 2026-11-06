package com.adapty.kmp.internal.plugin.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Request for `adapty_ui_destroy_flow_view`: carries only the view id. */
@Serializable
internal data class AdaptyUIDestroyViewRequest(
    @SerialName("id") val id: String,
)
