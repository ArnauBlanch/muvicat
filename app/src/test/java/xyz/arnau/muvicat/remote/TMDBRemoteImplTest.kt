package xyz.arnau.muvicat.remote

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.MutableLiveData
import com.google.android.gms.common.api.Api
import junit.framework.TestCase.assertEquals
import okhttp3.Headers
import okhttp3.ResponseBody
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.*
import retrofit2.Response
import xyz.arnau.muvicat.remote.mapper.*
import xyz.arnau.muvicat.remote.model.ResponseStatus
import xyz.arnau.muvicat.remote.model.ResponseStatus.ERROR
import xyz.arnau.muvicat.remote.model.gencat.GencatCinemaResponse
import xyz.arnau.muvicat.remote.model.gencat.GencatMovieResponse
import xyz.arnau.muvicat.remote.model.gencat.GencatShowingResponse
import xyz.arnau.muvicat.remote.model.ResponseStatus.SUCCESSFUL
import xyz.arnau.muvicat.remote.model.tmdb.TMDBMovie
import xyz.arnau.muvicat.remote.model.tmdb.TMDBSearchMovieResponse
import xyz.arnau.muvicat.remote.model.tmdb.TMDBSearchedMovie
import xyz.arnau.muvicat.remote.service.GencatService
import xyz.arnau.muvicat.remote.service.TMDBService
import xyz.arnau.muvicat.remote.test.*
import xyz.arnau.muvicat.remote.utils.ApiResponse
import xyz.arnau.muvicat.remote.utils.RemotePreferencesHelper
import xyz.arnau.muvicat.utils.getValueBlocking

@RunWith(JUnit4::class)
class TMDBRemoteImplTest {
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private val tmdbService = mock(TMDBService::class.java)
    private val tmdbMovieInfoMapper = TMDBMovieInfoMapper()
    private val tmdbRemote = TMDBRemoteImpl(tmdbService, tmdbMovieInfoMapper)

    private val searchMovieLiveData = MutableLiveData<ApiResponse<TMDBSearchMovieResponse>>()
    private val getMovieLiveData = MutableLiveData<ApiResponse<TMDBMovie>>()

    @Before
    fun setUp() {
        `when`(tmdbService.searchMovie("movie title")).thenReturn(searchMovieLiveData)
        `when`(tmdbService.getMovie(TMDBSampleSearchMovieResponse.body.results[0].id)).thenReturn(getMovieLiveData)
    }

    @Test
    fun getMovieReturnsMovieExtraInfo() {
        searchMovieLiveData.postValue(ApiResponse<TMDBSearchMovieResponse>(Response.success(TMDBSampleSearchMovieResponse.body)))
        getMovieLiveData.postValue(ApiResponse<TMDBMovie>(Response.success(TMDBSampleMovieResponse.body)))

        val result = tmdbRemote.getMovie("movie title").getValueBlocking()
        assertEquals(
            tmdbMovieInfoMapper.mapFromRemote(Pair(TMDBSampleSearchMovieResponse.body.results[0], TMDBSampleMovieResponse.body)),
            result?.body
        )
        assertEquals(SUCCESSFUL, result?.type)
        assertEquals(null, result?.errorMessage)
    }

    @Suppress("UNCHECKED_CAST")
    @Test
    fun getMovieReturnsErrorIfNullSearchResponse() {
        val apiResponse = mock(ApiResponse::class.java)
        `when`(apiResponse.body).thenReturn(null)
        searchMovieLiveData.postValue(apiResponse as ApiResponse<TMDBSearchMovieResponse>?)

        val result = tmdbRemote.getMovie("movie title").getValueBlocking()
        assertEquals(null, result?.body)
        assertEquals(ERROR, result?.type)
        assertEquals(null, result?.errorMessage)
        verify(tmdbService, never()).getMovie(ArgumentMatchers.anyInt(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString())
    }

    @Test
    fun getMovieReturnsErrorIfEmptySearchList() {
        searchMovieLiveData.postValue(ApiResponse<TMDBSearchMovieResponse>(Response.success(TMDBSampleSearchMovieResponse.emptyBody)))

        val result = tmdbRemote.getMovie("movie title").getValueBlocking()
        assertEquals(null, result?.body)
        assertEquals(ERROR, result?.type)
        assertEquals(null, result?.errorMessage)
        verify(tmdbService, never()).getMovie(ArgumentMatchers.anyInt(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString())
    }

    @Test
    fun getMovieReturnsErrorIfErrorOnSearchMovieRequest() {
        searchMovieLiveData.postValue(ApiResponse(Response.error(404, ResponseBody.create(null, "error body"))))

        val result = tmdbRemote.getMovie("movie title").getValueBlocking()
        assertEquals(null, result?.body)
        assertEquals(ERROR, result?.type)
        assertEquals("error body", result?.errorMessage)
        verify(tmdbService, never()).getMovie(ArgumentMatchers.anyInt(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString())
    }

    @Test
    fun getMovieReturnsErrorIfErrorOnGetMovieRequest() {
        searchMovieLiveData.postValue(ApiResponse<TMDBSearchMovieResponse>(Response.success(TMDBSampleSearchMovieResponse.body)))
        getMovieLiveData.postValue(ApiResponse(Response.error(404, ResponseBody.create(null, "error body"))))

        val result = tmdbRemote.getMovie("movie title").getValueBlocking()
        assertEquals(null, result?.body)
        assertEquals(ERROR, result?.type)
        assertEquals("error body", result?.errorMessage)
    }

    @Suppress("UNCHECKED_CAST")
    @Test
    fun getMovieReturnsErrorIfNullGetMovieResponseBody() {
        searchMovieLiveData.postValue(ApiResponse<TMDBSearchMovieResponse>(Response.success(TMDBSampleSearchMovieResponse.body)))
        val apiResponse = mock(ApiResponse::class.java)
        `when`(apiResponse.body).thenReturn(null)
        getMovieLiveData.postValue(apiResponse as ApiResponse<TMDBMovie>?)

        val result = tmdbRemote.getMovie("movie title").getValueBlocking()
        assertEquals(null, result?.body)
        assertEquals(ERROR, result?.type)
        assertEquals(null, result?.errorMessage)
    }
}