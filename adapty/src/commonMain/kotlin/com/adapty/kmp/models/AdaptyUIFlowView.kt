package com.adapty.kmp.models

import com.adapty.kmp.AdaptyUI

/**
 * Represents a flow view in the Adapty UI (cross_platform 4.0.0).
 *
 * @property id The unique identifier of this flow view instance.
 * @property placementId The identifier of the placement.
 * @property variationId The identifier of the flow variation.
 */
public data class AdaptyUIFlowView internal constructor(
    val id: String,
    val placementId: String,
    val variationId: String
) {

    /**
     * Presents the flow view to the user.
     */
    public suspend fun present(
        iosPresentationStyle: AdaptyUIIOSPresentationStyle = AdaptyUIIOSPresentationStyle.FULLSCREEN
    ): AdaptyResult<Unit> {
        return AdaptyUI.presentFlowView(view = this, iosPresentationStyle = iosPresentationStyle)
    }

    /**
     * Dismisses the currently presented flow view.
     */
    public suspend fun dismiss(): AdaptyResult<Unit> {
        return AdaptyUI.dismissFlowView(this)
    }

    /**
     * Presents a dialog on top of the flow view.
     */
    public suspend fun showDialog(
        title: String,
        content: String,
        primaryActionTitle: String,
        secondaryActionTitle: String? = null
    ): AdaptyResult<AdaptyUIDialogActionType> {
        return AdaptyUI.showDialog(
            viewId = this.id,
            title = title,
            content = content,
            primaryActionTitle = primaryActionTitle,
            secondaryActionTitle = secondaryActionTitle
        )
    }
}
