package xyz.arnau.muvicat.remote.mapper

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import xyz.arnau.muvicat.data.model.Showing
import xyz.arnau.muvicat.remote.model.GencatShowing
import xyz.arnau.muvicat.remote.test.ShowingFactory
import java.text.SimpleDateFormat
import java.util.*

class GencatShowingEntityMapperTest {
    private lateinit var showingEntityMapper: GencatShowingEntityMapper

    @Before
    fun setUp() {
        showingEntityMapper = GencatShowingEntityMapper()
    }

    @Test
    fun mapFromRemoteMapsData() {
        val showingModel = ShowingFactory.makeGencatShowingModel()
        val showingEntity = showingEntityMapper.mapFromRemote(showingModel)

        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val date = sdf.parse(showingModel.date)
        assertShowingEquality(showingEntity!!, showingModel, date, showingModel.version, showingModel.seasonId)
    }

    @Test
    fun mapFromRemoteReturnsNullIfNullMovieId() {
        val showingModel = ShowingFactory.makeGencatShowingModelWithNullMovieId()
        val showingEntity = showingEntityMapper.mapFromRemote(showingModel)

        assertEquals(null, showingEntity)
    }

    @Test
    fun mapFromRemoteReturnsNullIfNullCinemaId() {
        val showingModel = ShowingFactory.makeGencatShowingModelWithNullCinemaId()
        val showingEntity = showingEntityMapper.mapFromRemote(showingModel)

        assertEquals(null, showingEntity)
    }

    @Test
    fun mapFromRemoteReturnsNullIfNullDate() {
        val showingModel = ShowingFactory.makeGencatShowingModelWithNullDate()
        val showingEntity = showingEntityMapper.mapFromRemote(showingModel)

        assertEquals(null, showingEntity)
    }

    @Test
    fun mapFromRemoteReturnsNullIfNullVersion() {
        val showingModel = ShowingFactory.makeGencatShowingModelWithNullVersion()
        val showingEntity = showingEntityMapper.mapFromRemote(showingModel)

        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val date = sdf.parse(showingModel.date)
        assertShowingEquality(showingEntity!!, showingModel, date, null, showingModel.seasonId)
    }

    @Test
    fun mapFromRemoteReturnsNullIfEmptyVersion() {
        val showingModel = ShowingFactory.makeGencatShowingModelWithEmptyVersion()
        val showingEntity = showingEntityMapper.mapFromRemote(showingModel)

        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val date = sdf.parse(showingModel.date)
        assertShowingEquality(showingEntity!!, showingModel, date, null, showingModel.seasonId)
    }

    @Test
    fun mapFromRemoteReturnsNullIfInvalidVersion() {
        val showingModel = ShowingFactory.makeGencatShowingModelWithInvalidVersion()
        val showingEntity = showingEntityMapper.mapFromRemote(showingModel)

        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val date = sdf.parse(showingModel.date)
        assertShowingEquality(showingEntity!!, showingModel, date, null, showingModel.seasonId)
    }

    @Test
    fun mapFromRemoteReturnsNullIfNullSeasonId() {
        val showingModel = ShowingFactory.makeGencatShowingModelWithNullSeasonId()
        val showingEntity = showingEntityMapper.mapFromRemote(showingModel)

        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val date = sdf.parse(showingModel.date)
        assertShowingEquality(showingEntity!!, showingModel, date, showingModel.version, null)
    }

    @Test
    fun mapFromRemoteReturnsNullIfSeasonIdEqualTo0() {
        val showingModel = ShowingFactory.makeGencatShowingModelWithSeasonIdEqualTo0()
        val showingEntity = showingEntityMapper.mapFromRemote(showingModel)

        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val date = sdf.parse(showingModel.date)
        assertShowingEquality(showingEntity!!, showingModel, date, showingModel.version, null)
    }

    @Test
    fun mapFromRemoteReturnsNullIfSeasonIdEqualTo1() {
        val showingModel = ShowingFactory.makeGencatShowingModelWithSeasonidEqualTo1()
        val showingEntity = showingEntityMapper.mapFromRemote(showingModel)

        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val date = sdf.parse(showingModel.date)
        assertShowingEquality(showingEntity!!, showingModel, date, showingModel.version, null)
    }


    private fun assertShowingEquality(
        showing: Showing, showingModel: GencatShowing, date: Date?, version: String?, seasonId: Int?
    ) {
        assertEquals(null, showing.id)
        assertEquals(showingModel.movieId?.toLong(), showing.movieId)
        assertEquals(showingModel.cinemaId?.toLong(), showing.cinemaId)
        assertEquals(date, showing.date)
        assertEquals(showingModel.version, version)
        assertEquals(showingModel.seasonId?.toLong(), seasonId?.toLong())
    }
}