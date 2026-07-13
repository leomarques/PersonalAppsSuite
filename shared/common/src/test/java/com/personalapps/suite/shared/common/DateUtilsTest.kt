package com.personalapps.suite.shared.common

import java.time.Instant
import org.junit.Assert.assertEquals
import org.junit.Test

class DateUtilsTest {

    @Test
    fun testFormatDateTime() {
        val instant = Instant.ofEpochMilli(1710000000000L)
        val formattedStr = DateUtils.formatDateTime(instant)
        val parsedInstant = DateUtils.parseDateTime(formattedStr)
        // Compare with minutes precision since dd/MM/yyyy HH:mm drops seconds
        assertEquals(instant.toEpochMilli() / 60000, parsedInstant.toEpochMilli() / 60000)
    }

    @Test
    fun testFormatDateOnly() {
        val instant = Instant.parse("2026-07-12T10:15:30Z")
        val formattedStr = DateUtils.formatDateOnly(instant)
        assertEquals("12/07/2026", formattedStr)
    }
}
