@file:Suppress("UNCHECKED_CAST")

package xyz.arnau.muvicat.repository

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import junit.framework.TestCase
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito
import org.mockito.Mockito.*
import xyz.arnau.muvicat.cache.model.MovieEntity
import xyz.arnau.muvicat.repository.MovieRepository.Companion.EXPIRATION_TIME
import xyz.arnau.muvicat.repository.model.Movie
import xyz.arnau.muvicat.repository.model.Resource
import xyz.arnau.muvicat.repository.model.Status
import xyz.arnau.muvicat.repository.data.GencatRemote
import xyz.arnau.muvicat.repository.data.MovieCache
import xyz.arnau.muvicat.repository.test.MovieEntityFactory
import xyz.arnau.muvicat.repository.test.MovieMapper
import xyz.arnau.muvicat.repository.utils.RepoPreferencesHelper
import xyz.arnau.muvicat.remote.DataUpdateCallback
import xyz.arnau.muvicat.remote.model.Response
import xyz.arnau.muvicat.utils.AfterCountDownLatch
import xyz.arnau.muvicat.utils.BeforeCountDownLatch
import xyz.arnau.muvicat.utils.InstantAppExecutors
import xyz.arnau.muvicat.utils.getValueBlocking

@RunWith(JUnit4::class)
class MovieRepositoryTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val movieCache = mock(MovieCache::class.java)
    private val gencatRemote = mock(GencatRemote::class.java)
    private val appExecutors = InstantAppExecutors()
    private val preferencesHelper = mock(RepoPreferencesHelper::class.java)
    private val beforeLatch = mock(BeforeCountDownLatch::class.java)
    private val afterLatch = mock(AfterCountDownLatch::class.java)
    private val movieRepository = MovieRepository(movieCache, gencatRemote, appExecutors, preferencesHelper, beforeLatch, afterLatch)

    private val dbMovieLiveData = MutableLiveData<List<Movie>>()
    private val dbMovies = MovieMapper.mapFromMovieEntityList(MovieEntityFactory.makeMovieEntityList(3))

    private val remoteMovieLiveData = MutableLiveData<Response<List<MovieEntity>>>()
    private val remoteMovies = MovieEntityFactory.makeMovieEntityList(3)
    private val remoteMoviesMapped = MovieMapper.mapFromMovieEntityList(remoteMovies)

    private val observer: Observer<*>? = mock(Observer::class.java)
    private val currentTime = System.currentTimeMillis()
    private val callback = mock(DataUpdateCallback::class.java)

    private val movie = MovieMapper.mapFromMovieEntity(MovieEntityFactory.makeMovieEntity())
    private val movieLiveData = MutableLiveData<Movie>()

    @Before
    fun setUp() {
        `when`(movieCache.getMovies()).thenReturn(dbMovieLiveData)
        `when`(gencatRemote.getMovies()).thenReturn(remoteMovieLiveData)
    }

    @Test
    fun getMoviesWhenMoviesAreCachedAndNotExpired() {
        dbMovieLiveData.postValue(dbMovies)
        `when`(preferencesHelper.movieslastUpdateTime).thenReturn(currentTime - 5000)

        val movies = movieRepository.getMovies()
        movies.observeForever(observer as Observer<Resource<List<Movie>>>)

        verify(observer).onChanged(Resource.success(dbMovies))

        verify(preferencesHelper, never()).moviesUpdated()
        verify(beforeLatch).countDown()
        verify(gencatRemote, never()).getMovies()
    }
    

    @Test
    fun getMoviesWhenMoviesAreCachedButExpiredSuccesfulWithCallback() {
        `when`(preferencesHelper.movieslastUpdateTime).thenReturn(currentTime - (EXPIRATION_TIME + 500))

        val movies = movieRepository.getMovies()
        movies.observeForever(observer as Observer<Resource<List<Movie>>>)

        dbMovieLiveData.postValue(dbMovies)
        remoteMovieLiveData.postValue(Response.successful(remoteMovies, callback))
        dbMovieLiveData.postValue(remoteMoviesMapped)

        verify(observer).onChanged(Resource.loading(dbMovies))
        verify(observer).onChanged(Resource.success(remoteMoviesMapped))
        verify(callback).onDataUpdated()

        verify(preferencesHelper).moviesUpdated()
        verify(beforeLatch).countDown()
        verify(movieCache).updateMovies(remoteMovies)
    }

    @Test
    fun getMoviesWhenMoviesAreCachedButExpiredSuccesfulWithoutCallback() {
        `when`(preferencesHelper.movieslastUpdateTime).thenReturn(currentTime - (EXPIRATION_TIME + 500))

        val movies = movieRepository.getMovies()
        movies.observeForever(observer as Observer<Resource<List<Movie>>>)

        dbMovieLiveData.postValue(dbMovies)
        remoteMovieLiveData.postValue(Response.successful(remoteMovies, null))
        dbMovieLiveData.postValue(remoteMoviesMapped)

        verify(observer).onChanged(Resource.loading(dbMovies))
        verify(observer).onChanged(Resource.success(remoteMoviesMapped))

        verify(preferencesHelper).moviesUpdated()
        verify(beforeLatch).countDown()
        verify(movieCache).updateMovies(remoteMovies)
    }

    @Test
    fun getMoviesWhenMoviesAreCachedButExpiredSuccesfulWithoutCallbackTwice() {
        `when`(preferencesHelper.movieslastUpdateTime).thenReturn(currentTime - (MovieRepository.EXPIRATION_TIME + 500))

        val movies = movieRepository.getMovies()
        movies.observeForever(observer as Observer<Resource<List<Movie>>>)

        dbMovieLiveData.postValue(dbMovies)
        remoteMovieLiveData.postValue(Response.successful(remoteMovies, null))
        dbMovieLiveData.postValue(remoteMoviesMapped)

        verify(observer).onChanged(Resource.loading(dbMovies))
        verify(observer).onChanged(Resource.success(remoteMoviesMapped))


        val dbMovieLiveData2 = MutableLiveData<List<Movie>>()
        val remoteMovieLiveData2 = MutableLiveData<Response<List<MovieEntity>>>()
        `when`(movieCache.getMovies()).thenReturn(dbMovieLiveData2)
        `when`(gencatRemote.getMovies()).thenReturn(remoteMovieLiveData2)

        val movies2 = movieRepository.getMovies()
        val observer2: Observer<*>? = mock(Observer::class.java)
        movies2.observeForever(observer2 as Observer<Resource<List<Movie>>>)

        dbMovieLiveData2.postValue(dbMovies)
        remoteMovieLiveData2.postValue(Response.successful(remoteMovies, null))
        dbMovieLiveData2.postValue(remoteMoviesMapped)

        verify(observer2).onChanged(Resource.loading(dbMovies))
        verify(observer2).onChanged(Resource.success(remoteMoviesMapped))

        verify(preferencesHelper, times(2)).moviesUpdated()
        verify(beforeLatch, times(1)).countDown()
        verify(movieCache, times(2)).updateMovies(remoteMovies)
    }

    @Test
    fun getMoviesWhenMoviesAreCachedButExpiredNotModified() {
        `when`(preferencesHelper.movieslastUpdateTime).thenReturn(currentTime - (EXPIRATION_TIME + 500))

        val movies = movieRepository.getMovies()
        movies.observeForever(observer as Observer<Resource<List<Movie>>>)

        dbMovieLiveData.postValue(dbMovies)
        remoteMovieLiveData.postValue(Response.notModified(callback))

        verify(observer).onChanged(Resource.loading(dbMovies))
        verify(observer).onChanged(Resource.success(dbMovies))
        verify(callback, never()).onDataUpdated()

        verify(preferencesHelper).moviesUpdated()
        verify(beforeLatch).countDown()
        verify(movieCache, never()).updateMovies(Mockito.anyList())
    }

    @Test
    fun getMoviesWhenMoviesAreCachedButExpiredError() {
        `when`(preferencesHelper.movieslastUpdateTime).thenReturn(currentTime - (EXPIRATION_TIME + 500))

        val movies = movieRepository.getMovies()
        movies.observeForever(observer as Observer<Resource<List<Movie>>>)

        dbMovieLiveData.postValue(dbMovies)
        remoteMovieLiveData.postValue(Response.error("error msg", callback))

        verify(observer).onChanged(Resource.loading(dbMovies))
        verify(observer).onChanged(Resource.error("error msg", dbMovies))
        verify(callback, never()).onDataUpdated()

        verify(preferencesHelper, never()).moviesUpdated()
        verify(beforeLatch).countDown()
        verify(movieCache, never()).updateMovies(remoteMovies)
    }

    @Test
    fun getMoviesWhenMoviesAreCachedButExpiredErrorTwice() {
        `when`(preferencesHelper.movieslastUpdateTime).thenReturn(currentTime - (EXPIRATION_TIME + 500))

        val movies = movieRepository.getMovies()
        movies.observeForever(observer as Observer<Resource<List<Movie>>>)

        dbMovieLiveData.postValue(dbMovies)
        remoteMovieLiveData.postValue(Response.error("error msg", callback))

        verify(observer).onChanged(Resource.loading(dbMovies))
        verify(observer).onChanged(Resource.error("error msg", dbMovies))


        val dbMovieLiveData2 = MutableLiveData<List<Movie>>()
        val remoteMovieLiveData2 = MutableLiveData<Response<List<MovieEntity>>>()
        `when`(movieCache.getMovies()).thenReturn(dbMovieLiveData2)
        `when`(gencatRemote.getMovies()).thenReturn(remoteMovieLiveData2)

        val movies2 = movieRepository.getMovies()
        val observer2: Observer<*>? = mock(Observer::class.java)
        movies2.observeForever(observer2 as Observer<Resource<List<Movie>>>)

        dbMovieLiveData2.postValue(dbMovies)
        remoteMovieLiveData2.postValue(Response.error("error msg", callback))

        verify(observer2).onChanged(Resource.loading(dbMovies))
        verify(observer2).onChanged(Resource.error("error msg", dbMovies))

        verify(callback, never()).onDataUpdated()
        verify(preferencesHelper, never()).moviesUpdated()
        verify(beforeLatch, times(1)).countDown()
        verify(movieCache, never()).updateMovies(remoteMovies)
    }

    @Test
    fun getMoviesWhenMoviesAreNullSuccesful() {
        val movies = movieRepository.getMovies()
        movies.observeForever(observer as Observer<Resource<List<Movie>>>)

        dbMovieLiveData.postValue(null)
        remoteMovieLiveData.postValue(Response.successful(remoteMovies, callback))
        dbMovieLiveData.postValue(remoteMoviesMapped)

        verify(observer).onChanged(Resource.loading(null))
        verify(observer).onChanged(Resource.success(remoteMoviesMapped))
        verify(callback).onDataUpdated()

        verify(preferencesHelper).moviesUpdated()
        verify(beforeLatch).countDown()
        verify(movieCache).updateMovies(remoteMovies)
    }

    @Test
    fun getMoviesWhenMoviesAreNotCachedSuccesful() {
        val movies = movieRepository.getMovies()
        movies.observeForever(observer as Observer<Resource<List<Movie>>>)

        dbMovieLiveData.postValue(listOf())
        remoteMovieLiveData.postValue(Response.successful(remoteMovies, callback))
        dbMovieLiveData.postValue(remoteMoviesMapped)

        verify(observer).onChanged(Resource.loading(listOf()))
        verify(observer).onChanged(Resource.success(remoteMoviesMapped))
        verify(callback).onDataUpdated()

        verify(preferencesHelper).moviesUpdated()
        verify(beforeLatch).countDown()
        verify(movieCache).updateMovies(remoteMovies)
    }

    @Test
    fun getMoviesWhenMoviesAreNotCachedSucessfulWithNullResponseBody() {
        val movies = movieRepository.getMovies()
        movies.observeForever(observer as Observer<Resource<List<Movie>>>)
        dbMovieLiveData.postValue(listOf())

        remoteMovieLiveData.postValue(Response.successful(null, callback))

        verify(observer).onChanged(Resource.loading(listOf()))
        verify(observer).onChanged(Resource.error(null, listOf()))
        verify(callback, never()).onDataUpdated()

        verify(preferencesHelper, never()).moviesUpdated()
        verify(beforeLatch).countDown()
        verify(movieCache, never()).updateMovies(Mockito.anyList())
    }

    @Test
    fun getMoviesWhenMoviesAreNotCachedError() {
        val movies = movieRepository.getMovies()
        movies.observeForever(observer as Observer<Resource<List<Movie>>>)
        dbMovieLiveData.postValue(listOf())

        remoteMovieLiveData.postValue(Response.error("error msg", callback))

        verify(observer).onChanged(Resource.loading(listOf()))
        verify(observer).onChanged(Resource.error("error msg", listOf()))
        verify(callback, never()).onDataUpdated()

        verify(preferencesHelper, never()).moviesUpdated()
        verify(beforeLatch).countDown()
        verify(movieCache, never()).updateMovies(remoteMovies)
    }



    @Test
    fun getMovieReturnsMovieLiveDataWithSuccessIfExists() {
        `when`(movieCache.getMovie(movie.id)).thenReturn(movieLiveData)
        movieLiveData.postValue(movie)

        val res = movieRepository.getMovie(movie.id).getValueBlocking()
        assertEquals(Status.SUCCESS, res?.status)
        assertEquals(movie, res?.data)
    }

    @Test
    fun getMovieReturnsMovieLiveDataWithErrorIfDoesNotExist() {
        `when`(movieCache.getMovie(100.toLong())).thenReturn(movieLiveData)
        movieLiveData.postValue(null)

        val res = movieRepository.getMovie(100.toLong()).getValueBlocking()
        assertEquals(Status.ERROR, res?.status)
        assertEquals(null, res?.data)
    }

    @Test
    fun getMoviesByCinemaReturnsMovieLiveDataWithSuccessIfExists() {
        `when`(movieCache.getMoviesByCinema(100.toLong())).thenReturn(dbMovieLiveData)
        dbMovieLiveData.postValue(dbMovies)

        val res = movieRepository.getMoviesByCinema(100.toLong()).getValueBlocking()
        assertEquals(Status.SUCCESS, res?.status)
        assertEquals(dbMovies, res?.data)
    }

    @Test
    fun getMoviesByCinemaReturnsMovieLiveDataWithErrorIfNullCache() {
        `when`(movieCache.getMoviesByCinema(100.toLong())).thenReturn(dbMovieLiveData)
        dbMovieLiveData.postValue(null)

        val res = movieRepository.getMoviesByCinema(100.toLong()).getValueBlocking()
        assertEquals(Status.ERROR, res?.status)
        assertEquals(null, res?.data)
    }

    @Test
    fun getMoviesByCinemaReturnsMovieLiveDataWithErrorIfEmptyCache() {
        `when`(movieCache.getMoviesByCinema(100.toLong())).thenReturn(dbMovieLiveData)
        dbMovieLiveData.postValue(listOf<Movie>())

        val res = movieRepository.getMoviesByCinema(100.toLong()).getValueBlocking()
        assertEquals(Status.ERROR, res?.status)
        assertEquals(listOf<Movie>(), res?.data)
    }


    @Test
    fun hasExpiredReturnsTrueIfExpired() {
        val currentTime = System.currentTimeMillis()
        `when`(preferencesHelper.movieslastUpdateTime)
            .thenReturn(currentTime - (MovieRepository.EXPIRATION_TIME + 500))
        TestCase.assertEquals(true, movieRepository.hasExpired())
    }

    @Test
    fun isExpiredReturnsFalseIfNotExpired() {
        val currentTime = System.currentTimeMillis()
        `when`(preferencesHelper.movieslastUpdateTime).thenReturn(currentTime - 5000)
        TestCase.assertEquals(false, movieRepository.hasExpired())
    }

    @Test
    fun companionObjectTest() {
        TestCase.assertEquals((3 * 60 * 60 * 1000).toLong(), MovieRepository.EXPIRATION_TIME)
    }
}