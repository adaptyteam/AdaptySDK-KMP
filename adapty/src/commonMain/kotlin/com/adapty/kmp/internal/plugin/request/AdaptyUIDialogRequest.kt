package com.adapty.kmp.internal.plugin.request

import com.adapty.kmp.models.AdaptyUIDialog
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptyUIDialogRequest(
    @SerialName("title")
    val title: String? = null,

    @SerialName("content")
    val content: String? = null,

    @SerialName("default_action_title")
    val defaultActionTitle: String,

    @SerialName("secondary_action_title")
    val secondaryActionTitle: String? = null
)

internal fun AdaptyUIDialog.asAdaptyUIDialogConfigurationRequest(): AdaptyUIDialogRequest {

    return AdaptyUIDialogRequest(
        title = this.title,
        content = this.content,
        defaultActionTitle = this.primaryActionTitle,
        secondaryActionTitle = this.secondaryActionTitle
    )
}
