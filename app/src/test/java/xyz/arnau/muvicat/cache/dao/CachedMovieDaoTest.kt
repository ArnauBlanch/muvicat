package xyz.arnau.muvicat.cache.dao

import android.arch.persistence.room.Room
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import xyz.arnau.muvicat.cache.db.MuvicatDatabase
import xyz.arnau.muvicat.cache.test.MovieFactory

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [16])
open class CachedMovieDaoTest {
    private lateinit var muvicatDatabase: MuvicatDatabase

    @Before
    fun initDb() {
        muvicatDatabase = Room.inMemoryDatabaseBuilder(
                RuntimeEnvironment.application.baseContext,
                MuvicatDatabase::class.java)
                .allowMainThreadQueries()
                .build()
    }

    @After
    fun closeDb() {
        muvicatDatabase.close()
    }

    @Test
    fun getMoviesRetrievesData() {
        val cachedMovies = MovieFactory.makeCachedMovieList(5)

        cachedMovies.forEach { muvicatDatabase.cachedMoviesDao().insertMovie(it) }

        val retrievedMovies = muvicatDatabase.cachedMoviesDao().getMovies()
        assert(retrievedMovies == cachedMovies
                .sortedWith(compareBy({ it.id }, { it.id })))
    }

    @Test
    fun getMoviesRetrievesDataWithNullValues() {
        val cachedMovies = MovieFactory.makeCachedMovieListWithNullValues(6)

        cachedMovies.forEach { muvicatDatabase.cachedMoviesDao().insertMovie(it) }

        val retrievedMovies = muvicatDatabase.cachedMoviesDao().getMovies()
        assert(retrievedMovies == cachedMovies
                .sortedWith(compareBy({ it.id }, { it.id })))
    }

    @Test
    fun clearMoviesDeletesAllMovies() {
        val cachedMovies = MovieFactory.makeCachedMovieList(5)
        cachedMovies.forEach { muvicatDatabase.cachedMoviesDao().insertMovie(it) }
        muvicatDatabase.cachedMoviesDao().clearMovies()
        val retrievedMovies = muvicatDatabase.cachedMoviesDao().getMovies()
        assert(retrievedMovies.isEmpty())
    }
}