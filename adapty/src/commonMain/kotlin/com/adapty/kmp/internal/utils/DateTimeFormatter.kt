package com.adapty.kmp.internal.utils

import com.adapty.kmp.internal.logger
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern

private const val ADAPTY_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"

internal fun Map<String, LocalDateTime>.asAdaptyValidDateTimeFormat(): Map<String, String> {
    return this.mapValues { (_, localDateTime) -> localDateTime.asAdaptyValidDateTimeFormat() }
}

//LocalDateTime should be in UTC format
internal fun LocalDateTime.asAdaptyValidDateTimeFormat(): String {
    // Milliseconds (3 digits)
    val ms = (this.nanosecond / 1_000_000).toString().padStart(3, '0')
    val offset = "Z" // UTC offset for UTC

    return "${this.year}-${this.monthNumber.toString().padStart(2, '0')}-" +
            "${this.dayOfMonth.toString().padStart(2, '0')}T" +
            "${this.hour.toString().padStart(2, '0')}:" +
            "${this.minute.toString().padStart(2, '0')}:" +
            "${this.second.toString().padStart(2, '0')}.$ms$offset"

}

@OptIn(FormatStringsInDatetimeFormats::class)
internal fun String.parseAdaptyDateTimeToLocalDateTime(): LocalDateTime {
    return try {
        val cleaned = this.removeSuffix("Z")
        val dateTimeFormat = LocalDateTime.Format {
            byUnicodePattern(ADAPTY_DATE_TIME_FORMAT.removeSuffix("Z"))
        }
        dateTimeFormat.parse(cleaned)
    } catch (e: IllegalArgumentException) {
        logger.log("Error while parsing date time: $e")
        throw e
    }
}