@file:Suppress("DEPRECATION")

package xyz.arnau.muvicat.remote.service

import android.arch.core.executor.testing.InstantTaskExecutorRule
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import xyz.arnau.muvicat.remote.model.GencatMovie
import xyz.arnau.muvicat.remote.model.GencatMovieResponse
import xyz.arnau.muvicat.remote.model.ResponseStatus.*
import xyz.arnau.muvicat.remote.util.LiveDataCallAdapterFactory
import xyz.arnau.muvicat.utils.getValueBlocking
import java.net.HttpURLConnection.*


@Suppress("DEPRECATION")
@RunWith(JUnit4::class)
class GencatServiceTest {
    private lateinit var mockServer: MockWebServer
    private lateinit var gencatService: GencatService
    private val eTag: String = "\"8d569d-16589-56a479e141df0\""

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

    private val body = GencatMovieResponse(listOf(
            GencatMovie(
                    id = 31347,
                    priority = 100,
                    title = "Carinyo, jo soc tu",
                    year = "2017",
                    posterUrl = "carinyojosoctu.jpg",
                    originalTitle = "L'un dans l'autre",
                    direction = "Bruno Chiche",
                    cast = "Stéphane De Groodt,  Louise Bourgoin,  Aure Atika,  Ginnie Watson",
                    plot = "Dues parelles comparteixen amistat però tot canvia quan la Pénélope i el Pierre es converteixen en amants. Després d’una darrera nit junts, es desperten cadascun en el cos de l’altre.",
                    releaseDate = "13/04/2018",
                    originalLanguage = "francès",
                    ageRating = "A partir de 12 anys",
                    trailerUrl = "sbzncDh0a2s"
            ),
            GencatMovie(
                    id = 31377,
                    priority = 100,
                    title = "Leo da Vinci, missió Mona Lisa",
                    year = "2018",
                    posterUrl = "leodavinci2.jpg",
                    originalTitle = "Leo da Vinci: Missione Monna Lisa",
                    direction = "Sergio Manfio",
                    cast = "--",
                    plot = "El jove Leo da Vinci té moltes idees al cap. Quan s’enamora de la Mona Lisa haurà d’aguditzar encara més el seu enginy. Junts viuran aventures emocionants i cercaran un tresor pirata.",
                    releaseDate = "13/04/2018",
                    originalLanguage = "anglès",
                    ageRating = "Apta per a tots els públics",
                    trailerUrl = "FQGe3jZEFBk"
            )
    ))

    private val xml = """
        <dataroot>
            <FILM>
                <IDFILM>31347</IDFILM>
                <PRIORITAT>100</PRIORITAT>
                <TITOL>Carinyo, jo soc tu</TITOL>
                <SITUACIO>--</SITUACIO>
                <ANY>2017</ANY>
                <CARTELL>carinyojosoctu.jpg</CARTELL>
                <ORIGINAL>L'un dans l'autre</ORIGINAL>
                <DIRECCIO>Bruno Chiche</DIRECCIO>
                <INTERPRETS>Stéphane De Groodt,  Louise Bourgoin,  Aure Atika,  Ginnie Watson</INTERPRETS>
                <SINOPSI>Dues parelles comparteixen amistat però tot canvia quan la Pénélope i el Pierre es converteixen en amants. Després d’una darrera nit junts, es desperten cadascun en el cos de l’altre.</SINOPSI>
                <VERSIO>Doblada i VOSC</VERSIO>
                <IDIOMA_x0020_ORIGINAL>francès</IDIOMA_x0020_ORIGINAL>
                <QUALIFICACIO>A partir de 12 anys</QUALIFICACIO>
                <TRAILER>sbzncDh0a2s</TRAILER>
                <WEB>--</WEB>
                <ESTRENA>13/04/2018</ESTRENA>
            </FILM>
            <FILM>
                <IDFILM>31377</IDFILM>
                <PRIORITAT>100</PRIORITAT>
                <TITOL>Leo da Vinci, missió Mona Lisa</TITOL>
                <SITUACIO>--</SITUACIO>
                <ANY>2018</ANY>
                <CARTELL>leodavinci2.jpg</CARTELL>
                <ORIGINAL>Leo da Vinci: Missione Monna Lisa</ORIGINAL>
                <DIRECCIO>Sergio Manfio</DIRECCIO>
                <INTERPRETS>--</INTERPRETS>
                <SINOPSI>El jove Leo da Vinci té moltes idees al cap. Quan s’enamora de la Mona Lisa haurà d’aguditzar encara més el seu enginy. Junts viuran aventures emocionants i cercaran un tresor pirata.</SINOPSI>
                <VERSIO>Doblada</VERSIO>
                <IDIOMA_x0020_ORIGINAL>anglès</IDIOMA_x0020_ORIGINAL>
                <QUALIFICACIO>Apta per a tots els públics</QUALIFICACIO>
                <TRAILER>FQGe3jZEFBk</TRAILER>
                <WEB>--</WEB>
                <ESTRENA>13/04/2018</ESTRENA>
            </FILM>
        </dataroot>
    """.trimIndent()
}