package com.adapty.kmp.models

/**
 * Represents the developer’s preference for how refund requests should be handled on iOS.
 *
 * Use this when updating a user's refund preference via
 * [com.adapty.kmp.Adapty.updateRefundPreference].
 *
 * This setting allows you to guide Apple’s refund handling behavior
 * when processing refund requests for in-app purchases or subscriptions.
 *
 */
public enum class AdaptyIosRefundPreference {
    /** No specific preference. The system decides how to handle refund requests. */
    NO_PREFERENCE,

    /** Suggests that refund requests should generally be **granted**. */
    GRANT,

    /** Suggests that refund requests should generally be **declined**. */
    DECLINE
}