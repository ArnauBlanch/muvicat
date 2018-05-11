@file:Suppress("DEPRECATION")

package xyz.arnau.muvicat.utils

import android.content.Context
import org.joda.time.LocalDate
import xyz.arnau.muvicat.R
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DateFormatter @Inject constructor(private val context: Context) {

    fun shortReleaseDate(d: Date?, today: LocalDate = LocalDate.now()): String? {
        if (d == null) {
            return null
        }
        val releaseString = context.getString(R.string.release)
        val dateString = shortDate(d, today) ?: return null
        return String.format(releaseString, dateString)
    }

    fun shortDate(d: Date?, today: LocalDate = LocalDate.now()): String? {
        if (d == null) {
            return null
        }
        val date = LocalDate(d)
        return when {
            date.isEqual(today) -> { // today
                context.getString(R.string.today)
            }
            date.isEqual(today.plusDays(1)) -> { // tomorrow
                context.getString(R.string.tomorrow)
            }
            date.isAfter(today) -> {
                date.toString("EE dd/MM", Locale("ca"))
            }
            else -> null
        }
    }

    fun longDate(d: Date): String {
        return LocalDate(d).toString("d MMMM 'de' YYYY", Locale("ca"))
    }
}
