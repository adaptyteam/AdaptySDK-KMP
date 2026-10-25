package com.adapty.kmp.models

import com.adapty.kmp.AdaptyUI

/**
 * Represents a flow view in the Adapty UI (cross_platform 4.0.0).
 *
 * @property id The unique identifier of this flow view instance.
 * @property placementId The identifier of the placement.
 * @property variationId The identifier of the flow variation.
 * @property locale The localization the view was actually built with: the locale passed to
 *   `createFlowView` when the flow has that localization, `en` when no locale was passed and the
 *   flow has `en`, and the flow's default localization in every other case.
 */
public data class AdaptyUIFlowView internal constructor(
    val id: String,
    val placementId: String,
    val variationId: String,
    val locale: String? = null
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
     *
     * By default ([destroy] = `true`) the native view is released along with the dismissal, and
     * this view cannot be presented again.
     *
     * Pass `destroy = false` to keep the native view alive instead: [present] shows it again,
     * resuming on the screen the user left and with the state the flow had built up. Event
     * observers are global, so nothing needs to be re-registered. A view kept this way is held in
     * memory until [destroy] is called.
     *
     * ```kotlin
     * view.dismiss(destroy = false) // hide, keep the view and its state
     * view.present()                // resumes where the user left off
     * view.dismiss()                // hide and release for good
     * ```
     *
     * **Behavior change (4.3.0):** earlier versions always kept the dismissed view alive
     * (equivalent to `destroy = false`) with no way to release it. The default is now `true`.
     *
     * @param destroy Whether to release the native view after it is dismissed. Defaults to `true`.
     * @return [AdaptyResult] indicating success or failure.
     */
    public suspend fun dismiss(destroy: Boolean = true): AdaptyResult<Unit> {
        return AdaptyUI.dismissFlowView(view = this, destroy = destroy)
    }

    /**
     * Releases the native view kept alive by `dismiss(destroy = false)`.
     *
     * Not needed after a default [dismiss], which already releases the view. After this call the
     * view cannot be presented.
     *
     * @return [AdaptyResult] indicating success or failure (for example if the view was already
     * released).
     */
    public suspend fun destroy(): AdaptyResult<Unit> {
        return AdaptyUI.destroyFlowView(this)
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
