package xyz.arnau.muvicat.remote

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.MutableLiveData
import junit.framework.TestCase.assertEquals
import okhttp3.Headers
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.*
import retrofit2.Response
import xyz.arnau.muvicat.remote.mapper.*
import xyz.arnau.muvicat.remote.model.GencatCinemaResponse
import xyz.arnau.muvicat.remote.model.GencatMovieResponse
import xyz.arnau.muvicat.remote.model.GencatShowingResponse
import xyz.arnau.muvicat.remote.model.ResponseStatus.SUCCESSFUL
import xyz.arnau.muvicat.remote.service.GencatService
import xyz.arnau.muvicat.remote.utils.ApiResponse
import xyz.arnau.muvicat.remote.test.GencatRemoteSampleCinemaData
import xyz.arnau.muvicat.remote.test.GencatRemoteSampleMovieData
import xyz.arnau.muvicat.remote.test.GencatRemoteSampleShowingData
import xyz.arnau.muvicat.remote.utils.RemotePreferencesHelper
import xyz.arnau.muvicat.utils.getValueBlocking

@RunWith(JUnit4::class)
class GencatRemoteImplTest {
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private val gencatService = mock(GencatService::class.java)
    private val preferencesHelper = mock(RemotePreferencesHelper::class.java)
    private val moviesEntityMapper = GencatMovieListEntityMapper(GencatMovieEntityMapper())
    private val cinemasEntityMapper = GencatCinemaListEntityMapper(GencatCinemaEntityMapper())
    private val showingsEntityMapper = GencatShowingListEntityMapper(GencatShowingEntityMapper())
    private val gencatRemote =
        GencatRemoteImpl(gencatService, preferencesHelper, moviesEntityMapper, cinemasEntityMapper, showingsEntityMapper)

    @Test
    fun getMoviesReturnsMovieList() {
        val eTag = "movies-etag1"
        `when`(preferencesHelper.moviesETag).thenReturn(eTag)
        val liveData = MutableLiveData<ApiResponse<GencatMovieResponse>>()
        `when`(gencatService.getMovies(eTag)).thenReturn(liveData)
        val response = Response.success(
            GencatRemoteSampleMovieData.body,
            Headers.of(mapOf("ETag" to "movies-etag2"))
        )
        liveData.postValue(ApiResponse<GencatMovieResponse>(response))

        val result = gencatRemote.getMovies().getValueBlocking()
        assertEquals(
            moviesEntityMapper.mapFromRemote(GencatRemoteSampleMovieData.body),
            result?.body
        )
        assertEquals(SUCCESSFUL, result?.type)
        assertEquals(null, result?.errorMessage)
        verify(preferencesHelper).moviesETag = "movies-etag2"
    }

    @Test
    fun getMoviesReturnsMovieListWithNullIds() {
        val eTag = "movies-etag1"
        `when`(preferencesHelper.moviesETag).thenReturn(eTag)
        val liveData = MutableLiveData<ApiResponse<GencatMovieResponse>>()
        `when`(gencatService.getMovies(eTag)).thenReturn(liveData)
        val movieResponseWithNullIds = GencatRemoteSampleMovieData.bodyWithNullId

        val response =
            Response.success(movieResponseWithNullIds, Headers.of(mapOf("ETag" to "movies-etag2")))
        liveData.postValue(ApiResponse<GencatMovieResponse>(response))

        val result = gencatRemote.getMovies().getValueBlocking()
        assertEquals(moviesEntityMapper.mapFromRemote(movieResponseWithNullIds), result?.body)
        assertEquals(SUCCESSFUL, result?.type)
        assertEquals(null, result?.errorMessage)
        verify(preferencesHelper).moviesETag = "movies-etag2"
        assertEquals(1, result?.body!!.size)
    }

    @Test
    fun getCinemasReturnsCinemaList() {
        val eTag = "cinemas-etag1"
        `when`(preferencesHelper.cinemasETag).thenReturn(eTag)
        val liveData = MutableLiveData<ApiResponse<GencatCinemaResponse>>()
        `when`(gencatService.getCinemas(eTag)).thenReturn(liveData)
        val response = Response.success(
            GencatRemoteSampleCinemaData.body,
            Headers.of(mapOf("ETag" to "cinemas-etag2"))
        )
        liveData.postValue(ApiResponse<GencatCinemaResponse>(response))

        val result = gencatRemote.getCinemas().getValueBlocking()
        assertEquals(
            cinemasEntityMapper.mapFromRemote(GencatRemoteSampleCinemaData.body),
            result?.body
        )
        assertEquals(SUCCESSFUL, result?.type)
        assertEquals(null, result?.errorMessage)
        verify(preferencesHelper).cinemasETag = "cinemas-etag2"
    }

    @Test
    fun getCinemasReturnsCinemaListWithNullIds() {
        val eTag = "cinemas-etag1"
        `when`(preferencesHelper.cinemasETag).thenReturn(eTag)
        val liveData = MutableLiveData<ApiResponse<GencatCinemaResponse>>()
        `when`(gencatService.getCinemas(eTag)).thenReturn(liveData)
        val cinemaResponseWithNullIds = GencatRemoteSampleCinemaData.bodyWithNullId
        val response =
            Response.success(
                cinemaResponseWithNullIds,
                Headers.of(mapOf("ETag" to "cinemas-etag2"))
            )
        liveData.postValue(ApiResponse<GencatCinemaResponse>(response))

        val result = gencatRemote.getCinemas().getValueBlocking()
        assertEquals(cinemasEntityMapper.mapFromRemote(cinemaResponseWithNullIds), result?.body)
        assertEquals(SUCCESSFUL, result?.type)
        assertEquals(null, result?.errorMessage)
        verify(preferencesHelper).cinemasETag = "cinemas-etag2"
        assertEquals(2, result?.body!!.size)
    }

    @Test
    fun getShowingsReturnsShowingList() {
        val eTag = "showings-etag1"
        `when`(preferencesHelper.showingsETag).thenReturn(eTag)
        val liveData = MutableLiveData<ApiResponse<GencatShowingResponse>>()
        `when`(gencatService.getShowings(eTag)).thenReturn(liveData)
        val response = Response.success(
            GencatRemoteSampleShowingData.body,
            Headers.of(mapOf("ETag" to "showings-etag2"))
        )
        liveData.postValue(ApiResponse<GencatShowingResponse>(response))

        val result = gencatRemote.getShowings().getValueBlocking()
        assertEquals(
            showingsEntityMapper.mapFromRemote(GencatRemoteSampleShowingData.body),
            result?.body
        )
        assertEquals(SUCCESSFUL, result?.type)
        assertEquals(null, result?.errorMessage)
        verify(preferencesHelper).showingsETag = "showings-etag2"
    }

    @Test
    fun getShowingsReturnsShowingListWithNullIds() {
        val eTag = "showings-etag1"
        `when`(preferencesHelper.showingsETag).thenReturn(eTag)
        val liveData = MutableLiveData<ApiResponse<GencatShowingResponse>>()
        `when`(gencatService.getShowings(eTag)).thenReturn(liveData)
        val showingsResponseWithNullIds = GencatRemoteSampleShowingData.bodyWithNullId
        val response =
            Response.success(
                showingsResponseWithNullIds,
                Headers.of(mapOf("ETag" to "showings-etag2"))
            )
        liveData.postValue(ApiResponse<GencatShowingResponse>(response))

        val result = gencatRemote.getShowings().getValueBlocking()
        assertEquals(showingsEntityMapper.mapFromRemote(showingsResponseWithNullIds), result?.body)
        assertEquals(SUCCESSFUL, result?.type)
        assertEquals(null, result?.errorMessage)
        verify(preferencesHelper).showingsETag = "showings-etag2"
        assertEquals(1, result?.body!!.size)
    }
}