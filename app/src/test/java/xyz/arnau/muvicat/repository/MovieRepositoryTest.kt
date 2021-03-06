@file:Suppress("UNCHECKED_CAST")

package xyz.arnau.muvicat.repository

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.database.sqlite.SQLiteConstraintException
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
import xyz.arnau.muvicat.cache.model.MovieExtraInfo
import xyz.arnau.muvicat.remote.DataUpdateCallback
import xyz.arnau.muvicat.remote.model.Response
import xyz.arnau.muvicat.repository.MovieRepository.Companion.EXPIRATION_TIME
import xyz.arnau.muvicat.repository.data.GencatRemote
import xyz.arnau.muvicat.repository.data.MovieCache
import xyz.arnau.muvicat.repository.data.TMDBRemote
import xyz.arnau.muvicat.repository.model.Movie
import xyz.arnau.muvicat.repository.model.MovieWithCast
import xyz.arnau.muvicat.repository.model.Resource
import xyz.arnau.muvicat.repository.model.Status
import xyz.arnau.muvicat.repository.test.*
import xyz.arnau.muvicat.repository.utils.RepoPreferencesHelper
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
    private val tmdbRemote = mock(TMDBRemote::class.java)
    private val appExecutors = InstantAppExecutors()
    private val preferencesHelper = mock(RepoPreferencesHelper::class.java)
    private val beforeLatch = mock(BeforeCountDownLatch::class.java)
    private val afterLatch = mock(AfterCountDownLatch::class.java)
    private val movieRepository = MovieRepository(
        movieCache,
        gencatRemote,
        tmdbRemote,
        appExecutors,
        preferencesHelper,
        beforeLatch,
        afterLatch
    )

    private val dbMoviesLiveData = MutableLiveData<List<Movie>>()
    private val dbMovies =
        MovieMapper.mapFromMovieEntityList(MovieEntityFactory.makeMovieEntityList(3))

    private val remoteMovieLiveData = MutableLiveData<Response<List<MovieEntity>>>()
    private val remoteMovies = MovieEntityFactory.makeMovieEntityList(3)
    private val remoteMoviesMapped = MovieMapper.mapFromMovieEntityList(remoteMovies)

    private val observer: Observer<*>? = mock(Observer::class.java)
    private val currentTime = System.currentTimeMillis()
    private val callback = mock(DataUpdateCallback::class.java)

    private val remoteExtraInfoLiveData = MutableLiveData<Response<MovieExtraInfo>>()
    private val remoteMovieExtraInfo = MovieExtraInfoFactory.makeExtraInfo()

    private var movieWithCast =
        MovieWithCastMapper.mapFromMovieEntity(MovieEntityFactory.makeMovieEntity())
    private var updatedMovieWithCast = movieWithCast.apply {
        this.castMembers = CastMemberMapper.mapFromCastMemberEntityList(remoteMovieExtraInfo.cast!!)
        this.movie.tmdbId = remoteMovieExtraInfo.tmdbId
        this.movie.runtime = remoteMovieExtraInfo.runtime
        this.movie.genres = remoteMovieExtraInfo.genres
        this.movie.backdropUrl = remoteMovieExtraInfo.backdropUrl
        this.movie.voteAverage = remoteMovieExtraInfo.voteAverage
        this.movie.voteCount = remoteMovieExtraInfo.voteCount
    }
    private val dbMovieWithCastLiveData = MutableLiveData<MovieWithCast>()
    private val rateMovieLiveData = MutableLiveData<Response<Boolean>>()

    @Before
    fun setUp() {
        `when`(movieCache.getMovies()).thenReturn(dbMoviesLiveData)
        `when`(gencatRemote.getMovies()).thenReturn(remoteMovieLiveData)

        `when`(movieCache.getMovie(movieWithCast.movie.id)).thenReturn(dbMovieWithCastLiveData)
        `when`(tmdbRemote.getMovie(movieWithCast.movie.originalTitle!!)).thenReturn(remoteExtraInfoLiveData)
        `when`(tmdbRemote.getMovie(movieWithCast.movie.tmdbId!!)).thenReturn(remoteExtraInfoLiveData)
        `when`(tmdbRemote.getMovie(movieWithCast.movie.title!!)).thenReturn(remoteExtraInfoLiveData)
        `when`(tmdbRemote.rateMovie(anyInt(), anyDouble())).thenReturn(rateMovieLiveData)
        `when`(tmdbRemote.unrateMovie(anyInt())).thenReturn(rateMovieLiveData)
    }

    @Test
    fun getMoviesWhenMoviesAreCachedAndNotExpired() {
        dbMoviesLiveData.postValue(dbMovies)
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

        dbMoviesLiveData.postValue(dbMovies)
        remoteMovieLiveData.postValue(Response.successful(remoteMovies, callback))
        dbMoviesLiveData.postValue(remoteMoviesMapped)

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

        dbMoviesLiveData.postValue(dbMovies)
        remoteMovieLiveData.postValue(Response.successful(remoteMovies, null))
        dbMoviesLiveData.postValue(remoteMoviesMapped)

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

        dbMoviesLiveData.postValue(dbMovies)
        remoteMovieLiveData.postValue(Response.successful(remoteMovies, null))
        dbMoviesLiveData.postValue(remoteMoviesMapped)

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

        dbMoviesLiveData.postValue(dbMovies)
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

        dbMoviesLiveData.postValue(dbMovies)
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

        dbMoviesLiveData.postValue(dbMovies)
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

        dbMoviesLiveData.postValue(null)
        remoteMovieLiveData.postValue(Response.successful(remoteMovies, callback))
        dbMoviesLiveData.postValue(remoteMoviesMapped)

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

        dbMoviesLiveData.postValue(listOf())
        remoteMovieLiveData.postValue(Response.successful(remoteMovies, callback))
        dbMoviesLiveData.postValue(remoteMoviesMapped)

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
        dbMoviesLiveData.postValue(listOf())

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
        dbMoviesLiveData.postValue(listOf())

        remoteMovieLiveData.postValue(Response.error("error msg", callback))

        verify(observer).onChanged(Resource.loading(listOf()))
        verify(observer).onChanged(Resource.error("error msg", listOf()))
        verify(callback, never()).onDataUpdated()

        verify(preferencesHelper, never()).moviesUpdated()
        verify(beforeLatch).countDown()
        verify(movieCache, never()).updateMovies(remoteMovies)
    }



    @Test
    fun getVOTEDMoviesReturnsMovieLiveDataWithSuccessIfExists() {
        `when`(movieCache.getVotedMovies()).thenReturn(dbMoviesLiveData)
        dbMoviesLiveData.postValue(dbMovies)

        val res = movieRepository.getVotedMovies().getValueBlocking()
        assertEquals(Status.SUCCESS, res?.status)
        assertEquals(dbMovies, res?.data)
    }

    @Test
    fun getVotedMoviesReturnsMovieLiveDataWithErrorIfNullCache() {
        `when`(movieCache.getVotedMovies()).thenReturn(dbMoviesLiveData)
        dbMoviesLiveData.postValue(null)

        val res = movieRepository.getVotedMovies().getValueBlocking()
        assertEquals(Status.ERROR, res?.status)
        assertEquals(null, res?.data)
    }

    @Test
    fun getVotedMoviesReturnsMovieLiveDataIfEmptyCache() {
        `when`(movieCache.getVotedMovies()).thenReturn(dbMoviesLiveData)
        dbMoviesLiveData.postValue(listOf())

        val res = movieRepository.getVotedMovies().getValueBlocking()
        assertEquals(Status.SUCCESS, res?.status)
        assertEquals(listOf<Movie>(), res?.data)
    }



    @Test
    fun getMoviesByCinemaReturnsMovieLiveDataWithSuccessIfExists() {
        `when`(movieCache.getMoviesByCinema(100.toLong())).thenReturn(dbMoviesLiveData)
        dbMoviesLiveData.postValue(dbMovies)

        val res = movieRepository.getMoviesByCinema(100.toLong()).getValueBlocking()
        assertEquals(Status.SUCCESS, res?.status)
        assertEquals(dbMovies, res?.data)
    }

    @Test
    fun getMoviesByCinemaReturnsMovieLiveDataWithErrorIfNullCache() {
        `when`(movieCache.getMoviesByCinema(100.toLong())).thenReturn(dbMoviesLiveData)
        dbMoviesLiveData.postValue(null)

        val res = movieRepository.getMoviesByCinema(100.toLong()).getValueBlocking()
        assertEquals(Status.ERROR, res?.status)
        assertEquals(null, res?.data)
    }

    @Test
    fun getMoviesByCinemaReturnsMovieLiveDataWithErrorIfEmptyCache() {
        `when`(movieCache.getMoviesByCinema(100.toLong())).thenReturn(dbMoviesLiveData)
        dbMoviesLiveData.postValue(listOf())

        val res = movieRepository.getMoviesByCinema(100.toLong()).getValueBlocking()
        assertEquals(Status.ERROR, res?.status)
        assertEquals(listOf<Movie>(), res?.data)
    }

    @Test
    fun getMovieWithNullDbResponse() {
        dbMovieWithCastLiveData.postValue(null)
        remoteExtraInfoLiveData.postValue(Response.successful(remoteMovieExtraInfo))

        val res = movieRepository.getMovie(movieWithCast.movie.id).getValueBlocking()

        assertEquals(Status.SUCCESS, res?.status)
        assertEquals(null, res?.data)
        assertEquals(null, res?.message)
    }

    @Test
    fun getMovieWithSuccessfulRemoteResponseWithTmdbId() {
        val result = movieRepository.getMovie(movieWithCast.movie.id)
        result.observeForever(observer as Observer<Resource<MovieWithCast>>)

        dbMovieWithCastLiveData.postValue(movieWithCast)
        verify(observer).onChanged(Resource.loading(movieWithCast))

        remoteExtraInfoLiveData.postValue(Response.successful(remoteMovieExtraInfo))
        verify(observer).onChanged(Resource.success(updatedMovieWithCast))

        verify(tmdbRemote).getMovie(movieWithCast.movie.tmdbId!!)
        verify(movieCache).updateExtraMovieInfo(movieWithCast.movie.id, remoteMovieExtraInfo)
    }

    @Test
    fun getMovieWithSuccessfulRemoteResponseWithOriginalTitle() {
        val result = movieRepository.getMovie(movieWithCast.movie.id)
        result.observeForever(observer as Observer<Resource<MovieWithCast>>)
        val movieWithCast2 = movieWithCast.copy()
        movieWithCast2.movie.tmdbId = null

        dbMovieWithCastLiveData.postValue(movieWithCast2)
        verify(observer).onChanged(Resource.loading(movieWithCast2))

        remoteExtraInfoLiveData.postValue(Response.successful(remoteMovieExtraInfo))
        verify(observer).onChanged(Resource.success(updatedMovieWithCast))

        verify(tmdbRemote).getMovie(movieWithCast.movie.originalTitle!!)
        verify(movieCache).updateExtraMovieInfo(movieWithCast.movie.id, remoteMovieExtraInfo)
    }

    @Test
    fun getMovieWithSuccessfulRemoteResponseWithTitle() {
        val result = movieRepository.getMovie(movieWithCast.movie.id)
        result.observeForever(observer as Observer<Resource<MovieWithCast>>)
        val movieWithCast2 = movieWithCast.copy()
        movieWithCast2.movie.tmdbId = null
        movieWithCast2.movie.originalTitle = null

        dbMovieWithCastLiveData.postValue(movieWithCast2)
        verify(observer).onChanged(Resource.loading(movieWithCast2))

        remoteExtraInfoLiveData.postValue(Response.successful(remoteMovieExtraInfo))
        verify(observer).onChanged(Resource.success(updatedMovieWithCast))

        verify(tmdbRemote).getMovie(movieWithCast.movie.title!!)
        verify(movieCache).updateExtraMovieInfo(movieWithCast.movie.id, remoteMovieExtraInfo)
    }

    @Test
    fun getMovieWithErrorRemoteResponse() {
        val result = movieRepository.getMovie(movieWithCast.movie.id)
        result.observeForever(observer as Observer<Resource<MovieWithCast>>)

        dbMovieWithCastLiveData.postValue(movieWithCast)
        verify(observer).onChanged(Resource.loading(movieWithCast))

        remoteExtraInfoLiveData.postValue(Response.error("error msg"))
        verify(observer).onChanged(Resource.error("error msg", movieWithCast))

        verify(tmdbRemote).getMovie(movieWithCast.movie.tmdbId!!)
        verify(movieCache, never()).updateExtraMovieInfo(movieWithCast.movie.id, remoteMovieExtraInfo)
    }


    @Test
    fun rateMovieWithSuccess() {
        rateMovieLiveData.postValue(Response.successful(true))

        val result = movieRepository.rateMovie(100.toLong(), 200, 5.0).getValueBlocking()
        assertEquals(true, result!!.data)
        assertEquals(null, result.message)
        assertEquals(Status.SUCCESS, result.status)

        verify(tmdbRemote).rateMovie(200, 5.0)
        verify(movieCache).voteMovie(100.toLong(), 5.0)
    }

    @Test
    fun rateMovieWithRemoteError() {
        rateMovieLiveData.postValue(Response.error("error msg"))

        val result = movieRepository.rateMovie(100.toLong(), 200, 5.0).getValueBlocking()
        assertEquals(null, result!!.data)
        assertEquals("error msg", result.message)
        assertEquals(Status.ERROR, result.status)

        verify(tmdbRemote).rateMovie(200, 5.0)
        verify(movieCache, never()).voteMovie(100.toLong(), 5.0)
    }

    @Test
    fun rateMovieWithDbError() {
        rateMovieLiveData.postValue(Response.successful(true))
        `when`(movieCache.voteMovie(100.toLong(), 5.0)).thenThrow(SQLiteConstraintException())

        val result = movieRepository.rateMovie(100.toLong(), 200, 5.0).getValueBlocking()
        assertEquals(null, result!!.data)
        assertEquals("db error", result.message)
        assertEquals(Status.ERROR, result.status)

        verify(tmdbRemote).rateMovie(200, 5.0)
        verify(movieCache).voteMovie(100.toLong(), 5.0)
    }

    @Test
    fun unrateMovieWithSuccess() {
        rateMovieLiveData.postValue(Response.successful(true))

        val result = movieRepository.unrateMovie(100.toLong(), 200).getValueBlocking()
        assertEquals(true, result!!.data)
        assertEquals(null, result.message)
        assertEquals(Status.SUCCESS, result.status)

        verify(tmdbRemote).unrateMovie(200)
        verify(movieCache).unvoteMovie(100.toLong())
    }

    @Test
    fun unrateMovieWithRemoteError() {
        rateMovieLiveData.postValue(Response.error("error msg"))

        val result = movieRepository.unrateMovie(100.toLong(), 200).getValueBlocking()
        assertEquals(null, result!!.data)
        assertEquals("error msg", result.message)
        assertEquals(Status.ERROR, result.status)

        verify(tmdbRemote).unrateMovie(200)
        verify(movieCache, never()).unvoteMovie(100.toLong())
    }

    @Test
    fun unrateMovieWithDbError() {
        rateMovieLiveData.postValue(Response.successful(true))
        `when`(movieCache.unvoteMovie(100.toLong())).thenThrow(SQLiteConstraintException())

        val result = movieRepository.unrateMovie(100.toLong(), 200).getValueBlocking()
        assertEquals(null, result!!.data)
        assertEquals("db error", result.message)
        assertEquals(Status.ERROR, result.status)

        verify(tmdbRemote).unrateMovie(200)
        verify(movieCache).unvoteMovie(100.toLong())
    }

    ////

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