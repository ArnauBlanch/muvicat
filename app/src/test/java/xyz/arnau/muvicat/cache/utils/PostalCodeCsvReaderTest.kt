package xyz.arnau.muvicat.cache.utils

import org.junit.Assert.assertEquals
import org.junit.Test
import xyz.arnau.muvicat.cache.model.PostalCodeEntity

class PostalCodeCsvReaderTest {
    private val csv = """"Codi postal","Latitud","Longitud"\n
        |11111, 1.111, 1.111
        |22222, 2.222, 2.222
        |33333, 3.333, 3.333""".trimMargin()
    private val postalCodes = listOf(
        PostalCodeEntity(11111, 1.111, 1.111),
        PostalCodeEntity(22222, 2.222, 2.222),
        PostalCodeEntity(33333, 3.333, 3.333)
    )

    @Test
    fun test() {
        assertEquals(postalCodes, PostalCodeCsvReader().readPostalCodeCsv(csv.byteInputStream()))
    }
}