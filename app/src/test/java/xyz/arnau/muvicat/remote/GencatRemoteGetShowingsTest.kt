@file:Suppress("DEPRECATION")

package xyz.arnau.muvicat.remote

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
import xyz.arnau.muvicat.data.repository.GencatRemote
import xyz.arnau.muvicat.remote.mapper.*
import xyz.arnau.muvicat.remote.model.ResponseStatus.*
import xyz.arnau.muvicat.remote.service.GencatService
import xyz.arnau.muvicat.remote.test.GencatRemoteSampleShowingData.body
import xyz.arnau.muvicat.remote.test.GencatRemoteSampleShowingData.eTag
import xyz.arnau.muvicat.remote.test.GencatRemoteSampleShowingData.xml
import xyz.arnau.muvicat.remote.util.LiveDataCallAdapterFactory
import xyz.arnau.muvicat.utils.getValueBlocking
import java.net.HttpURLConnection.*

@RunWith(JUnit4::class)
class GencatServiceGetShowingsTest {
    private lateinit var mockServer: MockWebServer
    private lateinit var gencatService: GencatService

    private lateinit var moviesEntityMapper: GencatMovieListEntityMapper
    private lateinit var cinemasEntityMapper: GencatCinemaListEntityMapper
    private lateinit var showingsEntityMapper: GencatShowingListEntityMapper
    private lateinit var gencatRemote: GencatRemote

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        mockServer = MockWebServer()
        mockServer.start()
        gencatService = Retrofit.Builder()
            .baseUrl(mockServer.url("/").toString())
            //.baseUrl("http://www.gencat.cat/llengua/cinema/")
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .addConverterFactory(SimpleXmlConverterFactory.create())
            .build()
            .create(GencatService::class.java)

        moviesEntityMapper = GencatMovieListEntityMapper(GencatMovieEntityMapper())
        cinemasEntityMapper = GencatCinemaListEntityMapper(GencatCinemaEntityMapper())
        showingsEntityMapper = GencatShowingListEntityMapper(GencatShowingEntityMapper())
        gencatRemote = GencatRemoteImpl(gencatService, moviesEntityMapper, cinemasEntityMapper, showingsEntityMapper)
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
                .addHeader("ETag", eTag)
                .setResponseCode(HTTP_OK)
                .setBody(xml)
        )

        val result = gencatRemote.getShowings(null).getValueBlocking()
        assertEquals(showingsEntityMapper.mapFromRemote(body), result?.body)
        assertEquals(SUCCESSFUL, result?.type)
        assertEquals(null, result?.errorMessage)
        assertEquals(eTag, result?.eTag)
    }

    @Test
    fun testNotModifiedResponse() {
        mockServer.enqueue(
            MockResponse()
                .addHeader("Content-Type", "application/xml")
                .setResponseCode(HTTP_NOT_MODIFIED)
                .setBody(xml)
        )

        val result = gencatRemote.getShowings(eTag).getValueBlocking()
        assertEquals(null, result?.body)
        assertEquals(NOT_MODIFIED, result?.type)
        assertEquals(null, result?.errorMessage)
        assertEquals(null, result?.eTag)
    }

    @Test
    fun testErrorResponse() {
        mockServer.enqueue(
            MockResponse()
                .addHeader("Content-Type", "application/xml")
                .setResponseCode(HTTP_BAD_REQUEST)
                .setBody("ERROR BODY")
        )

        val result = gencatRemote.getShowings(eTag).getValueBlocking()
        assertEquals(null, result?.body)
        assertEquals(ERROR, result?.type)
        assertEquals("ERROR BODY", result?.errorMessage)
        assertEquals(null, result?.eTag)
    }
}