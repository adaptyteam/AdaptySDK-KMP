package com.adapty.kmp.models

import com.adapty.kmp.AdaptyUI
import com.adapty.kmp.models.AdaptyOnboarding.Companion.PREFIX_NATIVE_PLATFORM_VIEW

/**
 * Represents an onboarding view in the Adapty UI.
 *
 * An onboarding can be presented either as a standalone screen (modal) or embedded
 * as a platform view inside your UI.
 *
 * Standalone screens allow users to dismiss using native gestures (swipe down on iOS,
 * back button on Android) and are ideal for optional onboardings.
 * Embedded platform views give full control over dismissal and are ideal for required flows.
 *
 * @property id The unique identifier of this onboarding view instance.
 * @property placementId The identifier of the placement where this onboarding is assigned.
 * @property variationId The variation identifier of the onboarding.
 */
public data class AdaptyUIOnboardingView internal constructor(
    val id: String,
    val placementId: String,
    val variationId: String
) {

    /**
     * Returns `true` if this onboarding is a standalone modal screen.
     * `false` indicates it is an embedded platform view.
     */
    val isStandaloneView: Boolean = !id.startsWith(PREFIX_NATIVE_PLATFORM_VIEW)


    /**
     * Presents the onboarding view to the user as a standalone screen.
     *
     * Each view can only be used once.
     *
     * ### Event Handling
     * Use a global event observer via [AdaptyUI.setOnboardingsEventsObserver] for handling events before presenting.
     * @return [AdaptyResult] indicating success or failure.
     */
    public suspend fun present(): AdaptyResult<Unit> {
        return AdaptyUI.presentOnboardingView(this)
    }

    /**
     * Dismisses the currently presented onboarding view.
     *
     * @return [AdaptyResult] indicating success or failure of the operation.
     */
    public suspend fun dismiss(): AdaptyResult<Unit> {
        return AdaptyUI.dismissOnboardingView(this)
    }

    /**
     * Presents a dialog on top of the onboarding view.
     *
     * @param title The dialog's title.
     * @param content Descriptive text providing details about the dialog.
     * @param primaryActionTitle The title for the primary action button.
     * @param secondaryActionTitle Optional title for a secondary action button.
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