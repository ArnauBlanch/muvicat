package xyz.arnau.muvicat.cache.mapper

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import xyz.arnau.muvicat.cache.mapper.MovieEntityMapper
import xyz.arnau.muvicat.cache.model.CachedMovie
import xyz.arnau.muvicat.cache.test.MovieFactory
import xyz.arnau.muvicat.data.model.MovieEntity

@RunWith(JUnit4::class)
class MovieEntityMapperTest {
    private lateinit var movieEntityMapper: MovieEntityMapper

    @Before
    fun setUp() {
        movieEntityMapper = MovieEntityMapper()
    }

    @Test
    fun mapToCachedMapsData() {
        val movieEntity = MovieFactory.makeMovieEntity()
        val cachedMovie = movieEntityMapper.mapToCached(movieEntity)

        assertMovieEquality(movieEntity, cachedMovie)
    }

    @Test
    fun mapFromCachedMapsData() {
        val cachedMovie = MovieFactory.makeCachedMovie()
        val movieEntity = movieEntityMapper.mapFromCached(cachedMovie)

        assertMovieEquality(movieEntity, cachedMovie)
    }

    private fun assertMovieEquality(movieEntity: MovieEntity, cachedMovie: CachedMovie) {
        assertEquals(movieEntity.id, cachedMovie.id)
        assertEquals(movieEntity.title, cachedMovie.title)
        assertEquals(movieEntity.originalTitle, cachedMovie.originalTitle)
        assertEquals(movieEntity.year, cachedMovie.year)
        assertEquals(movieEntity.direction, cachedMovie.direction)
        assertEquals(movieEntity.cast, cachedMovie.cast)
        assertEquals(movieEntity.plot, cachedMovie.plot)
        assertEquals(movieEntity.releaseDate, cachedMovie.releaseDate)
        assertEquals(movieEntity.posterUrl, cachedMovie.posterUrl)
        assertEquals(movieEntity.priority, cachedMovie.priority)
        assertEquals(movieEntity.originalLanguage, cachedMovie.originalLanguage)
        assertEquals(movieEntity.ageRating, cachedMovie.ageRating)
        assertEquals(movieEntity.trailerUrl, cachedMovie.trailerUrl)
    }
}