package xyz.arnau.muvicat.remote

import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import xyz.arnau.muvicat.data.model.Movie
import xyz.arnau.muvicat.remote.mapper.GencatMovieEntityMapper
import xyz.arnau.muvicat.remote.service.GencatService
import xyz.arnau.muvicat.remote.test.MovieFactory

@RunWith(JUnit4::class)
class GencatRemoteImplTest {
    private lateinit var entityMapper: GencatMovieEntityMapper
    private lateinit var gencatService: GencatService
    private lateinit var gencatRemoteImpl: GencatRemoteImpl

    @Before
    fun setUp() {
        entityMapper = GencatMovieEntityMapper()
        gencatService = mock(GencatService::class.java)
        gencatRemoteImpl = GencatRemoteImpl(gencatService, entityMapper)
    }

    @Test
    fun getMoviesCompletes() {
        val response = MovieFactory.makeGencatMovieResponse()
        `when`(gencatService.getMovies()).thenReturn(Single.just(response))

        val testObserver = gencatRemoteImpl.getMovies().test()
        testObserver.assertComplete()
    }

    @Test
    fun getMoviesReturnsData() {
        val response = MovieFactory.makeGencatMovieResponse()
        `when`(gencatService.getMovies()).thenReturn(Single.just(response))
        val movieEntities = mutableListOf<Movie>()
        response.moviesList?.forEach {
            movieEntities.add(entityMapper.mapFromRemote(it))
        }

        val testObserver = gencatRemoteImpl.getMovies().test()
        testObserver.assertValue(movieEntities)
    }
}