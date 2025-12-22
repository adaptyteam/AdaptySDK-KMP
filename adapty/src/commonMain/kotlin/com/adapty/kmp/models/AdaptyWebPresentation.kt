package com.adapty.kmp.models

/**
 * Defines how a web-based Adapty paywall should be presented to the user.
 *
 * Use [EXTERNAL_BROWSER] to open the paywall in the device's default browser,
 * or [IN_APP_BROWSER] to display it inside the app using an in-app web view
 * (e.g., Custom Tabs / SFSafariViewController).
 */
public enum class AdaptyWebPresentation {
    /** Opens the web paywall in the device's external browser. */
    EXTERNAL_BROWSER,

    /** Opens the web paywall inside the app using an in-app browser. */
    IN_APP_BROWSER
}
