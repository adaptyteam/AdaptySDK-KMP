package com.adapty.kmp.models

/**
 * Represents a user profile in Adapty.
 *
 * Contains information about the user's subscriptions, access levels, non-subscription purchases,
 * custom attributes, and test device status.
 *
 * @property profileId The unique identifier of the profile in Adapty.
 * @property segmentId Internal segment identifier. Nullable.
 * @property customerUserId Optional identifier of the user in your system.
 * @property accessLevels A map of access level identifiers to [AccessLevel] objects.
 * The values are Can be null if the customer has no access levels.
 * @property subscriptions A map of product IDs to [Subscription] objects.
 * The values are information about subscriptions. Can be null if the customer has no subscriptions.
 * @property nonSubscriptions A map of product IDs to lists of [NonSubscription] purchases.
 * The keys are product ids from the store. The values are arrays of information about consumables.
 * Can be null if the customer has no purchases.
 * @property customAttributes User-defined key-value attributes previously set via `.updateProfile()`.
 * @property isTestUser Indicates whether the profile belongs to a test device. Read more about
 * test devices in [Adapty documentation](https://adapty.io/docs/test-devices).
 */
public data class AdaptyProfile internal constructor(
    public val profileId: String,
    internal val segmentId: String? = null,
    public val customerUserId: String?,
    public val accessLevels: Map<String, AccessLevel>,
    public val subscriptions: Map<String, Subscription>,
    public val nonSubscriptions: Map<String, List<NonSubscription>>,
    public val customAttributes: Map<String, Any>,
    public val isTestUser: Boolean,
) {

    /**
     * Represents a premium access level assigned to a user.
     *
     * @property id Identifier of the access level.
     * @property isActive Whether this access level is currently active.
     * @property vendorProductId Product identifier from App Store or Google Play.
     * @property offerId Promotional or introductory offer ID, if applicable.
     * @property store Store name (App Store, Google Play, etc.).
     * Possible values: `app_store`, `play_store`, `adapty`
     * @property activatedAt Activation timestamp in ISO 8601 format.
     * @property startsAt Subscription start date. Nullable.
     * @property renewedAt Time when the access level was renewed. It can be `null` if the
     * purchase was first in chain or it is non-renewing subscription / non-consumable (e.g. lifetime)
     * @property expiresAt Time when the access level will expire (could be in the past and could be `null` for lifetime access)
     * @property isLifetime Whether this is a lifetime access (no expiration date).
     * @property cancellationReason Reason for cancellation, if any.
     * @property isRefund Whether this access level was refunded.
     * @property activeIntroductoryOfferType Type of active introductory offer, if any.
     * If the value is not `null`, it means that the offer was applied during the current subscription period.
     * Possible values: `free_trial`, `pay_as_you_go`, `pay_up_front`
     * @property activePromotionalOfferType A type of an active promotional offer. If the value is not `null`,
     * it means that the offer was applied during the current subscription period.
     * Possible values: `free_trial`, `pay_as_you_go`, `pay_up_front`
     * @property activePromotionalOfferId ID of active promotional offer, if any.
     * @property willRenew Whether the subscription will automatically renew.
     * @property isInGracePeriod Whether the subscription is currently in grace period.
     * @property unsubscribedAt Time when the auto-renewable subscription was cancelled.
     * Subscription can still be active, it just means that auto-renewal turned off.
     * Will be set to `null` if the user reactivates the subscription.
     * @property billingIssueDetectedAt When a billing issue was detected, if any.
     * Subscription can still be active. Would be set to null if a charge is made.
     */
    public data class AccessLevel(
        public val id: String,
        public val isActive: Boolean,
        public val vendorProductId: String,
        public val offerId: String? = null,
        public val store: String,
        public val activatedAt: String,
        public val startsAt: String?,
        public val renewedAt: String?,
        public val expiresAt: String?,
        public val isLifetime: Boolean,
        public val cancellationReason: String?,
        public val isRefund: Boolean,
        public val activeIntroductoryOfferType: String?,
        public val activePromotionalOfferType: String?,
        public val activePromotionalOfferId: String?,
        public val willRenew: Boolean,
        public val isInGracePeriod: Boolean,
        public val unsubscribedAt: String?,
        public val billingIssueDetectedAt: String?
    )

    /**
     * Represents a subscription purchase of the user.
     *
     * @property isActive Whether the subscription is active.
     * @property vendorProductId An identifier of a product in a store that unlocked this subscription.
     * @property vendorTransactionId A transaction id of a purchase in a store that unlocked this subscription.
     * @property vendorOriginalTransactionId An original transaction id of the purchase in a store
     * that unlocked this subscription. For auto-renewable subscription, this will be an id of the
     * first transaction in this subscription
     * @property offerId Promotional or introductory offer ID, if any.
     * @property store Store name (App Store, Google Play, etc.). Possible values: `app_store`, `play_store`, `adapty`
     * @property activatedAt Activation date in ISO 8601 format.
     * @property renewedAt Time when the subscription was renewed. It can be `null` if the
     * purchase was first in chain or it is non-renewing subscription.
     * @property expiresAt Time when the access level will expire (could be in the past and could be `null` for lifetime access).
     * @property startsAt Time when the subscription has started (could be in the future).
     * @property isLifetime Whether this is a lifetime subscription.
     * @property activeIntroductoryOfferType Type of active introductory offer, if any.
     * If the value is not `null`, it means that the offer was applied during the current subscription period.
     * Possible values: `free_trial`, `pay_as_you_go`, `pay_up_front`
     * @property activePromotionalOfferType A type of an active promotional offer. If the value is not `null`,
     * it means that the offer was applied during the current subscription period.
     * Possible values: `free_trial`, `pay_as_you_go`, `pay_up_front`
     * @property activePromotionalOfferId Active promotional offer ID, if any.
     * @property willRenew Whether the subscription will automatically renew.
     * @property isInGracePeriod Whether the subscription is currently in grace period.
     * @property unsubscribedAt Time when the auto-renewable subscription was cancelled.
     * Subscription can still be active, it means that auto-renewal is turned off.
     * Would be `null` if a user reactivates the subscription.
     * @property billingIssueDetectedAt Time when a billing issue was detected. Subscription can still be active.
     * @property isSandbox Whether this subscription was made in a sandbox environment.
     * @property isRefund Whether this subscription was refunded.
     * @property cancellationReason Reason for cancellation, if any.
     */
    public data class Subscription(
        public val isActive: Boolean,
        public val vendorProductId: String,
        public val vendorTransactionId: String?,
        public val vendorOriginalTransactionId: String?,
        public val offerId: String?,
        public val store: String,
        public val activatedAt: String,
        public val renewedAt: String?,
        public val expiresAt: String?,
        public val startsAt: String?,
        public val isLifetime: Boolean,
        public val activeIntroductoryOfferType: String?,
        public val activePromotionalOfferType: String?,
        public val activePromotionalOfferId: String?,
        public val willRenew: Boolean,
        public val isInGracePeriod: Boolean,
        public val unsubscribedAt: String?,
        public val billingIssueDetectedAt: String?,
        public val isSandbox: Boolean,
        public val isRefund: Boolean,
        public val cancellationReason: String?
    )

    /**
     * Represents a non-subscription purchase (consumables or one-time purchases).
     *
     * @property purchaseId Adapty purchase ID. You can use it to ensure that you've already processed
     * this purchase (for example tracking one time products).
     * @property vendorProductId Product identifier from App Store or Google Play.
     * @property vendorTransactionId Transaction ID from the store.
     * @property store Store name (App Store, Google Play, etc.). Possible values: `app_store`, `play_store`, `adapty`
     * @property purchasedAt Purchase timestamp in ISO 8601 format.
     * @property isConsumable Whether the purchase is consumable.
     * @property isSandbox Whether this purchase was made in a sandbox environment.
     * @property isRefund Whether this purchase was refunded.
     */
    public data class NonSubscription(
        public val purchaseId: String,
        public val vendorProductId: String,
        public val vendorTransactionId: String?,
        public val store: String,
        public val purchasedAt: String,
        public val isConsumable: Boolean,
        public val isSandbox: Boolean,
        public val isRefund: Boolean
    )

    /**
     * Represents user gender.
     */
    public enum class Gender {
        MALE, FEMALE, OTHER;

        override fun toString(): String {
            return this.name[0].lowercaseChar().toString()
        }

        internal companion object {
            fun from(value: String?): Gender? {
                if (value == null) return null
                return entries.firstOrNull { it.toString() == value.lowercase() }
            }
        }
    }


    /**
     * Represents a date (year-month-day) for user attributes.
     */
    public class Date(
        internal val year: Int,
        internal val month: Int,
        internal val date: Int
    ) {

        override fun toString(): String {
            val mm = month.toString().padStart(2, '0')
            val dd = date.toString().padStart(2, '0')
            return "$year-$mm-$dd"
        }

        internal companion object {
            fun from(value: String?): Date? {
                if (value == null) return null

                val parts = value.trim().split("-")
                if (parts.size != 3) return null

                val year = parts[0].toIntOrNull()
                val month = parts[1].toIntOrNull()
                val day = parts[2].toIntOrNull()

                if (year == null || month == null || day == null) return null

                return Date(year, month, day)
            }
        }
    }
}