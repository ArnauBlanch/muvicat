package xyz.arnau.muvicat.ui.utils

import org.joda.time.LocalDate

interface DateFilterable {
    fun onDatePicked(date: LocalDate)
}