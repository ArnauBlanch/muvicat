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
import xyz.arnau.muvicat.remote.util.GencatRemoteSampleMovieData.body
import xyz.arnau.muvicat.remote.util.GencatRemoteSampleMovieData.eTag
import xyz.arnau.muvicat.remote.util.GencatRemoteSampleMovieData.xml
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
    fun testSuccesfulResponse() {
        mockServer.enqueue(
                MockResponse()
                        .addHeader("Content-Type", "application/xml")
                        .addHeader("ETag", "\"new_etag\"")
                        .setResponseCode(HTTP_OK)
                        .setBody(xml)
        )
        val response = gencatService.getMovies(eTag).getValueBlocking()

        val request = mockServer.takeRequest()
        //var body2 = body.copy()
        //body2.moviesList!![0].cast = "--" // [fix] test in Jacoco fails but not in Android Studio
        assertEquals("/provacin.xml", request.path)
        assertEquals(eTag, request.getHeader("If-None-Match"))

        assertEquals(HTTP_OK, response?.code)
        assertEquals(SUCCESSFUL, response?.status)
        assertEquals("\"new_etag\"", response?.eTag)
        assertEquals(null, response?.errorMessage)
        assertEquals(body, response?.body)
    }

    @Test
    fun testNotModifiedResponse() {
        mockServer.enqueue(
                MockResponse()
                        .addHeader("Content-Type", "application/xml")
                        .setResponseCode(HTTP_NOT_MODIFIED)
                        .setBody(xml)
        )
        val response = gencatService.getMovies(eTag).getValueBlocking()

        val request = mockServer.takeRequest()
        assertEquals("/provacin.xml", request.path)
        assertEquals(eTag, request.getHeader("If-None-Match"))

        assertEquals(HTTP_NOT_MODIFIED, response?.code)
        assertEquals(NOT_MODIFIED, response?.status)
        assertEquals(null, response?.eTag)
        assertEquals(null, response?.errorMessage)
        assertEquals(null, response?.body)
    }

    @Test
    fun testErrorResponse() {
        mockServer.enqueue(
                MockResponse()
                        .addHeader("Content-Type", "application/xml")
                        .setResponseCode(HTTP_BAD_REQUEST)
                        .setBody("ERROR BODY")
        )
        val response = gencatService.getMovies(eTag).getValueBlocking()

        val request = mockServer.takeRequest()
        assertEquals("/provacin.xml", request.path)
        assertEquals(eTag, request.getHeader("If-None-Match"))

        assertEquals(HTTP_BAD_REQUEST, response?.code)
        assertEquals(ERROR, response?.status)
        assertEquals(null, response?.eTag)
        assertEquals("ERROR BODY", response?.errorMessage)
        assertEquals(null, response?.body)
    }
}