package com.adapty.kmp.models

/**
 * Controls who decides when store messages are shown: App Store messages (price increase consent,
 * billing issue, win-back offer, etc.) on iOS, Google Play in-app messages (e.g. a declined
 * payment) on Android.
 *
 * Set it with [AdaptyConfig.Builder.withStoreMessagesHandling]. When it is not set, the native
 * SDK default ([AUTO]) applies.
 *
 * Has no effect below iOS 16.
 */
public enum class AdaptyStoreMessagesHandling {
    /**
     * The default. iOS: StoreKit presents messages automatically. Android: the SDK requests
     * Google Play in-app messages on every `Activity.onResume`.
     */
    AUTO,

    /**
     * Messages are shown only when the app calls [com.adapty.kmp.Adapty.showStoreMessages], so
     * they don't interrupt onboarding, a purchase or other critical flows. On iOS the SDK also keeps
     * them in a queue readable via [com.adapty.kmp.Adapty.getPendingStoreMessageTypes].
     */
    MANUAL
}
