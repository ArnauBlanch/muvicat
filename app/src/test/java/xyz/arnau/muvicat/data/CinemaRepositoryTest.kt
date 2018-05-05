package xyz.arnau.muvicat.data

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.mockito.Mockito.*
import xyz.arnau.muvicat.AppExecutors
import xyz.arnau.muvicat.data.model.Cinema
import xyz.arnau.muvicat.data.model.CinemaInfo
import xyz.arnau.muvicat.data.model.Resource
import xyz.arnau.muvicat.data.model.Status
import xyz.arnau.muvicat.data.repository.CinemaCache
import xyz.arnau.muvicat.data.repository.GencatRemote
import xyz.arnau.muvicat.data.test.CinemaFactory
import xyz.arnau.muvicat.data.test.CinemaInfoFactory
import xyz.arnau.muvicat.data.test.CinemaInfoMapper
import xyz.arnau.muvicat.data.utils.PreferencesHelper
import xyz.arnau.muvicat.remote.model.Response
import xyz.arnau.muvicat.remote.model.ResponseStatus
import xyz.arnau.muvicat.utils.InstantAppExecutors
import xyz.arnau.muvicat.utils.getValueBlocking

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
        val dbCinemaLiveData = MutableLiveData<List<CinemaInfo>>()
        `when`(cinemaCache.getCinemas()).thenReturn(dbCinemaLiveData)
        val dbCinemas = CinemaInfoFactory.makeCinemaInfoList(3)
        dbCinemaLiveData.postValue(dbCinemas)
        `when`(cinemaCache.isExpired()).thenReturn(false)

        val cinemas = cinemaRepository.getCinemas()
        val observer: Observer<*>? = mock(Observer::class.java)
        @Suppress("UNCHECKED_CAST")
        cinemas.observeForever(observer as Observer<Resource<List<CinemaInfo>>>)
        verify(observer).onChanged(Resource.success(dbCinemas))
        verify(preferencesHelper, never()).cinemasUpdated()
        verify(gencatRemote, never()).getCinemas(ArgumentMatchers.anyString())
    }

    @Test
    fun getCinemasWhenCinemasAreCachedButExpired() {
        val dbCinemaLiveData = MutableLiveData<List<CinemaInfo>>()
        `when`(cinemaCache.getCinemas()).thenReturn(dbCinemaLiveData)
        val dbCinemas = CinemaInfoFactory.makeCinemaInfoList(3)
        `when`(cinemaCache.isExpired()).thenReturn(true)
        `when`(preferencesHelper.cinemasETag).thenReturn("cinema-etag")
        val remoteCinemaLiveData = MutableLiveData<Response<List<Cinema>>>()
        val remoteCinemas = CinemaFactory.makeCinemaList(3)
        `when`(gencatRemote.getCinemas("cinema-etag"))
            .thenReturn(remoteCinemaLiveData)


        val cinemas = cinemaRepository.getCinemas()

        val observer: Observer<*>? = mock(Observer::class.java)
        @Suppress("UNCHECKED_CAST")
        cinemas.observeForever(observer as Observer<Resource<List<CinemaInfo>>>)
        dbCinemaLiveData.postValue(dbCinemas)
        verify(observer).onChanged(Resource.loading(dbCinemas))
        remoteCinemaLiveData.postValue(
            Response(
                remoteCinemas, null,
                ResponseStatus.SUCCESSFUL, "cinema-etag2"
            )
        )
        val dbCinemas2 = CinemaInfoMapper.mapFromCinemaList(remoteCinemas)
        dbCinemaLiveData.postValue(dbCinemas2)
        verify(observer).onChanged(Resource.success(dbCinemas2))
        verify(preferencesHelper).cinemasETag = "cinema-etag2"
        verify(preferencesHelper).cinemasUpdated()
        verify(cinemaCache).updateCinemas(remoteCinemas)
    }

    @Test
    fun getCinemasWhenCinemasAreCachedButExpiredNotModified() {
        val dbCinemaLiveData = MutableLiveData<List<CinemaInfo>>()
        `when`(cinemaCache.getCinemas()).thenReturn(dbCinemaLiveData)
        val dbCinemas = CinemaInfoFactory.makeCinemaInfoList(3)
        `when`(cinemaCache.isExpired()).thenReturn(true)
        `when`(preferencesHelper.cinemasETag).thenReturn("cinema-etag")
        val remoteCinemaLiveData = MutableLiveData<Response<List<Cinema>>>()
        `when`(gencatRemote.getCinemas("cinema-etag"))
            .thenReturn(remoteCinemaLiveData)


        val cinemas = cinemaRepository.getCinemas()

        val observer: Observer<*>? = mock(Observer::class.java)
        @Suppress("UNCHECKED_CAST")
        cinemas.observeForever(observer as Observer<Resource<List<CinemaInfo>>>)
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
        val dbCinemaLiveData = MutableLiveData<List<CinemaInfo>>()
        `when`(cinemaCache.getCinemas()).thenReturn(dbCinemaLiveData)
        val dbCinemas = listOf<CinemaInfo>()
        `when`(preferencesHelper.cinemasETag).thenReturn("cinema-etag")
        val remoteCinemaLiveData = MutableLiveData<Response<List<Cinema>>>()
        val remoteCinemas = CinemaFactory.makeCinemaList(3)
        `when`(gencatRemote.getCinemas("cinema-etag"))
            .thenReturn(remoteCinemaLiveData)


        val cinemas = cinemaRepository.getCinemas()

        val observer: Observer<*>? = mock(Observer::class.java)
        @Suppress("UNCHECKED_CAST")
        cinemas.observeForever(observer as Observer<Resource<List<CinemaInfo>>>)
        dbCinemaLiveData.postValue(dbCinemas)
        verify(observer).onChanged(Resource.loading(dbCinemas))
        remoteCinemaLiveData.postValue(
            Response(
                remoteCinemas, null,
                ResponseStatus.SUCCESSFUL, "cinema-etag2"
            )
        )
        val mappedCinemas = CinemaInfoMapper.mapFromCinemaList(remoteCinemas)
        dbCinemaLiveData.postValue(mappedCinemas)
        verify(observer).onChanged(Resource.success(mappedCinemas))
        verify(preferencesHelper).cinemasETag = "cinema-etag2"
        verify(preferencesHelper).cinemasUpdated()
        verify(cinemaCache).updateCinemas(remoteCinemas)
    }

    @Test
    fun getCinemasWhenCinemasAreNotCachedWithNullResponseBody() {
        val dbCinemaLiveData = MutableLiveData<List<CinemaInfo>>()
        `when`(cinemaCache.getCinemas()).thenReturn(dbCinemaLiveData)
        val dbCinemas = listOf<CinemaInfo>()
        `when`(preferencesHelper.cinemasETag).thenReturn("cinema-etag")
        val remoteCinemaLiveData = MutableLiveData<Response<List<Cinema>>>()
        `when`(gencatRemote.getCinemas("cinema-etag"))
            .thenReturn(remoteCinemaLiveData)


        val cinemas = cinemaRepository.getCinemas()

        val observer: Observer<*>? = mock(Observer::class.java)
        @Suppress("UNCHECKED_CAST")
        cinemas.observeForever(observer as Observer<Resource<List<CinemaInfo>>>)
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

    @Test
    fun getCinemaReturnsCinemaLiveDataWithSuccessIfExists() {
        val cinema = CinemaInfoFactory.makeCinemaInfo()
        val cinemaLiveData = MutableLiveData<CinemaInfo>()
        `when`(cinemaCache.getCinema(cinema.id)).thenReturn(cinemaLiveData)
        cinemaLiveData.postValue(cinema)

        val res = cinemaRepository.getCinema(cinema.id).getValueBlocking()
        assertEquals(Status.SUCCESS, res?.status)
        assertEquals(cinema, res?.data)
    }

    @Test
    fun getCinemaReturnsCinemaLiveDataWithErrorIfDoesNotExist() {
        val cinemaLiveData = MutableLiveData<CinemaInfo>()
        `when`(cinemaCache.getCinema(100.toLong())).thenReturn(cinemaLiveData)
        cinemaLiveData.postValue(null)

        val res = cinemaRepository.getCinema(100.toLong()).getValueBlocking()
        assertEquals(Status.ERROR, res?.status)
        assertEquals(null, res?.data)
    }
}