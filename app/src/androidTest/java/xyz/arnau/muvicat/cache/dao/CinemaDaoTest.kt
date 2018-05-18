package xyz.arnau.muvicat.cache.dao

import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import xyz.arnau.muvicat.cache.db.MuvicatDatabase
import xyz.arnau.muvicat.repository.test.*
import xyz.arnau.muvicat.utils.getValueBlocking
import java.util.*


@Suppress("DEPRECATION")
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
    fun getCurrentCinemasRetrievesData() {
        val movies = MovieEntityFactory.makeMovieEntityList(2)
        muvicatDatabase.movieDao().insertMovies(movies)
        val cinemas = CinemaEntityFactory.makeCinemaEntityList(3)
        muvicatDatabase.cinemaDao().insertCinemas(cinemas)

        val showings = ShowingEntityFactory.makeShowingEntityList(6)
        showings.forEachIndexed { index, showingEntity ->
            showingEntity.movieId = movies[index%2].id
            showingEntity.cinemaId = cinemas[index%3].id
        }
        showings[0].date = Date(2018, 5, 13)
        showings[0].date = Date(2018, 5, 13)
        showings[1].date = Date(2018, 6, 13)
        showings[1].date = Date(2018, 6, 13)
        showings[2].date = Date(2018, 5, 12)
        showings[2].date = Date(2018, 5, 12)
        muvicatDatabase.showingDao().insertShowings(showings)

        val currentCinemaIds = listOf(showings[0].cinemaId, showings[1].cinemaId)

        val today = Date(2018, 5, 13)
        val retrievedCinemas = muvicatDatabase.cinemaDao().getCurrentCinemas(today.time).getValueBlocking()
        val expectedCinemas = CinemaMapper.mapFromCinemaEntityList(cinemas.sortedWith(compareBy({ it.id }, { it.id })))
            .filter { it.id in currentCinemaIds }
        expectedCinemas.forEach { c ->
            c.numMovies = showings.filter { s -> s.cinemaId == c.id && s.date >= today }.distinctBy { s -> s.movieId }.size
            c.numShowings = showings.filter { s -> s.cinemaId == c.id && s.date >= today }.size
        }
        assertEquals(expectedCinemas,
            retrievedCinemas
        )
    }



    @Test
    fun getCinemaRetrievesCinema() {
        val cinema = CinemaEntityFactory.makeCinemaEntity()
        val movie = MovieEntityFactory.makeMovieEntity()
        val showings = ShowingEntityFactory.makeShowingEntityList(2)
        showings.forEach {
            it.movieId = movie.id
            it.cinemaId = cinema.id
            it.date = Date(2018, 4, 17)
        }
        muvicatDatabase.cinemaDao().insertCinemas(listOf(cinema))
        muvicatDatabase.movieDao().insertMovies(listOf(movie))
        muvicatDatabase.showingDao().insertShowings(showings)

        val today = Date(2018, 4, 16)
        val retrievedCinema = muvicatDatabase.cinemaDao().getCinema(cinema.id, today.time).getValueBlocking()
        assertEquals(CinemaMapper.mapFromCinemaEntity(cinema). apply {
            numMovies = 1
            numShowings = 2
        }, retrievedCinema)
    }

    @Test
    fun getCinemaReturnsNullIfItDoesntExist() {
        val retrievedCinema = muvicatDatabase.cinemaDao().getCinema(123.toLong()).getValueBlocking()
        assertEquals(null, retrievedCinema)
    }

    @Test
    fun getCinemaReturnsNullIfNoCurrentShowings() {
        val cinema = CinemaEntityFactory.makeCinemaEntity()
        val movie = MovieEntityFactory.makeMovieEntity()
        val showings = ShowingEntityFactory.makeShowingEntityList(2)
        showings.forEach {
            it.movieId = movie.id
            it.cinemaId = cinema.id
            it.date = Date(2018, 4, 15)
        }
        muvicatDatabase.cinemaDao().insertCinemas(listOf(cinema))
        muvicatDatabase.movieDao().insertMovies(listOf(movie))
        muvicatDatabase.showingDao().insertShowings(showings)

        val today = Date(2018, 4, 16)
        val retrievedCinema = muvicatDatabase.cinemaDao().getCinema(cinema.id, today.time).getValueBlocking()
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

        assertEquals(
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