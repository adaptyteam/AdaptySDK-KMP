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
     * @property openIn How the URL should be presented — in an external browser or in-app browser.
     */
    public data class OpenUrlAction(
        val url: String,
        val openIn: AdaptyWebPresentation = AdaptyWebPresentation.EXTERNAL_BROWSER,
    ) : AdaptyUIAction

    /**
     * User tapped a button that triggers a custom action.
     *
     * @property action A string representing the custom action identifier.
     * @property arguments The arguments the flow passed with this action, or `null` if it passed
     * none. Values are `String`, `Boolean`, `Number`, `null`, `List` or nested `Map`. Read numbers
     * as [Number]: a whole number can arrive as a `Double`. Treat a missing key like a `null` value.
     */
    public data class CustomAction(
        val action: String,
        val arguments: Map<String, Any?>? = null,
    ) : AdaptyUIAction
}
