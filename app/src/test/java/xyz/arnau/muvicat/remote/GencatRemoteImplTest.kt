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
import xyz.arnau.muvicat.data.PreferencesHelper
import xyz.arnau.muvicat.remote.mapper.GencatMovieEntityMapper
import xyz.arnau.muvicat.remote.mapper.GencatMovieListEntityMapper
import xyz.arnau.muvicat.remote.model.GencatMovie
import xyz.arnau.muvicat.remote.model.GencatMovieResponse
import xyz.arnau.muvicat.remote.model.ResponseStatus.SUCCESSFUL
import xyz.arnau.muvicat.remote.service.GencatService
import xyz.arnau.muvicat.remote.util.ApiResponse
import xyz.arnau.muvicat.utils.getValueBlocking

@RunWith(JUnit4::class)
class GencatRemoteImplTest {
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private val gencatService = mock(GencatService::class.java)
    private val entityMapper = GencatMovieListEntityMapper(GencatMovieEntityMapper())
    private val gencatRemote = GencatRemoteImpl(gencatService, entityMapper)

    @Test
    fun getMoviesReturnsMovieList() {
        val eTag = "movies-etag1"
        val liveData = MutableLiveData<ApiResponse<GencatMovieResponse>>()
        `when`(gencatService.getMovies(eTag)).thenReturn(liveData)
        val response = Response.success(movieResponse, Headers.of(mapOf("ETag" to "movies-etag2")))
        liveData.postValue(ApiResponse<GencatMovieResponse>(response))

        val result = gencatRemote.getMovies(eTag).getValueBlocking()
        assertEquals(entityMapper.mapFromRemote(movieResponse), result?.body)
        assertEquals(SUCCESSFUL, result?.type)
        assertEquals(null, result?.errorMessage)
        assertEquals("movies-etag2", result?.eTag)
    }


    private val movieResponse = GencatMovieResponse(listOf(
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
}