@file:Suppress("DEPRECATION")

package xyz.arnau.muvicat.utils

import android.content.Context
import android.content.res.Resources
import android.text.format.DateUtils
import org.joda.time.DateTime
import org.joda.time.Days
import org.joda.time.LocalDate
import xyz.arnau.muvicat.R
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DateFormatter @Inject constructor(private val context: Context) {

    fun shortDate(d: Date?): String? {
        if (d == null) {
            return null
        }
        val date = LocalDate(d)
        val today = LocalDate.now()
        val releaseString = context.getString(R.string.release)
        when {
            date.isEqual(today) -> { // today
                val todayString = context.getString(R.string.today)
                return String.format(releaseString, todayString)
            }
            date.isEqual(today.plusDays(1)) -> { // tomorrow
                val tomorrowString = context.getString(R.string.tomorrow)
                return String.format(releaseString, tomorrowString)
            }
            date.isAfter(today) -> {
                val dateString = date.toString("EE dd/MM", Locale("ca"))
                return String.format(releaseString, dateString)
            }
            else -> return null
        }
    }
}
