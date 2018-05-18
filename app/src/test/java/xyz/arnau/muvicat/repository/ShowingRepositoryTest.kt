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
import xyz.arnau.muvicat.cache.model.ShowingEntity
import xyz.arnau.muvicat.repository.ShowingRepository.Companion.EXPIRATION_TIME
import xyz.arnau.muvicat.repository.model.*
import xyz.arnau.muvicat.repository.data.GencatRemote
import xyz.arnau.muvicat.repository.data.ShowingCache
import xyz.arnau.muvicat.repository.test.CinemaShowingMapper
import xyz.arnau.muvicat.repository.test.MovieShowingMapper
import xyz.arnau.muvicat.repository.test.ShowingEntityFactory
import xyz.arnau.muvicat.repository.test.ShowingMapper
import xyz.arnau.muvicat.repository.utils.RepoPreferencesHelper
import xyz.arnau.muvicat.remote.DataUpdateCallback
import xyz.arnau.muvicat.remote.model.Response
import xyz.arnau.muvicat.utils.AfterCountDownLatch
import xyz.arnau.muvicat.utils.BeforeCountDownLatch
import xyz.arnau.muvicat.utils.InstantAppExecutors
import xyz.arnau.muvicat.utils.getValueBlocking

@RunWith(JUnit4::class)
class ShowingRepositoryTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val showingCache = mock(ShowingCache::class.java)
    private val gencatRemote = mock(GencatRemote::class.java)
    private val appExecutors = InstantAppExecutors()
    private val preferencesHelper = mock(RepoPreferencesHelper::class.java)
    private val beforeLatch = mock(BeforeCountDownLatch::class.java)
    private val afterLatch = mock(AfterCountDownLatch::class.java)
    private val showingRepository = ShowingRepository(showingCache, gencatRemote, appExecutors, preferencesHelper, beforeLatch, afterLatch)

    private val showings = ShowingEntityFactory.makeShowingEntityList(3)

    private val dbShowingLiveData = MutableLiveData<List<Showing>>()
    private val dbCinemaShowingLiveData = MutableLiveData<List<CinemaShowing>>()
    private val dbMovieShowingLiveData = MutableLiveData<List<MovieShowing>>()
    private val dbShowings = ShowingMapper.mapFromShowingEntityList(showings)
    private val dbCinemaShowings = CinemaShowingMapper.mapFromShowingEntityList(showings)
    private val dbMovieShowings = MovieShowingMapper.mapFromShowingEntityList(showings)

    private val remoteShowingLiveData = MutableLiveData<Response<List<ShowingEntity>>>()
    private val remoteShowings = ShowingEntityFactory.makeShowingEntityList(3)
    private val remoteShowingsMapped = ShowingMapper.mapFromShowingEntityList(remoteShowings)

    private val observer: Observer<*>? = mock(Observer::class.java)
    private val currentTime = System.currentTimeMillis()
    private val callback = mock(DataUpdateCallback::class.java)


    @Before
    fun setUp() {
        `when`(showingCache.getShowings()).thenReturn(dbShowingLiveData)
        `when`(gencatRemote.getShowings()).thenReturn(remoteShowingLiveData)
    }

    @Test
    fun getShowingsWhenShowingsAreCachedAndNotExpired() {
        dbShowingLiveData.postValue(dbShowings)
        `when`(preferencesHelper.showingslastUpdateTime).thenReturn(currentTime - 5000)

        val showings = showingRepository.getShowings()
        showings.observeForever(observer as Observer<Resource<List<Showing>>>)

        verify(observer).onChanged(Resource.success(dbShowings))

        verify(preferencesHelper, never()).showingsUpdated()
        verify(gencatRemote, never()).getShowings()
    }
    

    @Test
    fun getShowingsWhenShowingsAreCachedButExpiredSuccesfulWithCallback() {
        `when`(preferencesHelper.showingslastUpdateTime).thenReturn(currentTime - (EXPIRATION_TIME + 500))
        `when`(showingCache.updateShowings(remoteShowings)).thenReturn(true)

        val showings = showingRepository.getShowings()
        showings.observeForever(observer as Observer<Resource<List<Showing>>>)

        beforeLatch.countDown()
        beforeLatch.countDown()

        dbShowingLiveData.postValue(dbShowings)
        remoteShowingLiveData.postValue(Response.successful(remoteShowings, callback))
        dbShowingLiveData.postValue(remoteShowingsMapped)

        verify(observer).onChanged(Resource.loading(dbShowings))
        verify(observer).onChanged(Resource.success(remoteShowingsMapped))
        verify(callback).onDataUpdated()

        verify(preferencesHelper).showingsUpdated()
        verify(showingCache).updateShowings(remoteShowings)
    }

    @Test
    fun getShowingsWhenShowingsAreCachedButExpiredSuccesfulWithoutCallback() {
        `when`(preferencesHelper.showingslastUpdateTime).thenReturn(currentTime - (EXPIRATION_TIME + 500))
        `when`(showingCache.updateShowings(remoteShowings)).thenReturn(true)

        val showings = showingRepository.getShowings()
        showings.observeForever(observer as Observer<Resource<List<Showing>>>)

        beforeLatch.countDown()
        beforeLatch.countDown()

        dbShowingLiveData.postValue(dbShowings)
        remoteShowingLiveData.postValue(Response.successful(remoteShowings, null))
        dbShowingLiveData.postValue(remoteShowingsMapped)

        verify(observer).onChanged(Resource.loading(dbShowings))
        verify(observer).onChanged(Resource.success(remoteShowingsMapped))

        verify(preferencesHelper).showingsUpdated()
        verify(showingCache).updateShowings(remoteShowings)
    }

    @Test
    fun getShowingsWhenShowingsAreCachedButExpiredNotModified() {
        `when`(preferencesHelper.showingslastUpdateTime).thenReturn(currentTime - (EXPIRATION_TIME + 500))

        val showings = showingRepository.getShowings()
        showings.observeForever(observer as Observer<Resource<List<Showing>>>)

        dbShowingLiveData.postValue(dbShowings)
        remoteShowingLiveData.postValue(Response.notModified(callback))

        verify(observer).onChanged(Resource.loading(dbShowings))
        verify(observer).onChanged(Resource.success(dbShowings))
        verify(callback, never()).onDataUpdated()

        verify(preferencesHelper).showingsUpdated()
        verify(showingCache, never()).updateShowings(Mockito.anyList())
    }

    @Test
    fun getShowingsWhenShowingsAreCachedButExpiredError() {
        `when`(preferencesHelper.showingslastUpdateTime).thenReturn(currentTime - (EXPIRATION_TIME + 500))

        val showings = showingRepository.getShowings()
        showings.observeForever(observer as Observer<Resource<List<Showing>>>)

        dbShowingLiveData.postValue(dbShowings)
        remoteShowingLiveData.postValue(Response.error("error msg", callback))

        verify(observer).onChanged(Resource.loading(dbShowings))
        verify(observer).onChanged(Resource.error("error msg", dbShowings))
        verify(callback, never()).onDataUpdated()

        verify(preferencesHelper, never()).showingsUpdated()
        verify(showingCache, never()).updateShowings(remoteShowings)
    }

    @Test
    fun getShowingsWhenShowingsAreNullSuccesful() {
        `when`(showingCache.updateShowings(remoteShowings)).thenReturn(true)

        val showings = showingRepository.getShowings()
        showings.observeForever(observer as Observer<Resource<List<Showing>>>)

        beforeLatch.countDown()
        beforeLatch.countDown()

        dbShowingLiveData.postValue(null)
        remoteShowingLiveData.postValue(Response.successful(remoteShowings, callback))
        dbShowingLiveData.postValue(remoteShowingsMapped)

        verify(observer).onChanged(Resource.loading(null))
        verify(observer).onChanged(Resource.success(remoteShowingsMapped))
        verify(callback).onDataUpdated()

        verify(preferencesHelper).showingsUpdated()
        verify(showingCache).updateShowings(remoteShowings)
    }

    @Test
    fun getShowingsWhenShowingsAreNotCachedSuccesful() {
        `when`(showingCache.updateShowings(remoteShowings)).thenReturn(true)

        val showings = showingRepository.getShowings()
        showings.observeForever(observer as Observer<Resource<List<Showing>>>)

        beforeLatch.countDown()
        beforeLatch.countDown()

        dbShowingLiveData.postValue(listOf())
        remoteShowingLiveData.postValue(Response.successful(remoteShowings, callback))
        dbShowingLiveData.postValue(remoteShowingsMapped)

        verify(observer).onChanged(Resource.loading(listOf()))
        verify(observer).onChanged(Resource.success(remoteShowingsMapped))
        verify(callback).onDataUpdated()

        verify(preferencesHelper).showingsUpdated()
        verify(showingCache).updateShowings(remoteShowings)
    }

    @Test
    fun getShowingsWhenShowingsAreNotCachedSuccesfulButNotInserted() {
        `when`(showingCache.updateShowings(remoteShowings)).thenReturn(false)

        val showings = showingRepository.getShowings()
        showings.observeForever(observer as Observer<Resource<List<Showing>>>)

        beforeLatch.countDown()
        beforeLatch.countDown()

        dbShowingLiveData.postValue(listOf())
        remoteShowingLiveData.postValue(Response.successful(remoteShowings, callback))
        dbShowingLiveData.postValue(remoteShowingsMapped)

        verify(observer).onChanged(Resource.loading(listOf()))
        verify(observer).onChanged(Resource.success(remoteShowingsMapped))
        verify(callback, never()).onDataUpdated()

        verify(preferencesHelper, never()).showingsUpdated()
        verify(showingCache).updateShowings(remoteShowings)
    }

    @Test
    fun getShowingsWhenShowingsAreNotCachedSucessfulWithNullResponseBody() {
        val showings = showingRepository.getShowings()
        showings.observeForever(observer as Observer<Resource<List<Showing>>>)
        dbShowingLiveData.postValue(listOf())

        remoteShowingLiveData.postValue(Response.successful(null, callback))

        verify(observer).onChanged(Resource.loading(listOf()))
        verify(observer).onChanged(Resource.error(null, listOf()))
        verify(callback, never()).onDataUpdated()

        verify(preferencesHelper, never()).showingsUpdated()
        verify(showingCache, never()).updateShowings(Mockito.anyList())
    }

    @Test
    fun getShowingsWhenShowingsAreNotCachedError() {
        val showings = showingRepository.getShowings()
        showings.observeForever(observer as Observer<Resource<List<Showing>>>)
        dbShowingLiveData.postValue(listOf())

        remoteShowingLiveData.postValue(Response.error("error msg", callback))

        verify(observer).onChanged(Resource.loading(listOf()))
        verify(observer).onChanged(Resource.error("error msg", listOf()))
        verify(callback, never()).onDataUpdated()

        verify(preferencesHelper, never()).showingsUpdated()
        verify(showingCache, never()).updateShowings(remoteShowings)
    }


    @Test
    fun getShowingsByCinemaReturnsShowingsLiveDataWithSuccessIfExists() {
        `when`(showingCache.getShowingsByCinema(100.toLong())).thenReturn(dbCinemaShowingLiveData)
        dbCinemaShowingLiveData.postValue(dbCinemaShowings)

        val res = showingRepository.getShowingsByCinema(100.toLong()).getValueBlocking()
        assertEquals(Status.SUCCESS, res?.status)
        assertEquals(dbCinemaShowings, res?.data)
    }

    @Test
    fun getShowingsByCinemaReturnsShowingsLiveDataWithErrorIfNullCache() {
        `when`(showingCache.getShowingsByCinema(100.toLong())).thenReturn(dbCinemaShowingLiveData)
        dbCinemaShowingLiveData.postValue(null)

        val res = showingRepository.getShowingsByCinema(100.toLong()).getValueBlocking()
        assertEquals(Status.ERROR, res?.status)
        assertEquals(null, res?.data)
    }

    @Test
    fun getShowingsByCinemaReturnsShowingsLiveDataWithErrorIfEmptyCache() {
        `when`(showingCache.getShowingsByCinema(100.toLong())).thenReturn(dbCinemaShowingLiveData)
        dbCinemaShowingLiveData.postValue(listOf())

        val res = showingRepository.getShowingsByCinema(100.toLong()).getValueBlocking()
        assertEquals(Status.ERROR, res?.status)
        assertEquals(listOf<CinemaShowing>(), res?.data)
    }


    @Test
    fun getShowingsByMovieReturnsShowingsLiveDataWithSuccessIfExists() {
        `when`(showingCache.getShowingsByMovie(100.toLong())).thenReturn(dbMovieShowingLiveData)
        dbMovieShowingLiveData.postValue(dbMovieShowings)

        val res = showingRepository.getShowingsByMovie(100.toLong()).getValueBlocking()
        assertEquals(Status.SUCCESS, res?.status)
        assertEquals(dbMovieShowings, res?.data)
    }

    @Test
    fun getShowingsByMovieReturnsShowingsLiveDataWithErrorIfNullCache() {
        `when`(showingCache.getShowingsByMovie(100.toLong())).thenReturn(dbMovieShowingLiveData)
        dbMovieShowingLiveData.postValue(null)

        val res = showingRepository.getShowingsByMovie(100.toLong()).getValueBlocking()
        assertEquals(Status.ERROR, res?.status)
        assertEquals(null, res?.data)
    }

    @Test
    fun getShowingsByMovieReturnsShowingsLiveDataWithErrorIfEmptyCache() {
        `when`(showingCache.getShowingsByMovie(100.toLong())).thenReturn(dbMovieShowingLiveData)
        dbMovieShowingLiveData.postValue(listOf())

        val res = showingRepository.getShowingsByMovie(100.toLong()).getValueBlocking()
        assertEquals(Status.ERROR, res?.status)
        assertEquals(listOf<MovieShowing>(), res?.data)
    }



    @Test
    fun hasExpiredReturnsTrueIfExpired() {
        val currentTime = System.currentTimeMillis()
        `when`(preferencesHelper.showingslastUpdateTime)
            .thenReturn(currentTime - (ShowingRepository.EXPIRATION_TIME + 500))
        TestCase.assertEquals(true, showingRepository.hasExpired())
    }

    @Test
    fun isExpiredReturnsFalseIfNotExpired() {
        val currentTime = System.currentTimeMillis()
        `when`(preferencesHelper.showingslastUpdateTime).thenReturn(currentTime - 5000)
        TestCase.assertEquals(false, showingRepository.hasExpired())
    }

    @Test
    fun companionObjectTest() {
        TestCase.assertEquals((3 * 60 * 60 * 1000).toLong(), ShowingRepository.EXPIRATION_TIME)
    }
}