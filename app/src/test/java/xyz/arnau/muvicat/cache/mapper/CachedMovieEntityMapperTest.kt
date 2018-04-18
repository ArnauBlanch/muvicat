package xyz.arnau.muvicat.cache.mapper

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import xyz.arnau.muvicat.cache.model.CachedMovie
import xyz.arnau.muvicat.cache.test.MovieFactory
import xyz.arnau.muvicat.data.model.MovieEntity

@RunWith(JUnit4::class)
class CachedMovieEntityMapperTest {
    private lateinit var movieEntityMapper: CachedMovieEntityMapper

    @Before
    fun setUp() {
        movieEntityMapper = CachedMovieEntityMapper()
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
        assertEquals(cachedMovie.id, movieEntity.id)
        assertEquals(cachedMovie.title, movieEntity.title)
        assertEquals(cachedMovie.originalTitle, movieEntity.originalTitle)
        assertEquals(cachedMovie.year, movieEntity.year)
        assertEquals(cachedMovie.direction, movieEntity.direction)
        assertEquals(cachedMovie.cast, movieEntity.cast)
        assertEquals(cachedMovie.plot, movieEntity.plot)
        assertEquals(cachedMovie.releaseDate, movieEntity.releaseDate)
        assertEquals(cachedMovie.posterUrl, movieEntity.posterUrl)
        assertEquals(cachedMovie.priority, movieEntity.priority)
        assertEquals(cachedMovie.originalLanguage, movieEntity.originalLanguage)
        assertEquals(cachedMovie.ageRating, movieEntity.ageRating)
        assertEquals(cachedMovie.trailerUrl, movieEntity.trailerUrl)
    }
}