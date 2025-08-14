package com.adapty.exampleapp

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

object Constants {
    const val EXAMPLE_PAYWALL_ID = "example_ab_test"
    const val TEST_TRANSACTION_ID = "test_transaction_id"
}


//Format for date time: YYYY-MM-DDTHH:mm:ss.sssZ
fun Instant.toFormattedDateTimeString(): String {
    val utcTime = this.toLocalDateTime(TimeZone.UTC)

    // Milliseconds (3 digits)
    val ms = (this.nanosecondsOfSecond / 1_000_000).toString().padStart(3, '0')
    val offset = "+0000" // UTC offset for UTC

    return "${utcTime.year}-${utcTime.monthNumber.toString().padStart(2,'0')}-" +
            "${utcTime.dayOfMonth.toString().padStart(2,'0')}T" +
            "${utcTime.hour.toString().padStart(2,'0')}:" +
            "${utcTime.minute.toString().padStart(2,'0')}:" +
            "${utcTime.second.toString().padStart(2,'0')}.$ms$offset"
}