package com.adapty.kmp.models

/**
 * Represents a UI action performed by the user on Adapty paywalls or onboarding screens.
 */
public sealed interface AdaptyUIAction {
    /** User pressed the close button. */
    public data object CloseAction : AdaptyUIAction

    /** User pressed the Android system back button. */
    public data object AndroidSystemBackAction : AdaptyUIAction

    /**
     * User tapped a button that opens a URL.
     *
     * @property url The URL to open.
     */
    public data class OpenUrlAction(val url: String) : AdaptyUIAction

    /**
     * User tapped a button that triggers a custom action.
     *
     * @property action A string representing the custom action identifier.
     */
    public data class CustomAction(val action: String) : AdaptyUIAction
}
