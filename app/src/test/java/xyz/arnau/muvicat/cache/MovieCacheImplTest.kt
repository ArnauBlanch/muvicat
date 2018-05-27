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
import xyz.arnau.muvicat.repository.model.MovieWithCast
import xyz.arnau.muvicat.repository.test.MovieEntityFactory
import xyz.arnau.muvicat.repository.test.MovieExtraInfoFactory
import xyz.arnau.muvicat.repository.test.MovieMapper
import xyz.arnau.muvicat.repository.test.MovieWithCastMapper


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
    fun getVotedMoviesReturnsData() {
        val movies = MovieMapper.mapFromMovieEntityList(MovieEntityFactory.makeMovieEntityList(5))
        val moviesLiveData = MutableLiveData<List<Movie>>()
        moviesLiveData.value = movies
        `when`(movieDao.getVotedMovies()).thenReturn(moviesLiveData)
        val moviesFromCache = movieCacheImpl.getVotedMovies()
        verify(movieDao).getVotedMovies()
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
        val movieWithCast = MovieWithCastMapper.mapFromMovieEntity(MovieEntityFactory.makeMovieEntity())
        val movieWithCastLiveData = MutableLiveData<MovieWithCast>()
        movieWithCastLiveData.value = movieWithCast
        `when`(movieDao.getMovie(movieWithCast.movie.id)).thenReturn(movieWithCastLiveData)
        val movieFromCache = movieCacheImpl.getMovie(movieWithCast.movie.id)
        verify(movieDao).getMovie(movieWithCast.movie.id)
        assertEquals(movieWithCast, movieFromCache.value)
    }

    @Test
    fun updateMoviesUpdateData() {
        val movies = MovieEntityFactory.makeMovieEntityList(5)
        movieCacheImpl.updateMovies(movies)

        verify(movieDao).updateMovieDb(movies)
    }

    @Test
    fun addExtraMovieInfoUpdatesInfo() {
        val extraInfo = MovieExtraInfoFactory.makeExtraInfo()
        movieCacheImpl.updateExtraMovieInfo(1.toLong(), extraInfo)

        verify(movieDao).addMovieExtraInfo(1.toLong(), extraInfo)
    }

    @Test
    fun voteMovieUpdatesVote() {
        movieCacheImpl.voteMovie(1.toLong(), 5.0)
        verify(movieDao).voteMovie(1.toLong(), 5.0)
    }

    @Test
    fun unvoteMovieUpdatesVote() {
        movieCacheImpl.unvoteMovie(1.toLong())
        verify(movieDao).unvoteMovie(1.toLong())
    }
}