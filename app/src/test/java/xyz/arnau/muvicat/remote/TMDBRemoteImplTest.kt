package xyz.arnau.muvicat.remote

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.MutableLiveData
import junit.framework.Assert
import junit.framework.TestCase.assertEquals
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
import xyz.arnau.muvicat.remote.mapper.TMDBMovieInfoMapper
import xyz.arnau.muvicat.remote.model.ResponseStatus.ERROR
import xyz.arnau.muvicat.remote.model.ResponseStatus.SUCCESSFUL
import xyz.arnau.muvicat.remote.model.tmdb.*
import xyz.arnau.muvicat.remote.service.TMDBService
import xyz.arnau.muvicat.remote.test.TMDBSampleGuestSessionResponse
import xyz.arnau.muvicat.remote.test.TMDBSampleMovieResponse
import xyz.arnau.muvicat.remote.test.TMDBSampleSearchMovieResponse
import xyz.arnau.muvicat.remote.test.TMDBSampleStatusResponse
import xyz.arnau.muvicat.remote.utils.ApiResponse
import xyz.arnau.muvicat.remote.utils.RemotePreferencesHelper
import xyz.arnau.muvicat.utils.getValueBlocking

@RunWith(JUnit4::class)
class TMDBRemoteImplTest {
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private val tmdbService = mock(TMDBService::class.java)
    private val tmdbMovieInfoMapper = TMDBMovieInfoMapper()
    private val preferencesHelper = mock(RemotePreferencesHelper::class.java)
    private val tmdbRemote = TMDBRemoteImpl(tmdbService, tmdbMovieInfoMapper, preferencesHelper)

    private val searchMovieLiveData = MutableLiveData<ApiResponse<TMDBSearchMovieResponse>>()
    private val getMovieLiveData = MutableLiveData<ApiResponse<TMDBMovie>>()
    private val createGuestSessionLiveData = MutableLiveData<ApiResponse<TMDBGuestSessionResponse>>()
    private val rateMovieLiveData = MutableLiveData<ApiResponse<TMDBStatusResponse>>()

    @Before
    fun setUp() {
        `when`(tmdbService.searchMovie("movie title")).thenReturn(searchMovieLiveData)
        `when`(tmdbService.getMovie(TMDBSampleSearchMovieResponse.body.results[0].id)).thenReturn(getMovieLiveData)
        `when`(tmdbService.getMovie(123, append = "")).thenReturn(getMovieLiveData)
        `when`(tmdbService.createGuestSession()).thenReturn(createGuestSessionLiveData)
        `when`(tmdbService.rateMovie(1, TMDBRateMovieBody(1.1), "GUEST_SESSION_ID")).thenReturn(rateMovieLiveData)
    }

    @Test
    fun getMovieByTitleReturnsMovieExtraInfo() {
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
    fun getMovieByTitleReturnsErrorIfNullSearchResponse() {
        val apiResponse = ApiResponse<TMDBSearchMovieResponse>(200, null, null, SUCCESSFUL)
        searchMovieLiveData.postValue(apiResponse)

        val result = tmdbRemote.getMovie("movie title").getValueBlocking()
        assertEquals(null, result?.body)
        assertEquals(ERROR, result?.type)
        assertEquals(null, result?.errorMessage)
        verify(tmdbService, never()).getMovie(ArgumentMatchers.anyInt(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString())
    }

    @Test
    fun getMovieByTitleReturnsErrorIfEmptySearchList() {
        searchMovieLiveData.postValue(ApiResponse<TMDBSearchMovieResponse>(Response.success(TMDBSampleSearchMovieResponse.emptyBody)))

        val result = tmdbRemote.getMovie("movie title").getValueBlocking()
        assertEquals(null, result?.body)
        assertEquals(ERROR, result?.type)
        assertEquals(null, result?.errorMessage)
        verify(tmdbService, never()).getMovie(ArgumentMatchers.anyInt(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString())
    }

    @Test
    fun getMovieByTitleReturnsErrorIfErrorOnSearchMovieRequest() {
        searchMovieLiveData.postValue(ApiResponse(Response.error(404, ResponseBody.create(null, "error body"))))

        val result = tmdbRemote.getMovie("movie title").getValueBlocking()
        assertEquals(null, result?.body)
        assertEquals(ERROR, result?.type)
        assertEquals("error body", result?.errorMessage)
        verify(tmdbService, never()).getMovie(ArgumentMatchers.anyInt(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString())
    }

    @Test
    fun getMovieByTitleReturnsErrorIfErrorOnGetMovieRequest() {
        searchMovieLiveData.postValue(ApiResponse<TMDBSearchMovieResponse>(Response.success(TMDBSampleSearchMovieResponse.body)))
        getMovieLiveData.postValue(ApiResponse(Response.error(404, ResponseBody.create(null, "error body"))))

        val result = tmdbRemote.getMovie("movie title").getValueBlocking()
        assertEquals(null, result?.body)
        assertEquals(ERROR, result?.type)
        assertEquals("error body", result?.errorMessage)
    }

    @Suppress("UNCHECKED_CAST")
    @Test
    fun getMovieByTitleReturnsErrorIfNullGetMovieResponseBody() {
        searchMovieLiveData.postValue(ApiResponse<TMDBSearchMovieResponse>(Response.success(TMDBSampleSearchMovieResponse.body)))
        val apiResponse = ApiResponse<TMDBMovie>(200, null, null, SUCCESSFUL)
        getMovieLiveData.postValue(apiResponse)

        val result = tmdbRemote.getMovie("movie title").getValueBlocking()
        assertEquals(null, result?.body)
        assertEquals(ERROR, result?.type)
        assertEquals(null, result?.errorMessage)
    }

    //////////////////

    @Test
    fun getMovieByTmdbIdReturnsMovieExtraInfo() {
        searchMovieLiveData.postValue(ApiResponse<TMDBSearchMovieResponse>(Response.success(TMDBSampleSearchMovieResponse.body)))
        getMovieLiveData.postValue(ApiResponse<TMDBMovie>(Response.success(TMDBSampleMovieResponse.body)))

        val result = tmdbRemote.getMovie(123).getValueBlocking()
        assertEquals(
            tmdbMovieInfoMapper.mapFromRemote(Pair(null, TMDBSampleMovieResponse.body)),
            result?.body
        )
        assertEquals(SUCCESSFUL, result?.type)
        assertEquals(null, result?.errorMessage)
    }

    @Test
    fun getMovieByTmdbIdReturnsErrorIfErrorResponse() {
        getMovieLiveData.postValue(ApiResponse(400, null, "error body", ERROR))

        val result = tmdbRemote.getMovie(123).getValueBlocking()
        assertEquals(
            null,
            result?.body
        )
        assertEquals(ERROR, result?.type)
        assertEquals("error body", result?.errorMessage)
    }

    @Test
    fun getMovieByTmdbIdReturnsErrorIfNullResponseBody() {
        getMovieLiveData.postValue(ApiResponse(200, null, null, SUCCESSFUL))

        val result = tmdbRemote.getMovie(123).getValueBlocking()
        assertEquals(
            null,
            result?.body
        )
        assertEquals(ERROR, result?.type)
        assertEquals(null, result?.errorMessage)
    }

    //////////////

    @Test
    fun rateMoviesReturnsTrueIfSuccess() {
        `when`(preferencesHelper.tmdbGuestSessionId).thenReturn("GUEST_SESSION_ID")
        rateMovieLiveData.postValue(ApiResponse(Response.success(TMDBSampleStatusResponse.successBody)))

        val result = tmdbRemote.rateMovie(1, 1.1).getValueBlocking()

        verify(tmdbService).rateMovie(1, TMDBRateMovieBody(1.1), "GUEST_SESSION_ID")
        assertEquals(true, result!!.body)
        assertEquals(SUCCESSFUL, result.type)
        assertEquals(null, result.errorMessage)
    }

    @Test
    fun rateMoviesReturnsErrorIfErrorResponse() {
        `when`(preferencesHelper.tmdbGuestSessionId).thenReturn("GUEST_SESSION_ID")
        rateMovieLiveData.postValue(ApiResponse(400, null, "error", ERROR))

        val result = tmdbRemote.rateMovie(1, 1.1).getValueBlocking()

        verify(tmdbService).rateMovie(1, TMDBRateMovieBody(1.1), "GUEST_SESSION_ID")
        assertEquals(null, result!!.body)
        assertEquals(ERROR, result.type)
        assertEquals("error", result.errorMessage)
    }

    @Test
    fun rateMoviesReturnsErrorIfCantGetAGuestSession() {
        `when`(preferencesHelper.tmdbGuestSessionId).thenReturn(null)
        val apiResponse = ApiResponse<TMDBGuestSessionResponse>(Response.error(404, ResponseBody.create(null, TMDBSampleStatusResponse.errorJson)))
        createGuestSessionLiveData.postValue(apiResponse)

        val result = tmdbRemote.rateMovie(1, 1.1).getValueBlocking()

        assertEquals(null, result!!.body)
        assertEquals(ERROR, result.type)
        assertEquals(TMDBSampleStatusResponse.errorJson, result.errorMessage)
    }

    //////////////

    @Test
    fun getGuestSessionAlreadyCreated() {
        `when`(preferencesHelper.tmdbGuestSessionId).thenReturn("PREF_GUEST_SESSION_ID")

        val result = tmdbRemote.getGuestSession().getValueBlocking()
        assertEquals("PREF_GUEST_SESSION_ID", result!!.body)
        assertEquals(SUCCESSFUL, result.type)
    }

    @Test
    fun getGuestSessionSuccesfulServerResponse() {
        `when`(preferencesHelper.tmdbGuestSessionId).thenReturn(null)
        val apiResponse = ApiResponse<TMDBGuestSessionResponse>(Response.success(TMDBSampleGuestSessionResponse.body))
        createGuestSessionLiveData.postValue(apiResponse)

        val result = tmdbRemote.getGuestSession().getValueBlocking()
        assertEquals(TMDBSampleGuestSessionResponse.body.guest_session_id, result!!.body)
        assertEquals(SUCCESSFUL, result.type)
        verify(preferencesHelper).tmdbGuestSessionId = TMDBSampleGuestSessionResponse.body.guest_session_id
    }

    @Suppress("UNCHECKED_CAST")
    @Test
    fun getGuestSessionSuccesfulServerResponseButNullBody() {
        `when`(preferencesHelper.tmdbGuestSessionId).thenReturn(null)
        val apiResponse = ApiResponse(200, null, null, SUCCESSFUL)
        createGuestSessionLiveData.postValue(apiResponse as ApiResponse<TMDBGuestSessionResponse>)

        val result = tmdbRemote.getGuestSession().getValueBlocking()
        assertEquals(null, result!!.body)
        assertEquals(ERROR, result.type)
        assertEquals(null, result.errorMessage)
    }

    @Test
    fun getGuestSessionSuccesfulServerResponseButSuccessFalse() {
        `when`(preferencesHelper.tmdbGuestSessionId).thenReturn(null)
        val apiResponse = ApiResponse<TMDBGuestSessionResponse>(Response.success(TMDBSampleGuestSessionResponse.bodyNoSuccess))
        createGuestSessionLiveData.postValue(apiResponse)

        val result = tmdbRemote.getGuestSession().getValueBlocking()
        assertEquals(null, result!!.body)
        assertEquals(ERROR, result.type)
        assertEquals(null, result.errorMessage)
    }

    @Test
    fun getGuestSessionSuccesfulServerResponseWithNullId() {
        `when`(preferencesHelper.tmdbGuestSessionId).thenReturn(null)
        val apiResponse = ApiResponse<TMDBGuestSessionResponse>(Response.success(TMDBSampleGuestSessionResponse.bodySuccessWithNullId))
        createGuestSessionLiveData.postValue(apiResponse)

        val result = tmdbRemote.getGuestSession().getValueBlocking()
        assertEquals(null, result!!.body)
        assertEquals(ERROR, result.type)
        assertEquals(null, result.errorMessage)
    }

    @Test
    fun getGuestSessionErrorServerResponse() {
        `when`(preferencesHelper.tmdbGuestSessionId).thenReturn(null)
        val apiResponse = ApiResponse<TMDBGuestSessionResponse>(Response.error(404, ResponseBody.create(null, TMDBSampleStatusResponse.errorJson)))
        createGuestSessionLiveData.postValue(apiResponse)

        val result = tmdbRemote.getGuestSession().getValueBlocking()
        assertEquals(null, result!!.body)
        assertEquals(ERROR, result.type)
        assertEquals(TMDBSampleStatusResponse.errorJson, result.errorMessage)
    }
}