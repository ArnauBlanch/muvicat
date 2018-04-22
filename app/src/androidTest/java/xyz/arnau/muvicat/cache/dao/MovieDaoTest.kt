package xyz.arnau.muvicat.cache.dao

import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import junit.framework.Assert.assertEquals
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import xyz.arnau.muvicat.cache.db.MuvicatDatabase
import xyz.arnau.muvicat.data.test.MovieFactory
import xyz.arnau.muvicat.utils.getValueBlocking


@RunWith(AndroidJUnit4::class)
class MovieDaoTest {

    private var muvicatDatabase = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getContext(),
            MuvicatDatabase::class.java
    ).allowMainThreadQueries().build()

    @After
    fun closeDb() {
        muvicatDatabase.close()
    }

    @Test
    fun getMoviesRetrievesData() {
        val movies = MovieFactory.makeMovieList(5)

        movies.forEach { muvicatDatabase.movieDao().insertMovie(it) }

        val retrievedMovies = muvicatDatabase.movieDao().getMovies().getValueBlocking()
        assertEquals(movies.sortedWith(compareBy({ it.id }, { it.id })), retrievedMovies)
    }

    @Test
    fun getMoviesRetrievesDataWithNullValues() {
        val movies = MovieFactory.makeMovieListWithNullValues(6)

        movies.forEach { muvicatDatabase.movieDao().insertMovie(it) }

        val retrievedMovies = muvicatDatabase.movieDao().getMovies().getValueBlocking()
        assertEquals(movies.sortedWith(compareBy({ it.id }, { it.id })), retrievedMovies)
    }

    @Test
    fun clearMoviesDeletesAllMovies() {
        val movies = MovieFactory.makeMovieList(5)
        movies.forEach { muvicatDatabase.movieDao().insertMovie(it) }
        muvicatDatabase.movieDao().clearMovies()
        val retrievedMovies = muvicatDatabase.movieDao().getMovies().getValueBlocking()
        assertEquals(true, retrievedMovies!!.isEmpty())
    }

    @Test
    fun insertMovieInDB() {
        val movie = MovieFactory.makeMovie()
        muvicatDatabase.movieDao().insertMovie(movie)

        assertEquals(movie, muvicatDatabase.movieDao().getMovies().getValueBlocking()!!.first())
    }

    @Test
    fun isCachedReturnsTrueIfThereAreMovies() {
        val movies = MovieFactory.makeMovieList(3)
        movies.forEach { muvicatDatabase.movieDao().insertMovie(it) }

        assertEquals(true, muvicatDatabase.movieDao().isCached())
    }

    @Test
    fun isCachedReturnsFalseIfThereAreNoMovies() {
        assertEquals(false, muvicatDatabase.movieDao().isCached())
    }
}