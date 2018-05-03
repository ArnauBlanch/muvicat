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
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import retrofit2.Response
import xyz.arnau.muvicat.remote.mapper.GencatCinemaEntityMapper
import xyz.arnau.muvicat.remote.mapper.GencatCinemaListEntityMapper
import xyz.arnau.muvicat.remote.mapper.GencatMovieEntityMapper
import xyz.arnau.muvicat.remote.mapper.GencatMovieListEntityMapper
import xyz.arnau.muvicat.remote.model.GencatCinemaResponse
import xyz.arnau.muvicat.remote.model.GencatMovieResponse
import xyz.arnau.muvicat.remote.model.ResponseStatus.SUCCESSFUL
import xyz.arnau.muvicat.remote.service.GencatService
import xyz.arnau.muvicat.remote.util.ApiResponse
import xyz.arnau.muvicat.remote.util.GencatRemoteSampleCinemaData
import xyz.arnau.muvicat.remote.util.GencatRemoteSampleMovieData
import xyz.arnau.muvicat.utils.getValueBlocking

@RunWith(JUnit4::class)
class GencatRemoteImplTest {
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private val gencatService = mock(GencatService::class.java)
    private val moviesEntityMapper = GencatMovieListEntityMapper(GencatMovieEntityMapper())
    private val cinemasEntityMapper = GencatCinemaListEntityMapper(GencatCinemaEntityMapper())
    private val gencatRemote =
        GencatRemoteImpl(gencatService, moviesEntityMapper, cinemasEntityMapper)

    @Test
    fun getMoviesReturnsMovieList() {
        val eTag = "movies-etag1"
        val liveData = MutableLiveData<ApiResponse<GencatMovieResponse>>()
        `when`(gencatService.getMovies(eTag)).thenReturn(liveData)
        val response = Response.success(
            GencatRemoteSampleMovieData.body,
            Headers.of(mapOf("ETag" to "movies-etag2"))
        )
        liveData.postValue(ApiResponse<GencatMovieResponse>(response))

        val result = gencatRemote.getMovies(eTag).getValueBlocking()
        assertEquals(
            moviesEntityMapper.mapFromRemote(GencatRemoteSampleMovieData.body),
            result?.body
        )
        assertEquals(SUCCESSFUL, result?.type)
        assertEquals(null, result?.errorMessage)
        assertEquals("movies-etag2", result?.eTag)
    }

    @Test
    fun getMoviesReturnsMovieListWithNullIds() {
        val eTag = "movies-etag1"
        val liveData = MutableLiveData<ApiResponse<GencatMovieResponse>>()
        `when`(gencatService.getMovies(eTag)).thenReturn(liveData)
        val movieResponseWithNullIds = GencatRemoteSampleMovieData.bodyWithNullId

        val response =
            Response.success(movieResponseWithNullIds, Headers.of(mapOf("ETag" to "movies-etag2")))
        liveData.postValue(ApiResponse<GencatMovieResponse>(response))

        val result = gencatRemote.getMovies(eTag).getValueBlocking()
        assertEquals(moviesEntityMapper.mapFromRemote(movieResponseWithNullIds), result?.body)
        assertEquals(SUCCESSFUL, result?.type)
        assertEquals(null, result?.errorMessage)
        assertEquals("movies-etag2", result?.eTag)
        assertEquals(1, result?.body!!.size)
    }

    @Test
    fun getCinemasReturnsCinemaList() {
        val eTag = "cinemas-etag1"
        val liveData = MutableLiveData<ApiResponse<GencatCinemaResponse>>()
        `when`(gencatService.getCinemas(eTag)).thenReturn(liveData)
        val response = Response.success(
            GencatRemoteSampleCinemaData.body,
            Headers.of(mapOf("ETag" to "cinemas-etag2"))
        )
        liveData.postValue(ApiResponse<GencatCinemaResponse>(response))

        val result = gencatRemote.getCinemas(eTag).getValueBlocking()
        assertEquals(
            cinemasEntityMapper.mapFromRemote(GencatRemoteSampleCinemaData.body),
            result?.body
        )
        assertEquals(SUCCESSFUL, result?.type)
        assertEquals(null, result?.errorMessage)
        assertEquals("cinemas-etag2", result?.eTag)
    }

    @Test
    fun getCinemasReturnsCinemaListWithNullIds() {
        val eTag = "cinemas-etag1"
        val liveData = MutableLiveData<ApiResponse<GencatCinemaResponse>>()
        `when`(gencatService.getCinemas(eTag)).thenReturn(liveData)
        val cinemaResponseWithNullIds = GencatRemoteSampleCinemaData.bodyWithNullId
        val response =
            Response.success(
                cinemaResponseWithNullIds,
                Headers.of(mapOf("ETag" to "cinemas-etag2"))
            )
        liveData.postValue(ApiResponse<GencatCinemaResponse>(response))

        val result = gencatRemote.getCinemas(eTag).getValueBlocking()
        assertEquals(cinemasEntityMapper.mapFromRemote(cinemaResponseWithNullIds), result?.body)
        assertEquals(SUCCESSFUL, result?.type)
        assertEquals(null, result?.errorMessage)
        assertEquals("cinemas-etag2", result?.eTag)
        assertEquals(2, result?.body!!.size)
    }
}