package com.adapty.kmp.models

public data class AdaptyUIDialog(
    val primaryActionTitle: String,
    val title: String? = null,
    val content: String? = null,
    val secondaryActionTitle: String? = null
)

public enum class AdaptyUIDialogActionType {
    PRIMARY,
    SECONDARY,
}

