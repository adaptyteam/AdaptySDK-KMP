package com.adapty.kmp.models

/**
 * The reason the store wants to show a store message (cross_platform 4.3.1).
 *
 * On iOS these map to StoreKit's `Message.Reason`. The known reasons are exposed as constants below,
 * but any [value] is valid: reasons the SDK does not know yet arrive as `storekit_<rawValue>`
 * (for example `storekit_7`) and are carried through unchanged, so they can be passed back to
 * [com.adapty.kmp.Adapty.showStoreMessages] as a filter. Compare against the constants, and fall
 * back to [value] for anything else:
 *
 * ```
 * when (type) {
 *     AdaptyStoreMessageType.BILLING_ISSUE -> explainBillingIssue()
 *     AdaptyStoreMessageType.PRICE_INCREASE_CONSENT -> explainPriceIncrease()
 *     else -> log("Unhandled store message: ${type.value}")
 * }
 * ```
 *
 * Requires iOS 16+.
 *
 * @property value the raw store message type identifier as sent by the native SDK.
 */
public class AdaptyStoreMessageType(public val value: String) {

    public companion object {
        /** A generic store message. */
        public val GENERIC: AdaptyStoreMessageType = AdaptyStoreMessageType("generic")

        /** The user needs to consent to a subscription price increase. */
        public val PRICE_INCREASE_CONSENT: AdaptyStoreMessageType =
            AdaptyStoreMessageType("price_increase_consent")

        /** The user's payment method failed and needs to be updated. */
        public val BILLING_ISSUE: AdaptyStoreMessageType = AdaptyStoreMessageType("billing_issue")

        /** A win-back offer for a lapsed subscriber. */
        public val WIN_BACK_OFFER: AdaptyStoreMessageType = AdaptyStoreMessageType("win_back_offer")
    }

    override fun equals(other: Any?): Boolean =
        this === other || (other is AdaptyStoreMessageType && other.value == value)

    override fun hashCode(): Int = value.hashCode()

    override fun toString(): String = "AdaptyStoreMessageType($value)"
}
