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
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import xyz.arnau.muvicat.remote.model.ResponseStatus.*
import xyz.arnau.muvicat.remote.test.GencatRemoteSampleCinemaData
import xyz.arnau.muvicat.remote.test.GencatRemoteSampleMovieData
import xyz.arnau.muvicat.remote.test.GencatRemoteSampleShowingData
import xyz.arnau.muvicat.remote.util.LiveDataCallAdapterFactory
import xyz.arnau.muvicat.utils.getValueBlocking
import java.net.HttpURLConnection.*


@Suppress("DEPRECATION")
@RunWith(JUnit4::class)
class GencatServiceTest {
    private lateinit var mockServer: MockWebServer
    private lateinit var gencatService: GencatService

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        mockServer = MockWebServer()
        mockServer.start()
        gencatService = Retrofit.Builder()
            .baseUrl(mockServer.url("/").toString())
            //.baseUrl("http://www.gencat.cat/llengua/cinema/aa/")
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .addConverterFactory(SimpleXmlConverterFactory.create())
            .build()
            .create(GencatService::class.java)
    }

    @After
    @Throws
    fun tearDown() {
        mockServer.shutdown()
    }

    @Test
    fun getMoviesSuccesfulResponse() {
        mockServer.enqueue(
            MockResponse()
                .addHeader("Content-Type", "application/xml")
                .addHeader("ETag", "\"new_etag\"")
                .setResponseCode(HTTP_OK)
                .setBody(GencatRemoteSampleMovieData.xml)
        )
        val response = gencatService.getMovies(GencatRemoteSampleMovieData.eTag).getValueBlocking()

        val request = mockServer.takeRequest()
        assertEquals("/provacin.xml", request.path)
        assertEquals(GencatRemoteSampleMovieData.eTag, request.getHeader("If-None-Match"))

        assertEquals(HTTP_OK, response?.code)
        assertEquals(SUCCESSFUL, response?.status)
        assertEquals("\"new_etag\"", response?.eTag)
        assertEquals(null, response?.errorMessage)
        assertEquals(GencatRemoteSampleMovieData.body, response?.body)
    }

    @Test
    fun getMoviesNotModifiedResponse() {
        mockServer.enqueue(
            MockResponse()
                .addHeader("Content-Type", "application/xml")
                .setResponseCode(HTTP_NOT_MODIFIED)
                .setBody(GencatRemoteSampleMovieData.xml)
        )
        val response = gencatService.getMovies(GencatRemoteSampleMovieData.eTag).getValueBlocking()

        val request = mockServer.takeRequest()
        assertEquals("/provacin.xml", request.path)
        assertEquals(GencatRemoteSampleMovieData.eTag, request.getHeader("If-None-Match"))

        assertEquals(HTTP_NOT_MODIFIED, response?.code)
        assertEquals(NOT_MODIFIED, response?.status)
        assertEquals(null, response?.eTag)
        assertEquals(null, response?.errorMessage)
        assertEquals(null, response?.body)
    }

    @Test
    fun getMoviesErrorResponse() {
        mockServer.enqueue(
            MockResponse()
                .addHeader("Content-Type", "application/xml")
                .setResponseCode(HTTP_BAD_REQUEST)
                .setBody("ERROR BODY")
        )
        val response = gencatService.getMovies(GencatRemoteSampleMovieData.eTag).getValueBlocking()

        val request = mockServer.takeRequest()
        assertEquals("/provacin.xml", request.path)
        assertEquals(GencatRemoteSampleMovieData.eTag, request.getHeader("If-None-Match"))

        assertEquals(HTTP_BAD_REQUEST, response?.code)
        assertEquals(ERROR, response?.status)
        assertEquals(null, response?.eTag)
        assertEquals("ERROR BODY", response?.errorMessage)
        assertEquals(null, response?.body)
    }


    @Test
    fun getCinemasSuccesfulResponse() {
        mockServer.enqueue(
            MockResponse()
                .addHeader("Content-Type", "application/xml")
                .addHeader("ETag", "\"new_etag\"")
                .setResponseCode(HTTP_OK)
                .setBody(GencatRemoteSampleCinemaData.xml)
        )
        val response =
            gencatService.getCinemas(GencatRemoteSampleCinemaData.eTag).getValueBlocking()

        val request = mockServer.takeRequest()
        assertEquals("/cinemes.xml", request.path)
        assertEquals(GencatRemoteSampleCinemaData.eTag, request.getHeader("If-None-Match"))

        assertEquals(HTTP_OK, response?.code)
        assertEquals(SUCCESSFUL, response?.status)
        assertEquals("\"new_etag\"", response?.eTag)
        assertEquals(null, response?.errorMessage)
        assertEquals(GencatRemoteSampleCinemaData.body, response?.body)
    }

    @Test
    fun getCinemasNotModifiedResponse() {
        mockServer.enqueue(
            MockResponse()
                .addHeader("Content-Type", "application/xml")
                .setResponseCode(HTTP_NOT_MODIFIED)
                .setBody(GencatRemoteSampleCinemaData.xml)
        )
        val response =
            gencatService.getCinemas(GencatRemoteSampleCinemaData.eTag).getValueBlocking()

        val request = mockServer.takeRequest()
        assertEquals("/cinemes.xml", request.path)
        assertEquals(GencatRemoteSampleCinemaData.eTag, request.getHeader("If-None-Match"))

        assertEquals(HTTP_NOT_MODIFIED, response?.code)
        assertEquals(NOT_MODIFIED, response?.status)
        assertEquals(null, response?.eTag)
        assertEquals(null, response?.errorMessage)
        assertEquals(null, response?.body)
    }

    @Test
    fun getCinemasErrorResponse() {
        mockServer.enqueue(
            MockResponse()
                .addHeader("Content-Type", "application/xml")
                .setResponseCode(HTTP_BAD_REQUEST)
                .setBody("ERROR BODY")
        )
        val response =
            gencatService.getCinemas(GencatRemoteSampleCinemaData.eTag).getValueBlocking()

        val request = mockServer.takeRequest()
        assertEquals("/cinemes.xml", request.path)
        assertEquals(GencatRemoteSampleCinemaData.eTag, request.getHeader("If-None-Match"))

        assertEquals(HTTP_BAD_REQUEST, response?.code)
        assertEquals(ERROR, response?.status)
        assertEquals(null, response?.eTag)
        assertEquals("ERROR BODY", response?.errorMessage)
        assertEquals(null, response?.body)
    }

    @Test
    fun getShowingsSuccesfulResponse() {
        mockServer.enqueue(
            MockResponse()
                .addHeader("Content-Type", "application/xml")
                .addHeader("ETag", "\"new_etag\"")
                .setResponseCode(HTTP_OK)
                .setBody(GencatRemoteSampleShowingData.xml)
        )
        val response =
            gencatService.getShowings(GencatRemoteSampleShowingData.eTag).getValueBlocking()

        val request = mockServer.takeRequest()
        assertEquals("/film_sessions.xml", request.path)
        assertEquals(GencatRemoteSampleShowingData.eTag, request.getHeader("If-None-Match"))

        assertEquals(HTTP_OK, response?.code)
        assertEquals(SUCCESSFUL, response?.status)
        assertEquals("\"new_etag\"", response?.eTag)
        assertEquals(null, response?.errorMessage)
        val body = GencatRemoteSampleShowingData.body
        body.showingList!![0].seasonId = 0
        assertEquals(GencatRemoteSampleShowingData.body, response?.body)
    }

    @Test
    fun getShowingsNotModifiedResponse() {
        mockServer.enqueue(
            MockResponse()
                .addHeader("Content-Type", "application/xml")
                .setResponseCode(HTTP_NOT_MODIFIED)
                .setBody(GencatRemoteSampleShowingData.xml)
        )
        val response =
            gencatService.getShowings(GencatRemoteSampleShowingData.eTag).getValueBlocking()

        val request = mockServer.takeRequest()
        assertEquals("/film_sessions.xml", request.path)
        assertEquals(GencatRemoteSampleShowingData.eTag, request.getHeader("If-None-Match"))

        assertEquals(HTTP_NOT_MODIFIED, response?.code)
        assertEquals(NOT_MODIFIED, response?.status)
        assertEquals(null, response?.eTag)
        assertEquals(null, response?.errorMessage)
        assertEquals(null, response?.body)
    }

    @Test
    fun getShowingsErrorResponse() {
        mockServer.enqueue(
            MockResponse()
                .addHeader("Content-Type", "application/xml")
                .setResponseCode(HTTP_BAD_REQUEST)
                .setBody("ERROR BODY")
        )
        val response =
            gencatService.getShowings(GencatRemoteSampleShowingData.eTag).getValueBlocking()

        val request = mockServer.takeRequest()
        assertEquals("/film_sessions.xml", request.path)
        assertEquals(GencatRemoteSampleShowingData.eTag, request.getHeader("If-None-Match"))

        assertEquals(HTTP_BAD_REQUEST, response?.code)
        assertEquals(ERROR, response?.status)
        assertEquals(null, response?.eTag)
        assertEquals("ERROR BODY", response?.errorMessage)
        assertEquals(null, response?.body)
    }
}