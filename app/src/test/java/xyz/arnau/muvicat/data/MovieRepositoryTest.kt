package xyz.arnau.muvicat.data

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import junit.framework.TestCase
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito
import org.mockito.Mockito.*
import xyz.arnau.muvicat.AppExecutors
import xyz.arnau.muvicat.data.model.Movie
import xyz.arnau.muvicat.data.model.Resource
import xyz.arnau.muvicat.data.model.Status
import xyz.arnau.muvicat.data.repository.GencatRemote
import xyz.arnau.muvicat.data.repository.MovieCache
import xyz.arnau.muvicat.data.test.MovieFactory
import xyz.arnau.muvicat.data.utils.RepoPreferencesHelper
import xyz.arnau.muvicat.remote.model.Response
import xyz.arnau.muvicat.remote.model.ResponseStatus
import xyz.arnau.muvicat.utils.InstantAppExecutors
import xyz.arnau.muvicat.utils.getValueBlocking

@RunWith(JUnit4::class)
class MovieRepositoryTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val movieCache: MovieCache = mock(MovieCache::class.java)
    private val gencatRemote: GencatRemote = mock(GencatRemote::class.java)
    private val appExecutors: AppExecutors = InstantAppExecutors()
    private val preferencesHelper: RepoPreferencesHelper = mock(RepoPreferencesHelper::class.java)

    private val movieRepository =
        MovieRepository(movieCache, gencatRemote, appExecutors, preferencesHelper)

    @Test
    fun getMoviesWhenMoviesAreCachedAndNotExpired() {
        val dbMovieLiveData = MutableLiveData<List<Movie>>()
        `when`(movieCache.getMovies()).thenReturn(dbMovieLiveData)
        val dbMovies = MovieFactory.makeMovieList(3)
        dbMovieLiveData.postValue(dbMovies)
        val currentTime = System.currentTimeMillis()
        `when`(preferencesHelper.movieslastUpdateTime).thenReturn(currentTime - 5000)

        val movies = movieRepository.getMovies()
        val observer: Observer<*>? = mock(Observer::class.java)
        @Suppress("UNCHECKED_CAST")
        movies.observeForever(observer as Observer<Resource<List<Movie>>>)
        verify(observer).onChanged(Resource.success(dbMovies))
        verify(preferencesHelper, never()).moviesUpdated()
        verify(gencatRemote, never()).getMovies()
    }

    @Test
    fun getMoviesWhenMoviesAreCachedButExpired() {
        val dbMovieLiveData = MutableLiveData<List<Movie>>()
        `when`(movieCache.getMovies()).thenReturn(dbMovieLiveData)
        val dbMovies = MovieFactory.makeMovieList(3)

        val currentTime = System.currentTimeMillis()
        `when`(preferencesHelper.movieslastUpdateTime)
            .thenReturn(currentTime - (MovieRepository.EXPIRATION_TIME + 500))

        val remoteMovieLiveData = MutableLiveData<Response<List<Movie>>>()
        val remoteMovies = MovieFactory.makeMovieList(3)
        `when`(gencatRemote.getMovies()).thenReturn(remoteMovieLiveData)


        val movies = movieRepository.getMovies()

        val observer: Observer<*>? = mock(Observer::class.java)
        @Suppress("UNCHECKED_CAST")
        movies.observeForever(observer as Observer<Resource<List<Movie>>>)
        dbMovieLiveData.postValue(dbMovies)
        verify(observer).onChanged(Resource.loading(dbMovies))
        remoteMovieLiveData.postValue(
            Response(remoteMovies, null, ResponseStatus.SUCCESSFUL)
        )
        dbMovieLiveData.postValue(remoteMovies)
        verify(observer).onChanged(Resource.success(remoteMovies))
        verify(preferencesHelper).moviesUpdated()
        verify(movieCache).updateMovies(remoteMovies)
    }

    @Test
    fun getMoviesWhenMoviesAreCachedButExpiredNotModified() {
        val dbMovieLiveData = MutableLiveData<List<Movie>>()
        `when`(movieCache.getMovies()).thenReturn(dbMovieLiveData)
        val dbMovies = MovieFactory.makeMovieList(3)

        val currentTime = System.currentTimeMillis()
        `when`(preferencesHelper.movieslastUpdateTime)
            .thenReturn(currentTime - (MovieRepository.EXPIRATION_TIME + 500))

        val remoteMovieLiveData = MutableLiveData<Response<List<Movie>>>()
        `when`(gencatRemote.getMovies()).thenReturn(remoteMovieLiveData)


        val movies = movieRepository.getMovies()

        val observer: Observer<*>? = mock(Observer::class.java)
        @Suppress("UNCHECKED_CAST")
        movies.observeForever(observer as Observer<Resource<List<Movie>>>)
        dbMovieLiveData.postValue(dbMovies)
        verify(observer).onChanged(Resource.loading(dbMovies))
        remoteMovieLiveData.postValue(
            Response(null, null, ResponseStatus.NOT_MODIFIED)
        )
        verify(observer).onChanged(Resource.success(dbMovies))
        verify(preferencesHelper).moviesUpdated()
        verify(movieCache, never()).updateMovies(Mockito.anyList())
    }

    @Test
    fun getMoviesWhenMoviesAreNotCached() {
        val dbMovieLiveData = MutableLiveData<List<Movie>>()
        `when`(movieCache.getMovies()).thenReturn(dbMovieLiveData)
        val dbMovies = listOf<Movie>()
        val remoteMovieLiveData = MutableLiveData<Response<List<Movie>>>()
        val remoteMovies = MovieFactory.makeMovieList(3)
        `when`(gencatRemote.getMovies()).thenReturn(remoteMovieLiveData)


        val movies = movieRepository.getMovies()

        val observer: Observer<*>? = mock(Observer::class.java)
        @Suppress("UNCHECKED_CAST")
        movies.observeForever(observer as Observer<Resource<List<Movie>>>)
        dbMovieLiveData.postValue(dbMovies)
        verify(observer).onChanged(Resource.loading(dbMovies))
        remoteMovieLiveData.postValue(
            Response(remoteMovies, null, ResponseStatus.SUCCESSFUL)
        )
        dbMovieLiveData.postValue(remoteMovies)
        verify(observer).onChanged(Resource.success(remoteMovies))
        verify(preferencesHelper).moviesUpdated()
        verify(movieCache).updateMovies(remoteMovies)
    }

    @Test
    fun getMoviesWhenMoviesAreNotCachedWithNullResponseBody() {
        val dbMovieLiveData = MutableLiveData<List<Movie>>()
        `when`(movieCache.getMovies()).thenReturn(dbMovieLiveData)
        val dbMovies = listOf<Movie>()
        val remoteMovieLiveData = MutableLiveData<Response<List<Movie>>>()
        `when`(gencatRemote.getMovies()).thenReturn(remoteMovieLiveData)


        val movies = movieRepository.getMovies()

        val observer: Observer<*>? = mock(Observer::class.java)
        @Suppress("UNCHECKED_CAST")
        movies.observeForever(observer as Observer<Resource<List<Movie>>>)
        verify(observer).onChanged(Resource.loading(null))
        remoteMovieLiveData.postValue(
            Response(null, null, ResponseStatus.SUCCESSFUL)
        )
        dbMovieLiveData.postValue(dbMovies)
        verify(observer).onChanged(Resource.success(dbMovies))
        verify(preferencesHelper, never()).moviesUpdated()
        verify(movieCache, never()).updateMovies(Mockito.anyList())
    }

    @Test
    fun getMovieReturnsMovieLiveDataWithSuccessIfExists() {
        val movie = MovieFactory.makeMovie()
        val movieLiveData = MutableLiveData<Movie>()
        `when`(movieCache.getMovie(movie.id)).thenReturn(movieLiveData)
        movieLiveData.postValue(movie)

        val res = movieRepository.getMovie(movie.id).getValueBlocking()
        assertEquals(Status.SUCCESS, res?.status)
        assertEquals(movie, res?.data)
    }

    @Test
    fun getMovieReturnsMovieLiveDataWithErrorIfDoesNotExist() {
        val movieLiveData = MutableLiveData<Movie>()
        `when`(movieCache.getMovie(100.toLong())).thenReturn(movieLiveData)
        movieLiveData.postValue(null)

        val res = movieRepository.getMovie(100.toLong()).getValueBlocking()
        assertEquals(Status.ERROR, res?.status)
        assertEquals(null, res?.data)
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