package com.adapty.kmp.models

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