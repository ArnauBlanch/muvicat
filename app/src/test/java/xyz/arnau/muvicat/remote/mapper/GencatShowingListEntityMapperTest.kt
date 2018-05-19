package xyz.arnau.muvicat.remote.mapper

import org.junit.Assert.assertEquals
import org.junit.Test
import xyz.arnau.muvicat.remote.model.gencat.GencatShowingResponse
import xyz.arnau.muvicat.remote.test.GencatShowingFactory

class GencatShowingListEntityMapperTest {
    private val showingEntityMapper = GencatShowingEntityMapper()

    private val showingListEntityMapper = GencatShowingListEntityMapper(showingEntityMapper)

    @Test
    fun mapFromRemoteMapsData() {
        val showingResponse = GencatShowingFactory.makeGencatShowingResponse(5)
        val showingList =
            showingResponse.showingList?.map { showingEntityMapper.mapFromRemote(it) }

        val mappedResponse = showingListEntityMapper.mapFromRemote(showingResponse)
        assertEquals(showingList, mappedResponse)
    }

    @Test
    fun mapFromRemoteWithNullShowingList() {
        val showingResponse = GencatShowingResponse()

        val mappedResponse = showingListEntityMapper.mapFromRemote(showingResponse)
        assertEquals(null, mappedResponse)
    }

    @Test
    fun mapFromRemoteWithNullResponse() {
        val mappedResponse = showingListEntityMapper.mapFromRemote(null)
        assertEquals(null, mappedResponse)
    }
}