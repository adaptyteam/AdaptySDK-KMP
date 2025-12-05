package com.adapty.kmp.models

/**
 * Represents a dialog that can be shown in the UI, typically for alerts, confirmations, or information.
 *
 * @property primaryActionTitle The title of the primary action button.
 * @property title The optional dialog title.
 * @property content The optional dialog content/message.
 * @property secondaryActionTitle The optional secondary action button title.
 */
public data class AdaptyUIDialog(
    val primaryActionTitle: String,
    val title: String? = null,
    val content: String? = null,
    val secondaryActionTitle: String? = null
)

/**
 * Represents the type of action taken by the user on an [AdaptyUIDialog].
 */
public enum class AdaptyUIDialogActionType {
    /** User tapped the primary action button. */
    PRIMARY,

    /** User tapped the secondary action button. */
    SECONDARY,
}

