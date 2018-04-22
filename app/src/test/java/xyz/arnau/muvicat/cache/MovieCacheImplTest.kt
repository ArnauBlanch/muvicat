package xyz.arnau.muvicat.cache

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.MutableLiveData
import junit.framework.TestCase.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.*
import xyz.arnau.muvicat.cache.dao.MovieDao
import xyz.arnau.muvicat.cache.db.MuvicatDatabase
import xyz.arnau.muvicat.data.PreferencesHelper
import xyz.arnau.muvicat.data.model.Movie
import xyz.arnau.muvicat.data.test.MovieFactory


@RunWith(JUnit4::class)
class MovieCacheImplTest {
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private val muvicatDatabase = mock(MuvicatDatabase::class.java)
    private val preferencesHelper = mock(PreferencesHelper::class.java)
    private val movieDao = mock(MovieDao::class.java)

    private val movieCacheImpl = MovieCacheImpl(muvicatDatabase, preferencesHelper)

    @Test
    fun clearMoviesDeletesMovies() {
        `when`(muvicatDatabase.movieDao()).thenReturn(movieDao)
        movieCacheImpl.clearMovies()
        verify(movieDao).clearMovies()
    }

    @Test
    fun getMoviesReturnsData() {
        `when`(muvicatDatabase.movieDao()).thenReturn(movieDao)
        val movies = MovieFactory.makeMovieList(5)
        val moviesLiveData = MutableLiveData<List<Movie>>()
        moviesLiveData.value = movies
        `when`(movieDao.getMovies()).thenReturn(moviesLiveData)
        val moviesFromCache = movieCacheImpl.getMovies()
        verify(movieDao).getMovies()
        assertEquals(movies, moviesFromCache.value)
    }

    @Test
    fun saveMoviesInsertsData() {
        `when`(muvicatDatabase.movieDao()).thenReturn(movieDao)
        val movies = MovieFactory.makeMovieList(5)
        movieCacheImpl.saveMovies(movies)

        movies.forEach { verify(movieDao).insertMovie(it) }
    }

    @Test
    fun setLastTimeCacheUpdatesSharedPreferences() {
        movieCacheImpl.setLastCacheTime(1000.toLong())
        verify(preferencesHelper).lastCacheTime = 1000.toLong()
    }

    @Test
    fun isExpiredReturnsTrueIfExpired() {
        val currentTime = System.currentTimeMillis()
        `when`(preferencesHelper.lastCacheTime)
                .thenReturn(currentTime - (MovieCacheImpl.EXPIRATION_TIME + 500))
        assertEquals(true, movieCacheImpl.isExpired())
    }

    @Test
    fun isExpiredReturnsFalseIfNotExpired() {
        val currentTime = System.currentTimeMillis()
        `when`(preferencesHelper.lastCacheTime)
                .thenReturn(currentTime - 5000)
        assertEquals(false, movieCacheImpl.isExpired())
    }

    @Test
    fun isCachedReturnsTrueIfMoviesExist() {
        `when`(muvicatDatabase.movieDao()).thenReturn(movieDao)
        `when`(movieDao.isCached()).thenReturn(true)
        assertEquals(true, movieCacheImpl.isCached())
    }

    @Test
    fun isCachedReturnFalseIfNotMoviesExist() {
        `when`(muvicatDatabase.movieDao()).thenReturn(movieDao)
        `when`(movieDao.isCached()).thenReturn(false)
        assertEquals(false, movieCacheImpl.isCached())
    }
}