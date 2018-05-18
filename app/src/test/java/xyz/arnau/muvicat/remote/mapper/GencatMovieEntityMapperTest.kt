package xyz.arnau.muvicat.remote.mapper

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import xyz.arnau.muvicat.cache.model.MovieEntity
import xyz.arnau.muvicat.remote.model.GencatMovie
import xyz.arnau.muvicat.remote.test.GencatMovieFactory
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
        val movieModel = GencatMovieFactory.makeGencatMovieModel()
        movieModel.year = "2000"
        movieModel.releaseDate = "01/02/2003"
        val movieEntity = movieEntityMapper.mapFromRemote(movieModel)

        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val date = sdf.parse("01/02/2003")
        assertMovieEquality(movieEntity!!, movieModel, 2000, date)
    }

    @Test
    fun mapFromRemoteThrowsExceptionIfReleaseDateIsUnparseable() {
        val movieModel = GencatMovieFactory.makeGencatMovieModel()
        movieModel.year = "2000"
        movieModel.releaseDate = "01/02/----"
        val movieEntity = movieEntityMapper.mapFromRemote(movieModel)

        assertMovieEquality(movieEntity!!, movieModel, 2000, null)
    }

    @Test
    fun mapFromRemoteReturnsNullIfNullId() {
        val movieModel = GencatMovieFactory.makeGencatMovieModelWithNullId()
        val movieEntity = movieEntityMapper.mapFromRemote(movieModel)

        assertEquals(null, movieEntity)
    }

    @Test
    fun mapFromRemoteMapsDataAndSetsNullIfUnknown() {
        val movieModel = GencatMovieFactory.makeGencatMovieModelWithUnknownValues()
        val movieEntity = movieEntityMapper.mapFromRemote(movieModel)

        assertMovieEquality(movieEntity!!, movieModel, null, null)
    }

    @Test
    fun mapFromRemoteMapsDataAndSetsNullIfEmpty() {
        val movieModel = GencatMovieFactory.makeGencatMovieModelWithEmptyValues()
        val movieEntity = movieEntityMapper.mapFromRemote(movieModel)

        assertMovieEquality(movieEntity!!, movieModel, null, null)
    }

    @Test
    fun mapFromRemoteMapsDataWithRareYear() {
        val movieModel = GencatMovieFactory.makeGencatMovieModel()
        movieModel.year = "2014-2015"
        movieModel.releaseDate = "--"
        val movieEntity = movieEntityMapper.mapFromRemote(movieModel)

        assertMovieEquality(movieEntity!!, movieModel, 2014, null)
    }

    @Test()
    fun mapFromRemoteMapsDataWithUnparseableYear() {
        val movieModel = GencatMovieFactory.makeGencatMovieModel()
        movieModel.year = "201a4-2015"
        movieModel.releaseDate = "--"
        val movieEntity = movieEntityMapper.mapFromRemote(movieModel)

        assertMovieEquality(movieEntity!!, movieModel, null, null)
    }

    private fun assertMovieEquality(
        movie: MovieEntity, movieModel: GencatMovie, year: Int?, releaseDate: Date?
    ) {
        assertEquals(movieModel.id?.toLong(), movie.id)
        assertEquals(movieModel.title, movie.title)
        assertEquals(movieModel.originalTitle, movie.originalTitle)
        assertEquals(year, movie.year)
        assertEquals(movieModel.direction, movie.direction)
        assertEquals(movieModel.cast, movie.cast)
        assertEquals(movieModel.plot, movie.plot)
        assertEquals(releaseDate, movie.releaseDate)
        assertEquals(movieModel.posterUrl, movie.posterUrl)
        assertEquals(movieModel.priority, movie.priority)
        assertEquals(movieModel.originalLanguage, movie.originalLanguage)
        assertEquals(movieModel.ageRating, movie.ageRating)
        assertEquals(movieModel.trailerUrl, movie.trailerUrl)
    }
}