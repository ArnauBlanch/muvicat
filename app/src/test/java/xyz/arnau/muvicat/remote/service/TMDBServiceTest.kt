@file:Suppress("DEPRECATION")

package xyz.arnau.muvicat.remote.service

import android.arch.core.executor.testing.InstantTaskExecutorRule
import junit.framework.Assert.assertEquals
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import xyz.arnau.muvicat.remote.model.ResponseStatus.*
import xyz.arnau.muvicat.remote.model.tmdb.TMDBRateMovieBody
import xyz.arnau.muvicat.remote.model.tmdb.TMDBSearchMovieResponse
import xyz.arnau.muvicat.remote.test.*
import xyz.arnau.muvicat.remote.utils.LiveDataCallAdapterFactory
import xyz.arnau.muvicat.utils.getValueBlocking
import java.net.HttpURLConnection.*


@RunWith(JUnit4::class)
class TMDBServiceTest {
    private lateinit var mockServer: MockWebServer
    private lateinit var tmdbService: TMDBService

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        mockServer = MockWebServer()
        mockServer.start()
        tmdbService = Retrofit.Builder()
            .baseUrl(mockServer.url("/").toString())
            //.baseUrl("https://api.themoviedb.org/3/")
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TMDBService::class.java)
    }

    @After
    @Throws
    fun tearDown() {
        mockServer.shutdown()
    }

    @Test
    fun searchMovieSuccesfulResponse() {
        mockServer.enqueue(
            MockResponse()
                .addHeader("Content-Type", "application/json")
                .setResponseCode(HTTP_OK)
                .setBody(TMDBSampleSearchMovieResponse.json)
        )
        val response = tmdbService.searchMovie("Estiu 1993", "apiKey").getValueBlocking()

        val request = mockServer.takeRequest()
        assertEquals("/search/movie?query=Estiu%201993&api_key=apiKey", request.path)

        assertEquals(HTTP_OK, response?.code)
        assertEquals(SUCCESSFUL, response?.status)
        assertEquals(null, response?.errorMessage)
        assertEquals(TMDBSampleSearchMovieResponse.body, response?.body)
    }

    @Test
    fun searchMovieErrorResponse() {
        mockServer.enqueue(
            MockResponse()
                .addHeader("Content-Type", "application/json")
                .setResponseCode(HTTP_BAD_REQUEST)
        )
        val response = tmdbService.searchMovie("Estiu 1993", "apiKey").getValueBlocking()

        val request = mockServer.takeRequest()
        assertEquals("/search/movie?query=Estiu%201993&api_key=apiKey", request.path)

        assertEquals(HTTP_BAD_REQUEST, response?.code)
        assertEquals(ERROR, response?.status)
        assertEquals("Client Error", response?.errorMessage)
        assertEquals(null, response?.body)
    }

    @Test
    fun getMovieSuccesfulResponse() {
        mockServer.enqueue(
            MockResponse()
                .addHeader("Content-Type", "application/json")
                .setResponseCode(HTTP_OK)
                .setBody(TMDBSampleMovieResponse.json)
        )
        val response = tmdbService.getMovie(438634, "apiKey").getValueBlocking()

        val request = mockServer.takeRequest()
        assertEquals("/movie/438634?api_key=apiKey&append_to_response=credits", request.path)

        assertEquals(HTTP_OK, response?.code)
        assertEquals(SUCCESSFUL, response?.status)
        assertEquals(null, response?.errorMessage)
        assertEquals(TMDBSampleMovieResponse.body, response?.body)
    }

    @Test
    fun getMovieErrorResponse() {
        mockServer.enqueue(
            MockResponse()
                .addHeader("Content-Type", "application/json")
                .setResponseCode(HTTP_BAD_REQUEST)
        )
        val response = tmdbService.getMovie(438634, "apiKey").getValueBlocking()

        val request = mockServer.takeRequest()
        assertEquals("/movie/438634?api_key=apiKey&append_to_response=credits", request.path)

        assertEquals(HTTP_BAD_REQUEST, response?.code)
        assertEquals(ERROR, response?.status)
        assertEquals("Client Error", response?.errorMessage)
        assertEquals(null, response?.body)
    }

    @Test
    fun createGuestSessionSucessfulResponse() {
        mockServer.enqueue(
            MockResponse()
                .setResponseCode(HTTP_OK)
                .setBody(TMDBSampleGuestSessionResponse.json)
        )

        val response = tmdbService.createGuestSession("apiKey").getValueBlocking()
        val request = mockServer.takeRequest()
        assertEquals("/authentication/guest_session/new?api_key=apiKey", request.path)

        assertEquals(HTTP_OK, response?.code)
        assertEquals(SUCCESSFUL, response?.status)
        assertEquals(null, response?.errorMessage)
        assertEquals(TMDBSampleGuestSessionResponse.body, response?.body)
    }

    @Test
    fun createGuestSessionErrorResponse() {
        mockServer.enqueue(
            MockResponse()
                .setResponseCode(401)
        )

        val response = tmdbService.createGuestSession("apiKey").getValueBlocking()
        val request = mockServer.takeRequest()
        assertEquals("/authentication/guest_session/new?api_key=apiKey", request.path)

        assertEquals(401, response?.code)
        assertEquals(ERROR, response?.status)
        assertEquals("Client Error", response?.errorMessage)
        assertEquals(null, response?.body)
    }

    @Test
    fun rateMovieSucessfulResponse() {
        mockServer.enqueue(
            MockResponse()
                .setResponseCode(HTTP_CREATED)
                .setBody(TMDBSampleStatusResponse.successJson)
        )

        val requestBody = TMDBRateMovieBody(5.0)
        val response =
            tmdbService.rateMovie(100, requestBody, "guestSessionId", "apiKey").getValueBlocking()
        val request = mockServer.takeRequest()
        assertEquals("/movie/100/rating?guest_session_id=guestSessionId&api_key=apiKey", request.path)
        assertEquals("[text={\"value\":5.0}]", request.body.toString())
        assertEquals("POST", request.method)

        assertEquals(HTTP_CREATED, response?.code)
        assertEquals(SUCCESSFUL, response?.status)
        assertEquals(null, response?.errorMessage)
        assertEquals(TMDBSampleStatusResponse.successBody, response?.body)
    }

    @Test
    fun rateMovieErrorResponse() {
        mockServer.enqueue(
            MockResponse()
                .setResponseCode(404)
                .setBody(TMDBSampleStatusResponse.errorJson)
        )

        val requestBody = TMDBRateMovieBody(5.0)
        val response =
            tmdbService.rateMovie(100, requestBody, "guestSessionId", "apiKey").getValueBlocking()
        val request = mockServer.takeRequest()
        assertEquals("/movie/100/rating?guest_session_id=guestSessionId&api_key=apiKey", request.path)
        assertEquals("[text={\"value\":5.0}]", request.body.toString())
        assertEquals("POST", request.method)

        assertEquals(404, response?.code)
        assertEquals(ERROR, response?.status)
        assertEquals(TMDBSampleStatusResponse.errorJson, response?.errorMessage)
        assertEquals(null, response?.body)
    }
}