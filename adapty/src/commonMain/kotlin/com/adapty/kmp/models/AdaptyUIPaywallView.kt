package com.adapty.kmp.models

import com.adapty.kmp.AdaptyUI

/**
 * Represents a paywall view in the Adapty UI.
 *
 * @property id The unique identifier of this paywall view instance.
 * @property placementId The identifier of the paywall placement.
 * @property variationId The identifier of the paywall variation.
 */
public data class AdaptyUIPaywallView internal constructor(
    val id: String,
    val placementId: String,
    val variationId: String
) {

    /**
     * Presents the paywall view to the user.
     *
     * @return [AdaptyResult] indicating success or failure of the operation.
     */
    public suspend fun present(iosPresentationStyle: AdaptyUIIOSPresentationStyle = AdaptyUIIOSPresentationStyle.FULLSCREEN): AdaptyResult<Unit> {
        return AdaptyUI.presentPaywallView(view = this, iosPresentationStyle = iosPresentationStyle)
    }

    /**
     * Dismisses the currently presented paywall view.
     *
     * @return [AdaptyResult] indicating success or failure of the operation.
     */
    public suspend fun dismiss(): AdaptyResult<Unit> {
        return AdaptyUI.dismissPaywallView(this)
    }

    /**
     * Presents a dialog on top of the paywall view.
     *
     * @param title The dialog's title.
     * @param content The content/message of the dialog.
     * @param primaryActionTitle The title of the primary action button.
     * @param secondaryActionTitle Optional title of the secondary action button.
     *
     * @return [AdaptyResult] with [AdaptyUIDialogActionType] indicating which action the user selected.
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