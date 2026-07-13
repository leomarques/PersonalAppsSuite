package com.personalapps.suite.shared.databaseutils

import java.time.Instant
import org.junit.Assert.assertEquals
import org.junit.Test

class ConvertersTest {

    private val converters = Converters()

    @Test
    fun fromTimestamp_returnsCorrectInstant() {
        val millis = 1672531200000L // 2023-01-01T00:00:00Z
        val expected = Instant.ofEpochMilli(millis)
        assertEquals(expected, converters.fromTimestamp(millis))
    }

    @Test
    fun dateToTimestamp_returnsCorrectLong() {
        val millis = 1672531200000L
        val date = Instant.ofEpochMilli(millis)
        assertEquals(millis, converters.dateToTimestamp(date))
    }
}
