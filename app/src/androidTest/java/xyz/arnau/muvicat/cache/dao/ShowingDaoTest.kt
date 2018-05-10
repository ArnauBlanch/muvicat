package xyz.arnau.muvicat.cache.dao

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import junit.framework.Assert
import junit.framework.TestCase.assertEquals
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import xyz.arnau.muvicat.cache.db.MuvicatDatabase
import xyz.arnau.muvicat.cache.model.MovieEntity
import xyz.arnau.muvicat.data.model.Movie
import xyz.arnau.muvicat.data.model.Showing
import xyz.arnau.muvicat.data.test.*
import xyz.arnau.muvicat.utils.getValueBlocking


@RunWith(AndroidJUnit4::class)
class ShowingDaoTest {
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private var muvicatDatabase = Room.inMemoryDatabaseBuilder(
        InstrumentationRegistry.getContext(),
        MuvicatDatabase::class.java
    ).allowMainThreadQueries().build()

    @After
    fun closeDb() {
        muvicatDatabase.close()
    }

    @Test
    fun insertShowingsInsertsDataAndGetShowingsRetrievesData() {
        val movies = MovieEntityFactory.makeMovieEntityList(3)
        muvicatDatabase.movieDao().insertMovies(movies)
        val cinemas = CinemaEntityFactory.makeCinemaEntityList(3)
        muvicatDatabase.cinemaDao().insertCinemas(cinemas)

        val showings = ShowingEntityFactory.makeShowingEntityList(3)
        showings.forEachIndexed { index, showingEntity ->
            showingEntity.cinemaId = cinemas[index].id
            showingEntity.movieId = movies[index].id
        }
        muvicatDatabase.showingDao().insertShowings(showings)

        val retrievedShowings = muvicatDatabase.showingDao().getShowings().getValueBlocking()
        val convertedOriginalShowings = ShowingMapper.mapFromShowingEntityList(showings)
        convertedOriginalShowings.forEachIndexed { index, it ->
            it.id = (index + 1).toLong()
            it.movieTitle = movies[index].title
            it.moviePosterUrl = movies[index].posterUrl
            it.cinemaName = cinemas[index].name
            it.cinemaTown = cinemas[index].town
            it.cinemaRegion = cinemas[index].region
            it.cinemaProvince = cinemas[index].province
        }
        assertEquals(
            convertedOriginalShowings.sortedWith(compareBy<Showing>{ it.date }.thenBy { it.movieId }.thenBy { it.cinemaName }),
            retrievedShowings
        )
    }

    @Test
    fun getCinemaIdsReturnAllExistingIds() {
        val cinemas = CinemaEntityFactory.makeCinemaEntityList(5)
        val ids = cinemas.map { it.id }
        muvicatDatabase.cinemaDao().insertCinemas(cinemas)

        assertEquals(ids.sorted(), muvicatDatabase.showingDao().getCinemaIds())
    }

    @Test
    fun getMovieIdsReturnAllExistingIds() {
        val movies = MovieEntityFactory.makeMovieEntityList(5)
        val ids = movies.map { it.id }
        muvicatDatabase.movieDao().insertMovies(movies)

        assertEquals(ids.sorted(), muvicatDatabase.movieDao().getMovieIds())
    }

    @Test
    fun clearShowingsDeletesAllShowings() {
        val movies = MovieEntityFactory.makeMovieEntityList(3)
        muvicatDatabase.movieDao().insertMovies(movies)
        val cinemas = CinemaEntityFactory.makeCinemaEntityList(3)
        muvicatDatabase.cinemaDao().insertCinemas(cinemas)

        val showings = ShowingEntityFactory.makeShowingEntityList(3)
        showings.forEachIndexed { index, showingEntity ->
            showingEntity.cinemaId = cinemas[index].id
            showingEntity.movieId = movies[index].id
        }
        muvicatDatabase.showingDao().insertShowings(showings)

        muvicatDatabase.showingDao().clearShowings()
        val retrievedShowings = muvicatDatabase.showingDao().getShowings().getValueBlocking()
        assertEquals(true, retrievedShowings!!.isEmpty())
    }


    @Test
    fun updateMovieDbWhenNothingIsCached() {
        val movies = MovieEntityFactory.makeMovieEntityList(3)
        muvicatDatabase.movieDao().insertMovies(movies)
        val cinemas = CinemaEntityFactory.makeCinemaEntityList(3)
        muvicatDatabase.cinemaDao().insertCinemas(cinemas)

        val showings = ShowingEntityFactory.makeShowingEntityList(3)
        showings.forEachIndexed { index, showingEntity ->
            showingEntity.cinemaId = cinemas[index].id
            showingEntity.movieId = movies[index].id
        }
        muvicatDatabase.showingDao().updateShowingDb(showings)

        val retrievedShowings = muvicatDatabase.showingDao().getShowings().getValueBlocking()
        val convertedOriginalShowings = ShowingMapper.mapFromShowingEntityList(showings)
        convertedOriginalShowings.forEachIndexed { index, it ->
            it.id = (index + 1).toLong()
            it.movieTitle = movies[index].title
            it.moviePosterUrl = movies[index].posterUrl
            it.cinemaName = cinemas[index].name
            it.cinemaTown = cinemas[index].town
            it.cinemaRegion = cinemas[index].region
            it.cinemaProvince = cinemas[index].province
        }
        assertEquals(
            convertedOriginalShowings.sortedWith(compareBy<Showing>{ it.date }.thenBy { it.movieId }.thenBy { it.cinemaName }),
            retrievedShowings
        )
    }

    @Test
    fun updateMovieDbDeletesAndInsertsAsExpected() {
        val movies1 = MovieEntityFactory.makeMovieEntityList(3)
        muvicatDatabase.movieDao().insertMovies(movies1)
        val cinemas1 = CinemaEntityFactory.makeCinemaEntityList(3)
        muvicatDatabase.cinemaDao().insertCinemas(cinemas1)
        val showings1 = ShowingEntityFactory.makeShowingEntityList(3)
        showings1.forEachIndexed { index, showingEntity ->
            showingEntity.cinemaId = cinemas1[index].id
            showingEntity.movieId = movies1[index].id
        }
        muvicatDatabase.showingDao().insertShowings(showings1)

        val movies2 = MovieEntityFactory.makeMovieEntityList(3)
        muvicatDatabase.movieDao().insertMovies(movies2)
        val cinemas2 = CinemaEntityFactory.makeCinemaEntityList(3)
        muvicatDatabase.cinemaDao().insertCinemas(cinemas2)
        val showings2 = ShowingEntityFactory.makeShowingEntityList(3)
        showings2.forEachIndexed { index, showingEntity ->
            showingEntity.cinemaId = cinemas2[index].id
            showingEntity.movieId = movies2[index].id
        }
        muvicatDatabase.showingDao().updateShowingDb(showings2)

        val retrievedShowings = muvicatDatabase.showingDao().getShowings().getValueBlocking()
        val convertedOriginalShowings = ShowingMapper.mapFromShowingEntityList(showings2)
        convertedOriginalShowings.forEachIndexed { index, it ->
            it.id = (index + 4).toLong()
            it.movieTitle = movies2[index].title
            it.moviePosterUrl = movies2[index].posterUrl
            it.cinemaName = cinemas2[index].name
            it.cinemaTown = cinemas2[index].town
            it.cinemaRegion = cinemas2[index].region
            it.cinemaProvince = cinemas2[index].province
        }
        assertEquals(
            convertedOriginalShowings.sortedWith(compareBy<Showing>{ it.date }.thenBy { it.movieId }.thenBy { it.cinemaName }),
            retrievedShowings
        )
    }

    @Test
    fun updateMovieDbDeletesAndInsertsWithInvalidIds() {
        val movies = MovieEntityFactory.makeMovieEntityList(3)
        muvicatDatabase.movieDao().insertMovies(movies)
        val cinemas = CinemaEntityFactory.makeCinemaEntityList(3)
        muvicatDatabase.cinemaDao().insertCinemas(cinemas)
        val showings = ShowingEntityFactory.makeShowingEntityList(3)
        showings.forEachIndexed { index, showingEntity ->
            showingEntity.cinemaId = cinemas[index].id
            showingEntity.movieId = movies[index].id
        }
        val showingWithInvalidMovieId = ShowingEntityFactory.makeShowingEntity()
        showingWithInvalidMovieId.cinemaId = cinemas[0].id
        val showingWithInvalidCinemaId = ShowingEntityFactory.makeShowingEntity()
        showingWithInvalidCinemaId.movieId = movies[0].id
        muvicatDatabase.showingDao()
            .updateShowingDb(showings + showingWithInvalidCinemaId + showingWithInvalidMovieId)

        val retrievedShowings = muvicatDatabase.showingDao().getShowings().getValueBlocking()
        val convertedOriginalShowings = ShowingMapper.mapFromShowingEntityList(showings)
        convertedOriginalShowings.forEachIndexed { index, it ->
            it.id = (index + 1).toLong()
            it.movieTitle = movies[index].title
            it.moviePosterUrl = movies[index].posterUrl
            it.cinemaName = cinemas[index].name
            it.cinemaTown = cinemas[index].town
            it.cinemaRegion = cinemas[index].region
            it.cinemaProvince = cinemas[index].province
        }
        assertEquals(
            convertedOriginalShowings.sortedWith(compareBy<Showing>{ it.date }.thenBy { it.movieId }.thenBy { it.cinemaName }),
            retrievedShowings
        )
    }
}