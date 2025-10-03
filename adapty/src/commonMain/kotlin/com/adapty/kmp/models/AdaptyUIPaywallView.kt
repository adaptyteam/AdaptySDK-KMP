package com.adapty.kmp.models

import com.adapty.kmp.AdaptyUI

public data class AdaptyUIPaywallView internal constructor(
    val id: String,
    val placementId: String,
    val variationId: String
) {
    public suspend fun present(): AdaptyResult<Unit> {
        return AdaptyUI.presentPaywallView(this)
    }

    public suspend fun dismiss(): AdaptyResult<Unit> {
        return AdaptyUI.dismissPaywallView(this)
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