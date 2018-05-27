package xyz.arnau.muvicat.ui

import org.joda.time.LocalDate

interface DateFilterable {
    fun onDatePicked(date: LocalDate)
}