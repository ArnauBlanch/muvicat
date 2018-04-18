package xyz.arnau.muvicat.remote.mapper

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import xyz.arnau.muvicat.data.model.MovieEntity
import xyz.arnau.muvicat.remote.model.GencatMovieModel
import xyz.arnau.muvicat.remote.test.MovieFactory
import java.text.SimpleDateFormat
import java.util.*

class GencatMovieEntityMapperTest {
    private lateinit var movieEntityMapper: GencatMovieEntityMapper

    @Before
    fun setUp() {
        movieEntityMapper = GencatMovieEntityMapper()
    }

    @Test
    fun mapFromRemoteMapsData() {
        val movieModel = MovieFactory.makeGencatMovieModel()
        movieModel.year = "2000"
        movieModel.releaseDate = "01/02/2003"
        val movieEntity = movieEntityMapper.mapFromRemote(movieModel)

        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val date = sdf.parse("01/02/2003")
        assertMovieEquality(movieEntity, movieModel, 2000, date)
    }

    @Test
    fun mapFromRemoteThrowsExceptionIfReleaseDateIsUnparseable() {
        val movieModel = MovieFactory.makeGencatMovieModel()
        movieModel.year = "2000"
        movieModel.releaseDate = "01/02/----"
        val movieEntity = movieEntityMapper.mapFromRemote(movieModel)

        assertMovieEquality(movieEntity, movieModel, 2000, null)
    }

    @Test
    fun mapFromRemoteMapsDataAndSetsNullIfUnknown() {
        val movieModel = MovieFactory.makeGencatMovieModelWithUnknownValues()
        val movieEntity = movieEntityMapper.mapFromRemote(movieModel)

        assertMovieEquality(movieEntity, movieModel, null, null)
    }

    @Test
    fun mapFromRemoteMapsDataAndSetsNullIfEmpty() {
        val movieModel = MovieFactory.makeGencatMovieModelWithEmptyValues()
        val movieEntity = movieEntityMapper.mapFromRemote(movieModel)

        assertMovieEquality(movieEntity, movieModel, null, null)
    }

    @Test
    fun mapFromRemoteMapsDataWithRareYear() {
        val movieModel = MovieFactory.makeGencatMovieModel()
        movieModel.year = "2014-2015"
        movieModel.releaseDate = "--"
        val movieEntity = movieEntityMapper.mapFromRemote(movieModel)

        assertMovieEquality(movieEntity, movieModel, 2014, null)
    }

    @Test()
    fun mapFromRemoteMapsDataWithUnparseableYear() {
        val movieModel = MovieFactory.makeGencatMovieModel()
        movieModel.year = "201a4-2015"
        movieModel.releaseDate = "--"
        val movieEntity = movieEntityMapper.mapFromRemote(movieModel)

        assertMovieEquality(movieEntity, movieModel, null, null)
    }

    private fun assertMovieEquality(
        movieEntity: MovieEntity, movieModel: GencatMovieModel, year: Int?, releaseDate: Date?
    ) {
        assertEquals(movieModel.id.toLong(), movieEntity.id)
        assertEquals(movieModel.title, movieEntity.title)
        assertEquals(movieModel.originalTitle, movieEntity.originalTitle)
        assertEquals(year, movieEntity.year)
        assertEquals(movieModel.direction, movieEntity.direction)
        assertEquals(movieModel.cast, movieEntity.cast)
        assertEquals(movieModel.plot, movieEntity.plot)
        assertEquals(releaseDate, movieEntity.releaseDate)
        assertEquals(movieModel.posterUrl, movieEntity.posterUrl)
        assertEquals(movieModel.priority, movieEntity.priority)
        assertEquals(movieModel.originalLanguage, movieEntity.originalLanguage)
        assertEquals(movieModel.ageRating, movieEntity.ageRating)
        assertEquals(movieModel.trailerUrl, movieEntity.trailerUrl)
    }
}