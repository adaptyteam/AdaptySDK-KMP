package com.adapty.kmp.models

import com.adapty.models.AdaptyProfile as AdaptyProfileAndroid

internal fun AdaptyProfileAndroid.asAdaptyProfile(): AdaptyProfile {
    return AdaptyProfile(
        profileId = profileId,
        customerUserId = customerUserId,
        accessLevels = accessLevels.entries.associate { entry -> entry.key to entry.value.asAdaptyAccessLevel() },
        subscriptions = subscriptions.entries.associate { entry -> entry.key to entry.value.asAdaptySubscription() },
        nonSubscriptions = nonSubscriptions.entries.associate { entry -> entry.key to entry.value.map { it.asAdaptyNonSubscription() } },
        customAttributes = customAttributes.entries.associate { entry -> entry.key to entry.value },
        isTestUser = false //TODO fix this make internal usage
    )
}

internal fun AdaptyProfileAndroid.AccessLevel.asAdaptyAccessLevel(): AdaptyProfile.AccessLevel {
    return AdaptyProfile.AccessLevel(
        id = id,
        isActive = isActive,
        vendorProductId = vendorProductId,
        offerId = offerId,
        store = store,
        activatedAt = activatedAt,
        startsAt = startsAt,
        renewedAt = renewedAt,
        expiresAt = expiresAt,
        isLifetime = isLifetime,
        cancellationReason = cancellationReason,
        isRefund = isRefund,
        activeIntroductoryOfferType = activeIntroductoryOfferType,
        activePromotionalOfferType = activePromotionalOfferType,
        activePromotionalOfferId = activePromotionalOfferId,
        willRenew = willRenew,
        isInGracePeriod = isInGracePeriod,
        unsubscribedAt = unsubscribedAt,
        billingIssueDetectedAt = billingIssueDetectedAt
    )
}

internal fun AdaptyProfileAndroid.NonSubscription.asAdaptyNonSubscription(): AdaptyProfile.NonSubscription {
    return AdaptyProfile.NonSubscription(
        purchaseId = purchaseId,
        vendorProductId = vendorProductId,
        vendorTransactionId = vendorTransactionId,
        store = store,
        purchasedAt = purchasedAt,
        isConsumable = isConsumable,
        isSandbox = isSandbox,
        isRefund = isRefund
    )
}

internal fun AdaptyProfileAndroid.Subscription.asAdaptySubscription(): AdaptyProfile.Subscription {
    return AdaptyProfile.Subscription(
        isActive = isActive,
        vendorProductId = vendorProductId,
        vendorTransactionId = vendorTransactionId,
        vendorOriginalTransactionId = vendorOriginalTransactionId,
        offerId = offerId,
        store = store,
        activatedAt = activatedAt,
        renewedAt = renewedAt,
        expiresAt = expiresAt,
        startsAt = startsAt,
        isLifetime = isLifetime,
        activeIntroductoryOfferType = activeIntroductoryOfferType,
        activePromotionalOfferType = activePromotionalOfferType,
        activePromotionalOfferId = activePromotionalOfferId,
        willRenew = willRenew,
        isInGracePeriod = isInGracePeriod,
        unsubscribedAt = unsubscribedAt,
        billingIssueDetectedAt = billingIssueDetectedAt,
        isSandbox = isSandbox,
        isRefund = isRefund,
        cancellationReason = cancellationReason
    )
}

internal fun AdaptyProfile.Gender.asAdaptyGenderAndroid(): AdaptyProfileAndroid.Gender {
    return when (this) {
        AdaptyProfile.Gender.MALE -> AdaptyProfileAndroid.Gender.MALE
        AdaptyProfile.Gender.FEMALE -> AdaptyProfileAndroid.Gender.FEMALE
        AdaptyProfile.Gender.OTHER -> AdaptyProfileAndroid.Gender.OTHER
    }
}

internal fun AdaptyProfile.Date.asAdaptyDateAndroid(): AdaptyProfileAndroid.Date {
    return AdaptyProfileAndroid.Date(year = year, month = month, date = date)
}