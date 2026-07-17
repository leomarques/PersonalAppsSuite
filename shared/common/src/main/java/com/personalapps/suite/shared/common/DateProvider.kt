package com.personalapps.suite.shared.common

import java.time.LocalDate

interface DateProvider {
    fun now(): LocalDate
}

class RealDateProvider : DateProvider {
    override fun now(): LocalDate = LocalDate.now()
}
