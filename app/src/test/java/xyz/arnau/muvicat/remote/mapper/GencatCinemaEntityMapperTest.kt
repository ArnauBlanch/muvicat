package xyz.arnau.muvicat.remote.mapper

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import xyz.arnau.muvicat.data.model.Cinema
import xyz.arnau.muvicat.remote.model.GencatCinema
import xyz.arnau.muvicat.remote.test.CinemaFactory

class GencatCinemaEntityMapperTest {
    private lateinit var cinemaEntityMapper: GencatCinemaEntityMapper

    @Before
    fun setUp() {
        cinemaEntityMapper = GencatCinemaEntityMapper()
    }

    @Test
    fun mapFromRemoteMapsDataWithoutPostalCode() {
        val cinemaModel = CinemaFactory.makeGencatCinemaModelWithoutPostalCode()
        val cinemaEntity = cinemaEntityMapper.mapFromRemote(cinemaModel)

        assertCinemaEquality(cinemaEntity!!, cinemaModel, null)
    }

    @Test
    fun mapFromRemoteMapsDataWithPostalCode() {
        val cinemaModel = CinemaFactory.makeGencatCinemaModelWithPostalCode(12345)
        val cinemaEntity = cinemaEntityMapper.mapFromRemote(cinemaModel)

        assertCinemaEquality(cinemaEntity!!, cinemaModel, 12345)
    }

    @Test
    fun mapFromRemoteMapsDataWithShortPostalCode() {
        val cinemaModel = CinemaFactory.makeGencatCinemaModelWithPostalCode(123)
        val cinemaEntity = cinemaEntityMapper.mapFromRemote(cinemaModel)

        assertCinemaEquality(cinemaEntity!!, cinemaModel, null)
    }

    @Test
    fun mapFromRemoteReturnsNullIfNullId() {
        val cinemaModel = CinemaFactory.makeGencatCinemaModelWithNullId()
        val cinemaEntity = cinemaEntityMapper.mapFromRemote(cinemaModel)

        assertEquals(null, cinemaEntity)
    }

    @Test
    fun mapFromRemoteReturnsNullIfNullName() {
        val cinemaModel = CinemaFactory.makeGencatCinemaModelWithNullName()
        val cinemaEntity = cinemaEntityMapper.mapFromRemote(cinemaModel)

        assertEquals(null, cinemaEntity)
    }

    @Test
    fun mapFromRemoteReturnsNullIfNullAddress() {
        val cinemaModel = CinemaFactory.makeGencatCinemaModelWithNullAddress()
        val cinemaEntity = cinemaEntityMapper.mapFromRemote(cinemaModel)

        assertEquals(null, cinemaEntity)
    }

    @Test
    fun mapFromRemoteReturnsNullIfEmptyName() {
        val cinemaModel = CinemaFactory.makeGencatCinemaModelWithEmptyName()
        val cinemaEntity = cinemaEntityMapper.mapFromRemote(cinemaModel)

        cinemaModel.name = null
        assertEquals(null, cinemaEntity)
    }

    @Test
    fun mapFromRemoteReturnsNullIfEmptyAddress() {
        val cinemaModel = CinemaFactory.makeGencatCinemaModelWithEmptyAddress()
        val cinemaEntity = cinemaEntityMapper.mapFromRemote(cinemaModel)

        cinemaModel.address = null
        assertEquals(null, cinemaEntity)
    }

    @Test
    fun mapFromRemoteReturnsNullIfInvalidName() {
        val cinemaModel = CinemaFactory.makeGencatCinemaModelWithInvalidName()
        val cinemaEntity = cinemaEntityMapper.mapFromRemote(cinemaModel)

        cinemaModel.name = null
        assertEquals(null, cinemaEntity)
    }

    @Test
    fun mapFromRemoteReturnsNullIfInvalidAddress() {
        val cinemaModel = CinemaFactory.makeGencatCinemaModelWithInvalidAddress()
        val cinemaEntity = cinemaEntityMapper.mapFromRemote(cinemaModel)

        cinemaModel.address = null
        assertEquals(null, cinemaEntity)
    }

    @Test
    fun mapFromRemoteMapsDataWithNullUnrequiredFields() {
        val cinemaModel = CinemaFactory.makeGencatCinemaModelWithNullUnrequiredFields()
        val cinemaEntity = cinemaEntityMapper.mapFromRemote(cinemaModel)

        assertEquals(cinemaModel.id?.toLong(), cinemaEntity!!.id)
        assertEquals(cinemaModel.name, cinemaEntity.name)
        assertEquals(cinemaModel.address, cinemaEntity.address)
        assertEquals(null, cinemaEntity.town)
        assertEquals(null, cinemaEntity.region)
        assertEquals(null, cinemaEntity.province)
        assertEquals(null, cinemaEntity.postalCode)
    }

    @Test
    fun mapFromRemoteMapsDataWithEmptyUnrequiredFields() {
        val cinemaModel = CinemaFactory.makeGencatCinemaModelWithEmptyUnrequiredFields()
        val cinemaEntity = cinemaEntityMapper.mapFromRemote(cinemaModel)

        assertEquals(cinemaModel.id?.toLong(), cinemaEntity!!.id)
        assertEquals(cinemaModel.name, cinemaEntity.name)
        assertEquals(cinemaModel.address, cinemaEntity.address)
        assertEquals(null, cinemaEntity.town)
        assertEquals(null, cinemaEntity.region)
        assertEquals(null, cinemaEntity.province)
        assertEquals(null, cinemaEntity.postalCode)
    }

    @Test
    fun mapFromRemoteMapsDataWithInvalidUnrequiredFields() {
        val cinemaModel = CinemaFactory.makeGencatCinemaModelWithInvalidUnrequiredFields()
        val cinemaEntity = cinemaEntityMapper.mapFromRemote(cinemaModel)

        assertEquals(cinemaModel.id?.toLong(), cinemaEntity!!.id)
        assertEquals(cinemaModel.name, cinemaEntity.name)
        assertEquals(cinemaModel.address, cinemaEntity.address)
        assertEquals(null, cinemaEntity.town)
        assertEquals(null, cinemaEntity.region)
        assertEquals(null, cinemaEntity.province)
        assertEquals(null, cinemaEntity.postalCode)
    }


    private fun assertCinemaEquality(
        cinema: Cinema, cinemaModel: GencatCinema, postalCode: Int?
    ) {
        assertEquals(cinemaModel.id?.toLong(), cinema.id)
        assertEquals(cinemaModel.name, cinema.name)
        assertEquals(cinemaModel.address, cinema.address)
        assertEquals(cinemaModel.town, cinema.town)
        assertEquals(cinemaModel.region, cinema.region)
        assertEquals(cinemaModel.province, cinema.province)
        assertEquals(postalCode, cinema.postalCode)
    }
}