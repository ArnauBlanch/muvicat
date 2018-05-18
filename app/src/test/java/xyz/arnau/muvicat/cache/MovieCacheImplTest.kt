package xyz.arnau.muvicat.cache

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.MutableLiveData
import junit.framework.TestCase.assertEquals
import org.joda.time.LocalDate
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.*
import xyz.arnau.muvicat.cache.dao.MovieDao
import xyz.arnau.muvicat.repository.model.Movie
import xyz.arnau.muvicat.repository.test.MovieEntityFactory
import xyz.arnau.muvicat.repository.test.MovieMapper


@RunWith(JUnit4::class)
class MovieCacheImplTest {
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private val movieDao = mock(MovieDao::class.java)

    private val movieCacheImpl = MovieCacheImpl(movieDao)

    @Test
    fun getMoviesReturnsData() {
        val movies = MovieMapper.mapFromMovieEntityList(MovieEntityFactory.makeMovieEntityList(5))
        val moviesLiveData = MutableLiveData<List<Movie>>()
        moviesLiveData.value = movies
        `when`(movieDao.getCurrentMovies()).thenReturn(moviesLiveData)
        val moviesFromCache = movieCacheImpl.getMovies()
        verify(movieDao).getCurrentMovies()
        assertEquals(movies, moviesFromCache.value)
    }

    @Test
    fun getMoviesByCinemaReturnsData() {
        val cinemaId = 100.toLong()
        val today = LocalDate.now().toDate().time
        val movies = MovieMapper.mapFromMovieEntityList(MovieEntityFactory.makeMovieEntityList(5))
        val moviesLiveData = MutableLiveData<List<Movie>>()
        moviesLiveData.value = movies
        `when`(movieDao.getCurrentMoviesByCinema(cinemaId, today)).thenReturn(moviesLiveData)
        val moviesFromCache = movieCacheImpl.getMoviesByCinema(cinemaId)
        verify(movieDao).getCurrentMoviesByCinema(cinemaId, today)
        assertEquals(movies, moviesFromCache.value)
    }

    @Test
    fun getMovieReturnsMovie() {
        val movie = MovieMapper.mapFromMovieEntity(MovieEntityFactory.makeMovieEntity())
        val movieLiveData = MutableLiveData<Movie>()
        movieLiveData.value = movie
        `when`(movieDao.getMovie(movie.id)).thenReturn(movieLiveData)
        val movieFromCache = movieCacheImpl.getMovie(movie.id)
        verify(movieDao).getMovie(movie.id)
        assertEquals(movie, movieFromCache.value)
    }

    @Test
    fun updateMoviesUpdateData() {
        val movies = MovieEntityFactory.makeMovieEntityList(5)
        movieCacheImpl.updateMovies(movies)

        verify(movieDao).updateMovieDb(movies)
    }
}