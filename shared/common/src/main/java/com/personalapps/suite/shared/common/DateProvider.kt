package com.personalapps.suite.shared.common

import java.time.Instant
import java.time.LocalDate

interface DateProvider {
    fun now(): LocalDate
    fun nowInstant(): Instant
}

class RealDateProvider : DateProvider {
    override fun now(): LocalDate = LocalDate.now()
    override fun nowInstant(): Instant = Instant.now()
}
