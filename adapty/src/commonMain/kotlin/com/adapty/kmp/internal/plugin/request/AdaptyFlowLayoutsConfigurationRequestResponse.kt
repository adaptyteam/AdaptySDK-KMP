package com.adapty.kmp.internal.plugin.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/**
 * Wire representation of `AdaptyFlow.ui_schema` (cross_platform 4.1.2).
 *
 * Describes which custom layouts a flow declares and how its grids map onto platforms, devices
 * and breakpoints. Nothing on the KMP side consumes it — it is carried through so the flow
 * round-trips losslessly back to native on `log_show_flow` and `adapty_ui_create_flow_view`.
 *
 * Named after the iOS SDK's `AdaptyFlow.LayoutsConfiguration` rather than the wire key, matching
 * how iOS models the same field.
 *
 * [Grid.platforms] and [Grid.devices] are `oneOf: "all" | [..]` on the wire, so they are held as
 * raw [JsonElement] rather than modelled into a closed type — an unknown platform or device id
 * must survive the round-trip unchanged.
 */
@Serializable
internal data class AdaptyFlowLayoutsConfigurationRequestResponse(
    @SerialName("layouts")
    val layouts: List<Layout> = emptyList(),

    @SerialName("grids")
    val grids: List<Grid> = emptyList(),
) {
    @Serializable
    internal data class Layout(
        @SerialName("flow_layout_id")
        val flowLayoutId: String
    )

    @Serializable
    internal data class Grid(
        @SerialName("platforms")
        val platforms: JsonElement? = null,

        @SerialName("devices")
        val devices: JsonElement? = null,

        @SerialName("custom_id")
        val customId: String? = null,

        @SerialName("h_breakpoints")
        val horizontalBreakpoints: List<Int> = emptyList(),

        @SerialName("v_breakpoints")
        val verticalBreakpoints: List<Int> = emptyList(),

        @SerialName("cells")
        val cells: List<Int> = emptyList(),
    )
}
