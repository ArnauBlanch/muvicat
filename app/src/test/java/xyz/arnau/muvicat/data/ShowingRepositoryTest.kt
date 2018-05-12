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
import xyz.arnau.muvicat.cache.model.ShowingEntity
import xyz.arnau.muvicat.data.ShowingRepository.Companion.EXPIRATION_TIME
import xyz.arnau.muvicat.data.model.Showing
import xyz.arnau.muvicat.data.model.Resource
import xyz.arnau.muvicat.data.model.Status
import xyz.arnau.muvicat.data.repository.GencatRemote
import xyz.arnau.muvicat.data.repository.ShowingCache
import xyz.arnau.muvicat.data.test.ShowingEntityFactory
import xyz.arnau.muvicat.data.test.ShowingMapper
import xyz.arnau.muvicat.data.utils.RepoPreferencesHelper
import xyz.arnau.muvicat.remote.DataUpdateCallback
import xyz.arnau.muvicat.remote.model.Response
import xyz.arnau.muvicat.utils.InstantAppExecutors
import xyz.arnau.muvicat.utils.getValueBlocking
import java.util.concurrent.CountDownLatch

@RunWith(JUnit4::class)
class ShowingRepositoryTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val showingCache = mock(ShowingCache::class.java)
    private val gencatRemote = mock(GencatRemote::class.java)
    private val appExecutors = InstantAppExecutors()
    private val preferencesHelper = mock(RepoPreferencesHelper::class.java)
    private val countDownLatch = mock(CountDownLatch::class.java)
    private val showingRepository = ShowingRepository(showingCache, gencatRemote, appExecutors, preferencesHelper, countDownLatch)

    private val dbShowingLiveData = MutableLiveData<List<Showing>>()
    private val dbShowings = ShowingMapper.mapFromShowingEntityList(ShowingEntityFactory.makeShowingEntityList(3))

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

        countDownLatch.countDown()
        countDownLatch.countDown()

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

        countDownLatch.countDown()
        countDownLatch.countDown()

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

        countDownLatch.countDown()
        countDownLatch.countDown()

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

        countDownLatch.countDown()
        countDownLatch.countDown()

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

        countDownLatch.countDown()
        countDownLatch.countDown()

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