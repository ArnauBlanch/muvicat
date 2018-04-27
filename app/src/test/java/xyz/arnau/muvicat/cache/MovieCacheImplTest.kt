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
import xyz.arnau.muvicat.data.model.Movie
import xyz.arnau.muvicat.data.test.MovieFactory
import xyz.arnau.muvicat.data.utils.PreferencesHelper


@RunWith(JUnit4::class)
class MovieCacheImplTest {
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private val preferencesHelper = mock(PreferencesHelper::class.java)
    private val movieDao = mock(MovieDao::class.java)

    private val movieCacheImpl = MovieCacheImpl(movieDao, preferencesHelper)

    @Test
    fun clearMoviesDeletesMovies() {
        movieCacheImpl.clearMovies()
        verify(movieDao).clearMovies()
    }

    @Test
    fun getMoviesReturnsData() {
        val movies = MovieFactory.makeMovieList(5)
        val moviesLiveData = MutableLiveData<List<Movie>>()
        moviesLiveData.value = movies
        `when`(movieDao.getMovies()).thenReturn(moviesLiveData)
        val moviesFromCache = movieCacheImpl.getMovies()
        verify(movieDao).getMovies()
        assertEquals(movies, moviesFromCache.value)
    }

    @Test
    fun getMoviesReturnsMovie() {
        val movie = MovieFactory.makeMovie()
        val movieLiveData = MutableLiveData<Movie>()
        movieLiveData.value = movie
        `when`(movieDao.getMovie(movie.id)).thenReturn(movieLiveData)
        val movieFromCache = movieCacheImpl.getMovie(movie.id)
        verify(movieDao).getMovie(movie.id)
        assertEquals(movie, movieFromCache.value)
    }

    @Test
    fun updateMoviesUpdateData() {
        val movies = MovieFactory.makeMovieList(5)
        movieCacheImpl.updateMovies(movies)

        verify(movieDao).updateMovieDb(movies)
    }

    @Test
    fun isExpiredReturnsTrueIfExpired() {
        val currentTime = System.currentTimeMillis()
        `when`(preferencesHelper.movieslastUpdateTime)
            .thenReturn(currentTime - (MovieCacheImpl.EXPIRATION_TIME + 500))
        assertEquals(true, movieCacheImpl.isExpired())
    }

    @Test
    fun isExpiredReturnsFalseIfNotExpired() {
        val currentTime = System.currentTimeMillis()
        `when`(preferencesHelper.movieslastUpdateTime)
            .thenReturn(currentTime - 5000)
        assertEquals(false, movieCacheImpl.isExpired())
    }

    @Test
    fun companionObjectTest() {
        assertEquals((3 * 60 * 60 * 1000).toLong(), MovieCacheImpl.EXPIRATION_TIME)
    }
}