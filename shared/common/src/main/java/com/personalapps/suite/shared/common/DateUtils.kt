package com.personalapps.suite.shared.common

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object DateUtils {
    private val defaultFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        .withZone(ZoneId.systemDefault())

    private val dateOnlyFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        .withZone(ZoneId.systemDefault())

    fun formatDateTime(instant: Instant): String {
        return defaultFormatter.format(instant)
    }

    fun formatDateOnly(instant: Instant): String {
        return dateOnlyFormatter.format(instant)
    }

    fun parseDateTime(dateTimeString: String): Instant {
        return Instant.from(defaultFormatter.parse(dateTimeString))
    }
}
