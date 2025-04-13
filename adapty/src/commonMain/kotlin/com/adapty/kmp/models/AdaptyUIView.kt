package com.adapty.kmp.models

import com.adapty.kmp.AdaptyUI

public data class AdaptyUIView internal constructor(
    val id: String,
    val placementId: String,
    val paywallVariationId: String
) {
    public fun present() {
        AdaptyUI.presentPaywallView(this)
    }

    public fun dismiss() {
        AdaptyUI.dismissPaywallView(this)
    }

    public suspend fun showDialog(
        title: String,
        content: String,
        primaryActionTitle: String,
        secondaryActionTitle: String? = null
    ): AdaptyUIDialogActionType {

        return AdaptyUI.showDialog(
            view = this,
            title = title,
            content = content,
            primaryActionTitle = primaryActionTitle,
            secondaryActionTitle = secondaryActionTitle
        )
    }
}