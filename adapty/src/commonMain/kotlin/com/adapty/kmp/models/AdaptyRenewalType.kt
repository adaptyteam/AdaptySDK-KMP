package com.adapty.kmp.models

/**
 * Represents the type of subscription renewal.
 */
public enum class AdaptyRenewalType {
    /** A prepaid subscription that does not automatically renew. */
    PREPAID,

    /** A subscription that automatically renews at the end of each period. */
    AUTORENEWABLE
}