package xyz.arnau.muvicat.cache.dao

import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import junit.framework.Assert
import junit.framework.TestCase.assertEquals
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import xyz.arnau.muvicat.cache.db.MuvicatDatabase
import xyz.arnau.muvicat.data.test.CinemaEntityFactory
import xyz.arnau.muvicat.data.test.CinemaMapper
import xyz.arnau.muvicat.data.test.PostalCodeEntityFactory
import xyz.arnau.muvicat.utils.getValueBlocking


@RunWith(AndroidJUnit4::class)
class CinemaDaoTest {

    private var muvicatDatabase = Room.inMemoryDatabaseBuilder(
        InstrumentationRegistry.getContext(),
        MuvicatDatabase::class.java
    ).allowMainThreadQueries().build()

    @After
    fun closeDb() {
        muvicatDatabase.close()
    }

    @Test
    fun getCinemasRetrievesData() {
        val cinemas = CinemaEntityFactory.makeCinemaEntityList(5)

        muvicatDatabase.cinemaDao().insertCinemas(cinemas)

        val retrievedCinemas = muvicatDatabase.cinemaDao().getCinemas().getValueBlocking()
        assertEquals(
            CinemaMapper.mapFromCinemaEntityList(cinemas.sortedWith(compareBy({ it.id }, { it.id }))),
            retrievedCinemas
        )
    }

    @Test
    fun getCinemasRetrievesDataWithNullValues() {
        val cinemas = CinemaEntityFactory.makeCinemaEntityListWithNullValues(6)

        muvicatDatabase.cinemaDao().insertCinemas(cinemas)

        val retrievedCinemas = muvicatDatabase.cinemaDao().getCinemas().getValueBlocking()
        assertEquals(
            CinemaMapper.mapFromCinemaEntityList(cinemas.sortedWith(compareBy({ it.id }, { it.id }))),
            retrievedCinemas
        )
    }

    @Test
    fun getCinemaRetrievesCinema() {
        val cinema = CinemaEntityFactory.makeCinemaEntity()

        muvicatDatabase.cinemaDao().insertCinemas(listOf(cinema))

        val retrievedCinema = muvicatDatabase.cinemaDao().getCinema(cinema.id).getValueBlocking()
        assertEquals(CinemaMapper.mapFromCinemaEntity(cinema), retrievedCinema)
    }

    @Test
    fun getCinemaThrowsExceptionIfItDoesntExist() {
        val retrievedCinema = muvicatDatabase.cinemaDao().getCinema(123.toLong()).getValueBlocking()
        assertEquals(null, retrievedCinema)
    }

    @Test
    fun clearCinemasDeletesAllCinemas() {
        val cinemas = CinemaEntityFactory.makeCinemaEntityList(5)
        muvicatDatabase.cinemaDao().insertCinemas(cinemas)
        muvicatDatabase.cinemaDao().clearCinemas()
        val retrievedCinemas = muvicatDatabase.cinemaDao().getCinemas().getValueBlocking()
        assertEquals(true, retrievedCinemas!!.isEmpty())
    }

    @Test
    fun insertCinemasInDB() {
        val cinemas = CinemaEntityFactory.makeCinemaEntityList(3)
        muvicatDatabase.cinemaDao().insertCinemas(cinemas)

        assertEquals(
            CinemaMapper.mapFromCinemaEntityList(cinemas.sortedWith(compareBy({ it.id }, { it.id }))),
            muvicatDatabase.cinemaDao().getCinemas().getValueBlocking()!!
        )
    }


    @Test
    fun updateCinemaDbWhenNothingIsCached() {
        val cinemas = CinemaEntityFactory.makeCinemaEntityList(5)
        muvicatDatabase.cinemaDao().updateCinemaDb(cinemas)

        Assert.assertEquals(
            CinemaMapper.mapFromCinemaEntityList(cinemas.sortedWith(compareBy({ it.id }, { it.id }))),
            muvicatDatabase.cinemaDao().getCinemas().getValueBlocking()
        )
    }

    @Test
    fun updateCinemaDbInsertsUpdatesAndDeletesAsExpected() {
        val cinemas1 = CinemaEntityFactory.makeCinemaEntityList(5)
        cinemas1.forEachIndexed { index, item -> item.id = index.toLong() }
        muvicatDatabase.cinemaDao().insertCinemas(cinemas1)

        val cinemas2 = CinemaEntityFactory.makeCinemaEntityList(4)
        muvicatDatabase.cinemaDao().updateCinemaDb(cinemas2)

        assertEquals(
            CinemaMapper.mapFromCinemaEntityList(cinemas2.sortedWith(compareBy({ it.id }, { it.id }))),
            muvicatDatabase.cinemaDao().getCinemas().getValueBlocking()
        )
    }

    @Test
    fun getCinemasRetrivesDataWithCoordinates() {
        val cinemas = CinemaEntityFactory.makeCinemaEntityList(5)
        val pc1 = PostalCodeEntityFactory.makePostalCodeEntity()
        val pc2 = PostalCodeEntityFactory.makePostalCodeEntity()
        cinemas[0].postalCode = pc1.code
        cinemas[1].postalCode = pc2.code
        cinemas[2].postalCode = 11111
        cinemas[3].postalCode = 22222

        muvicatDatabase.postalCodeDao().insertPostalCodes(listOf(pc1, pc2))
        muvicatDatabase.cinemaDao().insertCinemas(cinemas)

        val expectedCinemas = CinemaMapper.mapFromCinemaEntityList(cinemas)
        expectedCinemas[0].latitude = pc1.latitude
        expectedCinemas[0].longitude = pc1.longitude
        expectedCinemas[1].latitude = pc2.latitude
        expectedCinemas[1].longitude = pc2.longitude

        val retrievedCinemas = muvicatDatabase.cinemaDao().getCinemas().getValueBlocking()

        assertEquals(expectedCinemas.sortedWith(compareBy({ it.id }, { it.id })), retrievedCinemas)
    }
}