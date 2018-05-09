package xyz.arnau.muvicat.utils

import android.content.Context
import org.joda.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import xyz.arnau.muvicat.R

@Suppress("DEPRECATION")
class DateFormatterTest {
    private val context = mock(Context::class.java)
    private val dateFormatter = DateFormatter(context)

    @Test
    fun shortDateTestToday() {
        val date = LocalDate(2018, 4, 15).toDate()
        val today = LocalDate(2018, 4, 15)
        `when`(context.getString(R.string.release)).thenReturn("Estrena %s")
        `when`(context.getString(R.string.today)).thenReturn("avui")
        assertEquals("Estrena avui", dateFormatter.shortDate(date, today = today))
    }

    @Test
    fun shortDateTestTomorrow() {
        val date = LocalDate(2018, 4, 16).toDate()
        val today = LocalDate(2018, 4, 15)
        `when`(context.getString(R.string.release)).thenReturn("Estrena %s")
        `when`(context.getString(R.string.tomorrow)).thenReturn("demà")
        assertEquals("Estrena demà", dateFormatter.shortDate(date, today = today))
    }

    @Test
    fun shortDateTestAfterToday() {
        val date = LocalDate(2018, 4, 25).toDate()
        val today = LocalDate(2018, 4, 15)
        `when`(context.getString(R.string.release)).thenReturn("Estrena %s")
        assertEquals("Estrena dc. 25/04", dateFormatter.shortDate(date, today = today))
    }

    @Test
    fun shortDateTestBeforeToday() {
        val date = LocalDate(2018, 4, 10).toDate()
        val today = LocalDate(2018, 4, 15)
        `when`(context.getString(R.string.release)).thenReturn("Estrena %s")
        assertEquals(null, dateFormatter.shortDate(date, today = today))
    }

    @Test
    fun shortDateReturnsNullIfDateIsNull() {
        val today = LocalDate(2018, 4, 15)
        assertEquals(null, dateFormatter.shortDate(null, today = today))
    }

    @Test
    fun longDateTest() {
        val date = LocalDate(2018, 4, 15).toDate()
        assertEquals("15 d’abril de 2018", dateFormatter.longDate(date))
    }
}