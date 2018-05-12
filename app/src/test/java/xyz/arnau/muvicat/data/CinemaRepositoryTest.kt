@file:Suppress("UNCHECKED_CAST")

package xyz.arnau.muvicat.data

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
import xyz.arnau.muvicat.cache.model.CinemaEntity
import xyz.arnau.muvicat.data.CinemaRepository.Companion.EXPIRATION_TIME
import xyz.arnau.muvicat.data.model.Cinema
import xyz.arnau.muvicat.data.model.Resource
import xyz.arnau.muvicat.data.model.Status
import xyz.arnau.muvicat.data.repository.GencatRemote
import xyz.arnau.muvicat.data.repository.CinemaCache
import xyz.arnau.muvicat.data.test.CinemaEntityFactory
import xyz.arnau.muvicat.data.test.CinemaMapper
import xyz.arnau.muvicat.data.utils.RepoPreferencesHelper
import xyz.arnau.muvicat.remote.DataUpdateCallback
import xyz.arnau.muvicat.remote.model.Response
import xyz.arnau.muvicat.utils.InstantAppExecutors
import xyz.arnau.muvicat.utils.getValueBlocking
import java.util.concurrent.CountDownLatch

@RunWith(JUnit4::class)
class CinemaRepositoryTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val cinemaCache = mock(CinemaCache::class.java)
    private val gencatRemote = mock(GencatRemote::class.java)
    private val appExecutors = InstantAppExecutors()
    private val preferencesHelper = mock(RepoPreferencesHelper::class.java)
    private val countDownLatch = mock(CountDownLatch::class.java)
    private val cinemaRepository = CinemaRepository(cinemaCache, gencatRemote, appExecutors, preferencesHelper, countDownLatch)

    private val dbCinemaLiveData = MutableLiveData<List<Cinema>>()
    private val dbCinemas = CinemaMapper.mapFromCinemaEntityList(CinemaEntityFactory.makeCinemaEntityList(3))

    private val remoteCinemaLiveData = MutableLiveData<Response<List<CinemaEntity>>>()
    private val remoteCinemas = CinemaEntityFactory.makeCinemaEntityList(3)
    private val remoteCinemasMapped = CinemaMapper.mapFromCinemaEntityList(remoteCinemas)

    private val observer: Observer<*>? = mock(Observer::class.java)
    private val currentTime = System.currentTimeMillis()
    private val callback = mock(DataUpdateCallback::class.java)

    private val cinema = CinemaMapper.mapFromCinemaEntity(CinemaEntityFactory.makeCinemaEntity())
    private val cinemaLiveData = MutableLiveData<Cinema>()

    @Before
    fun setUp() {
        `when`(cinemaCache.getCinemas()).thenReturn(dbCinemaLiveData)
        `when`(gencatRemote.getCinemas()).thenReturn(remoteCinemaLiveData)
    }

    @Test
    fun getCinemasWhenCinemasAreCachedAndNotExpired() {
        dbCinemaLiveData.postValue(dbCinemas)
        `when`(preferencesHelper.cinemaslastUpdateTime).thenReturn(currentTime - 5000)

        val cinemas = cinemaRepository.getCinemas()
        cinemas.observeForever(observer as Observer<Resource<List<Cinema>>>)

        verify(observer).onChanged(Resource.success(dbCinemas))

        verify(preferencesHelper, never()).cinemasUpdated()
        verify(countDownLatch).countDown()
        verify(gencatRemote, never()).getCinemas()
    }

    @Test
    fun getCinemasWhenCinemasAreCachedButExpiredSuccesfulWithCallback() {
        `when`(preferencesHelper.cinemaslastUpdateTime).thenReturn(currentTime - (EXPIRATION_TIME + 500))

        val cinemas = cinemaRepository.getCinemas()
        cinemas.observeForever(observer as Observer<Resource<List<Cinema>>>)

        dbCinemaLiveData.postValue(dbCinemas)
        remoteCinemaLiveData.postValue(Response.successful(remoteCinemas, callback))
        dbCinemaLiveData.postValue(remoteCinemasMapped)

        verify(observer).onChanged(Resource.loading(dbCinemas))
        verify(observer).onChanged(Resource.success(remoteCinemasMapped))
        verify(callback).onDataUpdated()

        verify(preferencesHelper).cinemasUpdated()
        verify(countDownLatch).countDown()
        verify(cinemaCache).updateCinemas(remoteCinemas)
    }

    @Test
    fun getCinemasWhenCinemasAreCachedButExpiredSuccesfulWithoutCallback() {
        `when`(preferencesHelper.cinemaslastUpdateTime).thenReturn(currentTime - (EXPIRATION_TIME + 500))

        val cinemas = cinemaRepository.getCinemas()
        cinemas.observeForever(observer as Observer<Resource<List<Cinema>>>)

        dbCinemaLiveData.postValue(dbCinemas)
        remoteCinemaLiveData.postValue(Response.successful(remoteCinemas, null))
        dbCinemaLiveData.postValue(remoteCinemasMapped)

        verify(observer).onChanged(Resource.loading(dbCinemas))
        verify(observer).onChanged(Resource.success(remoteCinemasMapped))

        verify(preferencesHelper).cinemasUpdated()
        verify(countDownLatch).countDown()
        verify(cinemaCache).updateCinemas(remoteCinemas)
    }

    @Test
    fun getCinemasWhenCinemasAreCachedButExpiredSuccesfulWithoutCallbackTwice() {
        `when`(preferencesHelper.cinemaslastUpdateTime).thenReturn(currentTime - (EXPIRATION_TIME + 500))

        val cinemas = cinemaRepository.getCinemas()
        cinemas.observeForever(observer as Observer<Resource<List<Cinema>>>)

        dbCinemaLiveData.postValue(dbCinemas)
        remoteCinemaLiveData.postValue(Response.successful(remoteCinemas, null))
        dbCinemaLiveData.postValue(remoteCinemasMapped)

        verify(observer).onChanged(Resource.loading(dbCinemas))
        verify(observer).onChanged(Resource.success(remoteCinemasMapped))


        val dbCinemaLiveData2 = MutableLiveData<List<Cinema>>()
        val remoteCinemaLiveData2 = MutableLiveData<Response<List<CinemaEntity>>>()
        `when`(cinemaCache.getCinemas()).thenReturn(dbCinemaLiveData2)
        `when`(gencatRemote.getCinemas()).thenReturn(remoteCinemaLiveData2)

        val cinemas2 = cinemaRepository.getCinemas()
        val observer2: Observer<*>? = mock(Observer::class.java)
        cinemas2.observeForever(observer2 as Observer<Resource<List<Cinema>>>)

        dbCinemaLiveData2.postValue(dbCinemas)
        remoteCinemaLiveData2.postValue(Response.successful(remoteCinemas, null))
        dbCinemaLiveData2.postValue(remoteCinemasMapped)

        verify(observer2).onChanged(Resource.loading(dbCinemas))
        verify(observer2).onChanged(Resource.success(remoteCinemasMapped))

        verify(preferencesHelper, times(2)).cinemasUpdated()
        verify(countDownLatch, times(1)).countDown()
        verify(cinemaCache, times(2)).updateCinemas(remoteCinemas)
    }

    @Test
    fun getCinemasWhenCinemasAreCachedButExpiredNotModified() {
        `when`(preferencesHelper.cinemaslastUpdateTime).thenReturn(currentTime - (EXPIRATION_TIME + 500))

        val cinemas = cinemaRepository.getCinemas()
        cinemas.observeForever(observer as Observer<Resource<List<Cinema>>>)

        dbCinemaLiveData.postValue(dbCinemas)
        remoteCinemaLiveData.postValue(Response.notModified(callback))

        verify(observer).onChanged(Resource.loading(dbCinemas))
        verify(observer).onChanged(Resource.success(dbCinemas))
        verify(callback, never()).onDataUpdated()

        verify(preferencesHelper).cinemasUpdated()
        verify(countDownLatch).countDown()
        verify(cinemaCache, never()).updateCinemas(Mockito.anyList())
    }

    @Test
    fun getCinemasWhenCinemasAreCachedButExpiredError() {
        `when`(preferencesHelper.cinemaslastUpdateTime).thenReturn(currentTime - (EXPIRATION_TIME + 500))

        val cinemas = cinemaRepository.getCinemas()
        cinemas.observeForever(observer as Observer<Resource<List<Cinema>>>)

        dbCinemaLiveData.postValue(dbCinemas)
        remoteCinemaLiveData.postValue(Response.error("error msg", callback))

        verify(observer).onChanged(Resource.loading(dbCinemas))
        verify(observer).onChanged(Resource.error("error msg", dbCinemas))
        verify(callback, never()).onDataUpdated()

        verify(preferencesHelper, never()).cinemasUpdated()
        verify(countDownLatch).countDown()
        verify(cinemaCache, never()).updateCinemas(remoteCinemas)
    }

    @Test
    fun getCinemasWhenCinemasAreCachedButExpiredErrorTwice() {
        `when`(preferencesHelper.cinemaslastUpdateTime).thenReturn(currentTime - (CinemaRepository.EXPIRATION_TIME + 500))

        val cinemas = cinemaRepository.getCinemas()
        cinemas.observeForever(observer as Observer<Resource<List<Cinema>>>)

        dbCinemaLiveData.postValue(dbCinemas)
        remoteCinemaLiveData.postValue(Response.error("error msg", callback))

        verify(observer).onChanged(Resource.loading(dbCinemas))
        verify(observer).onChanged(Resource.error("error msg", dbCinemas))


        val dbCinemaLiveData2 = MutableLiveData<List<Cinema>>()
        val remoteCinemaLiveData2 = MutableLiveData<Response<List<CinemaEntity>>>()
        `when`(cinemaCache.getCinemas()).thenReturn(dbCinemaLiveData2)
        `when`(gencatRemote.getCinemas()).thenReturn(remoteCinemaLiveData2)

        val cinemas2 = cinemaRepository.getCinemas()
        val observer2: Observer<*>? = mock(Observer::class.java)
        cinemas2.observeForever(observer2 as Observer<Resource<List<Cinema>>>)

        dbCinemaLiveData2.postValue(dbCinemas)
        remoteCinemaLiveData2.postValue(Response.error("error msg", callback))

        verify(observer2).onChanged(Resource.loading(dbCinemas))
        verify(observer2).onChanged(Resource.error("error msg", dbCinemas))

        verify(callback, never()).onDataUpdated()
        verify(preferencesHelper, never()).cinemasUpdated()
        verify(countDownLatch, times(1)).countDown()
        verify(cinemaCache, never()).updateCinemas(remoteCinemas)
    }

    @Test
    fun getCinemasWhenCinemasAreNullSuccesful() {
        val cinemas = cinemaRepository.getCinemas()
        cinemas.observeForever(observer as Observer<Resource<List<Cinema>>>)

        dbCinemaLiveData.postValue(null)
        remoteCinemaLiveData.postValue(Response.successful(remoteCinemas, callback))
        dbCinemaLiveData.postValue(remoteCinemasMapped)

        verify(observer).onChanged(Resource.loading(null))
        verify(observer).onChanged(Resource.success(remoteCinemasMapped))
        verify(callback).onDataUpdated()

        verify(preferencesHelper).cinemasUpdated()
        verify(countDownLatch).countDown()
        verify(cinemaCache).updateCinemas(remoteCinemas)
    }

    @Test
    fun getCinemasWhenCinemasAreNotCachedSuccesful() {
        val cinemas = cinemaRepository.getCinemas()
        cinemas.observeForever(observer as Observer<Resource<List<Cinema>>>)

        dbCinemaLiveData.postValue(listOf())
        remoteCinemaLiveData.postValue(Response.successful(remoteCinemas, callback))
        dbCinemaLiveData.postValue(remoteCinemasMapped)

        verify(observer).onChanged(Resource.loading(listOf()))
        verify(observer).onChanged(Resource.success(remoteCinemasMapped))
        verify(callback).onDataUpdated()

        verify(preferencesHelper).cinemasUpdated()
        verify(countDownLatch).countDown()
        verify(cinemaCache).updateCinemas(remoteCinemas)
    }

    @Test
    fun getCinemasWhenCinemasAreNotCachedSucessfulWithNullResponseBody() {
        val cinemas = cinemaRepository.getCinemas()
        cinemas.observeForever(observer as Observer<Resource<List<Cinema>>>)
        dbCinemaLiveData.postValue(listOf())

        remoteCinemaLiveData.postValue(Response.successful(null, callback))

        verify(observer).onChanged(Resource.loading(listOf()))
        verify(observer).onChanged(Resource.error(null, listOf()))
        verify(callback, never()).onDataUpdated()

        verify(preferencesHelper, never()).cinemasUpdated()
        verify(countDownLatch).countDown()
        verify(cinemaCache, never()).updateCinemas(Mockito.anyList())
    }

    @Test
    fun getCinemasWhenCinemasAreNotCachedError() {
        val cinemas = cinemaRepository.getCinemas()
        cinemas.observeForever(observer as Observer<Resource<List<Cinema>>>)
        dbCinemaLiveData.postValue(listOf())

        remoteCinemaLiveData.postValue(Response.error("error msg", callback))

        verify(observer).onChanged(Resource.loading(listOf()))
        verify(observer).onChanged(Resource.error("error msg", listOf()))
        verify(callback, never()).onDataUpdated()

        verify(preferencesHelper, never()).cinemasUpdated()
        verify(countDownLatch).countDown()
        verify(cinemaCache, never()).updateCinemas(remoteCinemas)
    }



    @Test
    fun getCinemaReturnsCinemaLiveDataWithSuccessIfExists() {
        `when`(cinemaCache.getCinema(cinema.id)).thenReturn(cinemaLiveData)
        cinemaLiveData.postValue(cinema)

        val res = cinemaRepository.getCinema(cinema.id).getValueBlocking()
        assertEquals(Status.SUCCESS, res?.status)
        assertEquals(cinema, res?.data)
    }

    @Test
    fun getCinemaReturnsCinemaLiveDataWithErrorIfDoesNotExist() {
        `when`(cinemaCache.getCinema(100.toLong())).thenReturn(cinemaLiveData)
        cinemaLiveData.postValue(null)

        val res = cinemaRepository.getCinema(100.toLong()).getValueBlocking()
        assertEquals(Status.ERROR, res?.status)
        assertEquals(null, res?.data)
    }


    @Test
    fun hasExpiredReturnsTrueIfExpired() {
        val currentTime = System.currentTimeMillis()
        `when`(preferencesHelper.cinemaslastUpdateTime)
            .thenReturn(currentTime - (CinemaRepository.EXPIRATION_TIME + 500))
        TestCase.assertEquals(true, cinemaRepository.hasExpired())
    }

    @Test
    fun isExpiredReturnsFalseIfNotExpired() {
        val currentTime = System.currentTimeMillis()
        `when`(preferencesHelper.cinemaslastUpdateTime).thenReturn(currentTime - 5000)
        TestCase.assertEquals(false, cinemaRepository.hasExpired())
    }

    @Test
    fun companionObjectTest() {
        TestCase.assertEquals((3 * 60 * 60 * 1000).toLong(), CinemaRepository.EXPIRATION_TIME)
    }
}