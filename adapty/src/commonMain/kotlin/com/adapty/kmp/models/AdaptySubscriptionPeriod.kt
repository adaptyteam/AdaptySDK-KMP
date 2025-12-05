package com.adapty.kmp.models

/**
 * Represents the duration of a subscription period.
 *
 * @property unit The unit of time for the period (day, week, month, year, unknown).
 * @property numberOfUnits The number of units for this period.
 */
public data class AdaptySubscriptionPeriod internal constructor(
    val unit: AdaptyPeriodUnit,
    val numberOfUnits: Int
)