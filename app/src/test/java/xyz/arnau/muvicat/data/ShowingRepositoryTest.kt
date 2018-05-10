package xyz.arnau.muvicat.data

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import junit.framework.TestCase
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import xyz.arnau.muvicat.AppExecutors
import xyz.arnau.muvicat.cache.model.ShowingEntity
import xyz.arnau.muvicat.data.model.Resource
import xyz.arnau.muvicat.data.model.Showing
import xyz.arnau.muvicat.data.repository.GencatRemote
import xyz.arnau.muvicat.data.repository.ShowingCache
import xyz.arnau.muvicat.data.test.ShowingEntityFactory
import xyz.arnau.muvicat.data.test.ShowingMapper
import xyz.arnau.muvicat.data.utils.RepoPreferencesHelper
import xyz.arnau.muvicat.remote.model.Response
import xyz.arnau.muvicat.remote.model.ResponseStatus
import xyz.arnau.muvicat.utils.InstantAppExecutors

@RunWith(JUnit4::class)
class ShowingRepositoryTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val showingCache: ShowingCache = mock(ShowingCache::class.java)
    private val gencatRemote: GencatRemote = mock(GencatRemote::class.java)
    private val appExecutors: AppExecutors = InstantAppExecutors()
    private val preferencesHelper: RepoPreferencesHelper = mock(RepoPreferencesHelper::class.java)

    private val showingRepository =
        ShowingRepository(showingCache, gencatRemote, appExecutors, preferencesHelper)

    @Test
    fun getShowingsWhenShowingsAreCachedAndNotExpired() {
        val dbShowingLiveData = MutableLiveData<List<Showing>>()
        `when`(showingCache.getShowings()).thenReturn(dbShowingLiveData)
        val dbShowings = ShowingMapper.mapFromShowingEntityList(ShowingEntityFactory.makeShowingEntityList(3))
        dbShowingLiveData.postValue(dbShowings)
        val currentTime = System.currentTimeMillis()
        `when`(preferencesHelper.showingslastUpdateTime).thenReturn(currentTime - 5000)

        val showings = showingRepository.getShowings()
        val observer: Observer<*>? = mock(Observer::class.java)
        @Suppress("UNCHECKED_CAST")
        showings.observeForever(observer as Observer<Resource<List<Showing>>>)
        Mockito.verify(observer).onChanged(Resource.success(dbShowings))
        Mockito.verify(preferencesHelper, Mockito.never()).showingsUpdated()
        Mockito.verify(gencatRemote, Mockito.never()).getShowings()
    }

    @Test
    fun getShowingsWhenShowingsAreCachedButExpired() {
        val dbShowingLiveData = MutableLiveData<List<Showing>>()
        `when`(showingCache.getShowings()).thenReturn(dbShowingLiveData)
        val dbShowings = ShowingMapper.mapFromShowingEntityList(ShowingEntityFactory.makeShowingEntityList(3))

        val currentTime = System.currentTimeMillis()
        `when`(preferencesHelper.showingslastUpdateTime)
            .thenReturn(currentTime - (ShowingRepository.EXPIRATION_TIME + 500))

        val remoteShowingLiveData = MutableLiveData<Response<List<ShowingEntity>>>()
        val remoteShowings = ShowingEntityFactory.makeShowingEntityList(3)
        `when`(gencatRemote.getShowings()).thenReturn(remoteShowingLiveData)


        val showings = showingRepository.getShowings()

        val observer: Observer<*>? = mock(Observer::class.java)
        @Suppress("UNCHECKED_CAST")
        showings.observeForever(observer as Observer<Resource<List<Showing>>>)
        dbShowingLiveData.postValue(dbShowings)
        Mockito.verify(observer).onChanged(Resource.loading(dbShowings))
        remoteShowingLiveData.postValue(
            Response(remoteShowings, null, ResponseStatus.SUCCESSFUL)
        )
        dbShowingLiveData.postValue(ShowingMapper.mapFromShowingEntityList(remoteShowings))
        Mockito.verify(observer).onChanged(Resource.success(ShowingMapper.mapFromShowingEntityList(remoteShowings)))
        Mockito.verify(preferencesHelper).showingsUpdated()
        Mockito.verify(showingCache).updateShowings(remoteShowings)
    }

    @Test
    fun getShowingsWhenShowingsAreCachedButExpiredNotModified() {
        val dbShowingLiveData = MutableLiveData<List<Showing>>()
        `when`(showingCache.getShowings()).thenReturn(dbShowingLiveData)
        val dbShowings = ShowingMapper.mapFromShowingEntityList(ShowingEntityFactory.makeShowingEntityList(3))

        val currentTime = System.currentTimeMillis()
        `when`(preferencesHelper.showingslastUpdateTime)
            .thenReturn(currentTime - (ShowingRepository.EXPIRATION_TIME + 500))

        val remoteShowingLiveData = MutableLiveData<Response<List<ShowingEntity>>>()
        `when`(gencatRemote.getShowings()).thenReturn(remoteShowingLiveData)


        val showings = showingRepository.getShowings()

        val observer: Observer<*>? = mock(Observer::class.java)
        @Suppress("UNCHECKED_CAST")
        showings.observeForever(observer as Observer<Resource<List<Showing>>>)
        dbShowingLiveData.postValue(dbShowings)
        Mockito.verify(observer).onChanged(Resource.loading(dbShowings))
        remoteShowingLiveData.postValue(
            Response(null, null, ResponseStatus.NOT_MODIFIED)
        )
        Mockito.verify(observer).onChanged(Resource.success(dbShowings))
        Mockito.verify(preferencesHelper).showingsUpdated()
        Mockito.verify(showingCache, Mockito.never()).updateShowings(Mockito.anyList())
    }

    @Test
    fun getShowingsWhenShowingsAreNotCached() {
        val dbShowingLiveData = MutableLiveData<List<Showing>>()
        `when`(showingCache.getShowings()).thenReturn(dbShowingLiveData)
        val dbShowings = listOf<Showing>()
        val remoteShowingLiveData = MutableLiveData<Response<List<ShowingEntity>>>()
        val remoteShowings = ShowingEntityFactory.makeShowingEntityList(3)
        `when`(gencatRemote.getShowings()).thenReturn(remoteShowingLiveData)


        val showings = showingRepository.getShowings()

        val observer: Observer<*>? = mock(Observer::class.java)
        @Suppress("UNCHECKED_CAST")
        showings.observeForever(observer as Observer<Resource<List<Showing>>>)
        dbShowingLiveData.postValue(dbShowings)
        Mockito.verify(observer).onChanged(Resource.loading(dbShowings))
        remoteShowingLiveData.postValue(
            Response(remoteShowings, null, ResponseStatus.SUCCESSFUL)
        )
        dbShowingLiveData.postValue(ShowingMapper.mapFromShowingEntityList(remoteShowings))
        Mockito.verify(observer).onChanged(Resource.success(ShowingMapper.mapFromShowingEntityList(remoteShowings)))
        Mockito.verify(preferencesHelper).showingsUpdated()
        Mockito.verify(showingCache).updateShowings(remoteShowings)
    }

    @Test
    fun getShowingsWhenShowingsAreNotCachedWithNullResponseBody() {
        val dbShowingLiveData = MutableLiveData<List<Showing>>()
        `when`(showingCache.getShowings()).thenReturn(dbShowingLiveData)
        val dbShowings = listOf<Showing>()
        val remoteShowingLiveData = MutableLiveData<Response<List<ShowingEntity>>>()
        `when`(gencatRemote.getShowings()).thenReturn(remoteShowingLiveData)


        val showings = showingRepository.getShowings()

        val observer: Observer<*>? = mock(Observer::class.java)
        @Suppress("UNCHECKED_CAST")
        showings.observeForever(observer as Observer<Resource<List<Showing>>>)
        Mockito.verify(observer).onChanged(Resource.loading(null))
        remoteShowingLiveData.postValue(
            Response(null, null, ResponseStatus.SUCCESSFUL)
        )
        dbShowingLiveData.postValue(dbShowings)
        Mockito.verify(observer).onChanged(Resource.success(dbShowings))
        Mockito.verify(preferencesHelper, Mockito.never()).showingsUpdated()
        Mockito.verify(showingCache, Mockito.never()).updateShowings(Mockito.anyList())
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