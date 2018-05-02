package xyz.arnau.muvicat.remote.mapper

import org.junit.Assert.assertEquals
import org.junit.Test
import xyz.arnau.muvicat.remote.model.GencatCinemaResponse
import xyz.arnau.muvicat.remote.test.CinemaFactory

class GencatCinemaListEntityMapperTest {
    private val cinemaEntityMapper = GencatCinemaEntityMapper()

    private val cinemaListEntityMapper = GencatCinemaListEntityMapper(cinemaEntityMapper)

    @Test
    fun mapFromRemoteMapsData() {
        val cinemaResponse = CinemaFactory.makeGencatCinemaResponse(5)
        val cinemaList =
            cinemaResponse.cinemaList?.map { cinemaEntityMapper.mapFromRemote(it) }

        val mappedResponse = cinemaListEntityMapper.mapFromRemote(cinemaResponse)
        assertEquals(cinemaList, mappedResponse)
    }

    @Test
    fun mapFromRemoteMapsDataWithNullCinemas() {
        val cinemaResponse = CinemaFactory.makeGencatCinemaResponse(5)
        val cinemaList =
            cinemaResponse.cinemaList?.map { cinemaEntityMapper.mapFromRemote(it) }

        val mappedResponse = cinemaListEntityMapper.mapFromRemote(cinemaResponse)
        assertEquals(cinemaList, mappedResponse)
    }

    @Test
    fun mapFromRemoteWithNullMovieList() {
        val cinemaResponse = GencatCinemaResponse()

        val mappedResponse = cinemaListEntityMapper.mapFromRemote(cinemaResponse)
        assertEquals(null, mappedResponse)
    }

    @Test
    fun mapFromRemoteWithNullResponse() {
        val mappedResponse = cinemaListEntityMapper.mapFromRemote(null)
        assertEquals(null, mappedResponse)
    }
}