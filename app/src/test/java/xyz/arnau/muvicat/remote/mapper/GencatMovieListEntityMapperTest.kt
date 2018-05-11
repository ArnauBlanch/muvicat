package xyz.arnau.muvicat.remote.mapper

import org.junit.Assert.assertEquals
import org.junit.Test
import xyz.arnau.muvicat.remote.model.GencatMovieResponse
import xyz.arnau.muvicat.remote.test.GencatMovieFactory

class GencatMovieListEntityMapperTest {
    private val movieEntityMapper = GencatMovieEntityMapper()

    private val movieListEntityMapper = GencatMovieListEntityMapper(movieEntityMapper)

    @Test
    fun mapFromRemoteMapsData() {
        val movieResponse = GencatMovieFactory.makeGencatMovieResponse(5)
        val movieList =
            movieResponse.moviesList?.map { movieEntityMapper.mapFromRemote(it) }

        val mappedResponse = movieListEntityMapper.mapFromRemote(movieResponse)
        assertEquals(movieList, mappedResponse)
    }

    @Test
    fun mapFromRemoteMapsDataWithNullMovies() {
        val movieResponse = GencatMovieFactory.makeGencatMovieResponse(5)
        val movieList =
            movieResponse.moviesList?.map { movieEntityMapper.mapFromRemote(it) }

        val mappedResponse = movieListEntityMapper.mapFromRemote(movieResponse)
        assertEquals(movieList, mappedResponse)
    }

    @Test
    fun mapFromRemoteWithNullMovieList() {
        val movieResponse = GencatMovieResponse()

        val mappedResponse = movieListEntityMapper.mapFromRemote(movieResponse)
        assertEquals(null, mappedResponse)
    }

    @Test
    fun mapFromRemoteWithNullResponse() {
        val mappedResponse = movieListEntityMapper.mapFromRemote(null)
        assertEquals(null, mappedResponse)
    }
}