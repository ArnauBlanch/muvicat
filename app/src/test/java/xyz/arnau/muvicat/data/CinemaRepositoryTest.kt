package xyz.arnau.muvicat.data

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.mockito.Mockito.*
import xyz.arnau.muvicat.AppExecutors
import xyz.arnau.muvicat.data.model.Cinema
import xyz.arnau.muvicat.data.model.Resource
import xyz.arnau.muvicat.data.repository.CinemaCache
import xyz.arnau.muvicat.data.repository.GencatRemote
import xyz.arnau.muvicat.data.test.CinemaFactory
import xyz.arnau.muvicat.data.utils.PreferencesHelper
import xyz.arnau.muvicat.remote.model.Response
import xyz.arnau.muvicat.remote.model.ResponseStatus
import xyz.arnau.muvicat.utils.InstantAppExecutors

@RunWith(JUnit4::class)
class CinemaRepositoryTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val cinemaCache: CinemaCache = mock(CinemaCache::class.java)
    private val gencatRemote: GencatRemote = mock(GencatRemote::class.java)
    private val appExecutors: AppExecutors = InstantAppExecutors()
    private val preferencesHelper: PreferencesHelper = mock(PreferencesHelper::class.java)

    private val cinemaRepository =
        CinemaRepository(cinemaCache, gencatRemote, appExecutors, preferencesHelper)

    @Test
    fun getCinemasWhenCinemasAreCachedAndNotExpired() {
        val dbCinemaLiveData = MutableLiveData<List<Cinema>>()
        `when`(cinemaCache.getCinemas()).thenReturn(dbCinemaLiveData)
        val dbCinemas = CinemaFactory.makeCinemaList(3)
        dbCinemaLiveData.postValue(dbCinemas)
        `when`(cinemaCache.isExpired()).thenReturn(false)

        val cinemas = cinemaRepository.getCinemas()
        val observer: Observer<*>? = mock(Observer::class.java)
        @Suppress("UNCHECKED_CAST")
        cinemas.observeForever(observer as Observer<Resource<List<Cinema>>>)
        verify(observer).onChanged(Resource.success(dbCinemas))
        verify(preferencesHelper, never()).cinemasUpdated()
        verify(gencatRemote, never()).getCinemas(ArgumentMatchers.anyString())
    }

    @Test
    fun getCinemasWhenCinemasAreCachedButExpired() {
        val dbCinemaLiveData = MutableLiveData<List<Cinema>>()
        `when`(cinemaCache.getCinemas()).thenReturn(dbCinemaLiveData)
        val dbCinemas = CinemaFactory.makeCinemaList(3)
        `when`(cinemaCache.isExpired()).thenReturn(true)
        `when`(preferencesHelper.cinemasETag).thenReturn("cinema-etag")
        val remoteCinemaLiveData = MutableLiveData<Response<List<Cinema>>>()
        val remoteCinemas = CinemaFactory.makeCinemaList(3)
        `when`(gencatRemote.getCinemas("cinema-etag"))
            .thenReturn(remoteCinemaLiveData)


        val cinemas = cinemaRepository.getCinemas()

        val observer: Observer<*>? = mock(Observer::class.java)
        @Suppress("UNCHECKED_CAST")
        cinemas.observeForever(observer as Observer<Resource<List<Cinema>>>)
        dbCinemaLiveData.postValue(dbCinemas)
        verify(observer).onChanged(Resource.loading(dbCinemas))
        remoteCinemaLiveData.postValue(
            Response(
                remoteCinemas, null,
                ResponseStatus.SUCCESSFUL, "cinema-etag2"
            )
        )
        dbCinemaLiveData.postValue(remoteCinemas)
        verify(observer).onChanged(Resource.success(remoteCinemas))
        verify(preferencesHelper).cinemasETag = "cinema-etag2"
        verify(preferencesHelper).cinemasUpdated()
        verify(cinemaCache).updateCinemas(remoteCinemas)
    }

    @Test
    fun getCinemasWhenCinemasAreCachedButExpiredNotModified() {
        val dbCinemaLiveData = MutableLiveData<List<Cinema>>()
        `when`(cinemaCache.getCinemas()).thenReturn(dbCinemaLiveData)
        val dbCinemas = CinemaFactory.makeCinemaList(3)
        `when`(cinemaCache.isExpired()).thenReturn(true)
        `when`(preferencesHelper.cinemasETag).thenReturn("cinema-etag")
        val remoteCinemaLiveData = MutableLiveData<Response<List<Cinema>>>()
        `when`(gencatRemote.getCinemas("cinema-etag"))
            .thenReturn(remoteCinemaLiveData)


        val cinemas = cinemaRepository.getCinemas()

        val observer: Observer<*>? = mock(Observer::class.java)
        @Suppress("UNCHECKED_CAST")
        cinemas.observeForever(observer as Observer<Resource<List<Cinema>>>)
        dbCinemaLiveData.postValue(dbCinemas)
        verify(observer).onChanged(Resource.loading(dbCinemas))
        remoteCinemaLiveData.postValue(
            Response(
                null, null,
                ResponseStatus.NOT_MODIFIED, null
            )
        )
        verify(observer).onChanged(Resource.success(dbCinemas))
        verify(preferencesHelper).cinemasUpdated()
        verify(cinemaCache, never()).updateCinemas(Mockito.anyList())
    }

    @Test
    fun getCinemasWhenCinemasAreNotCached() {
        val dbCinemaLiveData = MutableLiveData<List<Cinema>>()
        `when`(cinemaCache.getCinemas()).thenReturn(dbCinemaLiveData)
        val dbCinemas = listOf<Cinema>()
        `when`(preferencesHelper.cinemasETag).thenReturn("cinema-etag")
        val remoteCinemaLiveData = MutableLiveData<Response<List<Cinema>>>()
        val remoteCinemas = CinemaFactory.makeCinemaList(3)
        `when`(gencatRemote.getCinemas("cinema-etag"))
            .thenReturn(remoteCinemaLiveData)


        val cinemas = cinemaRepository.getCinemas()

        val observer: Observer<*>? = mock(Observer::class.java)
        @Suppress("UNCHECKED_CAST")
        cinemas.observeForever(observer as Observer<Resource<List<Cinema>>>)
        dbCinemaLiveData.postValue(dbCinemas)
        verify(observer).onChanged(Resource.loading(dbCinemas))
        remoteCinemaLiveData.postValue(
            Response(
                remoteCinemas, null,
                ResponseStatus.SUCCESSFUL, "cinema-etag2"
            )
        )
        dbCinemaLiveData.postValue(remoteCinemas)
        verify(observer).onChanged(Resource.success(remoteCinemas))
        verify(preferencesHelper).cinemasETag = "cinema-etag2"
        verify(preferencesHelper).cinemasUpdated()
        verify(cinemaCache).updateCinemas(remoteCinemas)
    }

    @Test
    fun getCinemasWhenCinemasAreNotCachedWithNullResponseBody() {
        val dbCinemaLiveData = MutableLiveData<List<Cinema>>()
        `when`(cinemaCache.getCinemas()).thenReturn(dbCinemaLiveData)
        val dbCinemas = listOf<Cinema>()
        `when`(preferencesHelper.cinemasETag).thenReturn("cinema-etag")
        val remoteCinemaLiveData = MutableLiveData<Response<List<Cinema>>>()
        `when`(gencatRemote.getCinemas("cinema-etag"))
            .thenReturn(remoteCinemaLiveData)


        val cinemas = cinemaRepository.getCinemas()

        val observer: Observer<*>? = mock(Observer::class.java)
        @Suppress("UNCHECKED_CAST")
        cinemas.observeForever(observer as Observer<Resource<List<Cinema>>>)
        verify(observer).onChanged(Resource.loading(null))
        remoteCinemaLiveData.postValue(
            Response(
                null, null,
                ResponseStatus.SUCCESSFUL, "cinema-etag2"
            )
        )
        dbCinemaLiveData.postValue(dbCinemas)
        verify(observer).onChanged(Resource.success(dbCinemas))
        verify(preferencesHelper).cinemasETag = "cinema-etag2"
        verify(preferencesHelper, never()).cinemasUpdated()
        verify(cinemaCache, never()).updateCinemas(Mockito.anyList())
    }
}