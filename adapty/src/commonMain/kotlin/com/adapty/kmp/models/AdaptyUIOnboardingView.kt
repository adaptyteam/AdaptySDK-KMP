package com.adapty.kmp.models

import com.adapty.kmp.AdaptyUI
import com.adapty.kmp.models.AdaptyOnboarding.Companion.PREFIX_NATIVE_PLATFORM_VIEW

public data class AdaptyUIOnboardingView internal constructor(
    val id: String,
    val placementId: String,
    val variationId: String
) {

    val isStandaloneView: Boolean = !id.startsWith(PREFIX_NATIVE_PLATFORM_VIEW)


    public suspend fun present(): AdaptyResult<Unit> {
        return AdaptyUI.presentOnboardingView(this)
    }

    public suspend fun dismiss(): AdaptyResult<Unit> {
        return AdaptyUI.dismissOnboardingView(this)
    }

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