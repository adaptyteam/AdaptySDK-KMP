package com.adapty.kmp.internal.plugin.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Shared by `adapty_ui_dismiss_flow_view` (where `destroy` is required) and
 * `adapty_ui_dismiss_onboarding_view` (where it is optional, default `false`). There is no
 * default here so every caller states it explicitly and it is always serialized.
 */
@Serializable
internal data class AdaptyUIDismissViewRequest(
    @SerialName("id") val id: String,
    @SerialName("destroy") val destroy: Boolean,
)
