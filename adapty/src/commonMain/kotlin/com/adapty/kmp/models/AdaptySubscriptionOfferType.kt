package com.adapty.kmp.models

/**
 * The type of a subscription offer.
 */
public enum class AdaptySubscriptionOfferType {
    /** An introductory offer, usually for first-time subscribers. */
    INTRODUCTORY,

    /** A promotional offer, for marketing campaigns or special discounts. */
    PROMOTIONAL,

    /** A win-back offer, used to re-engage lapsed subscribers. */
    WINBACK,

    /** An offer redeemed via an offer code (iOS only). */
    CODE
}