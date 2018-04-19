package xyz.arnau.muvicat.cache.mapper

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import xyz.arnau.muvicat.cache.model.CachedMovie
import xyz.arnau.muvicat.cache.test.MovieFactory
import xyz.arnau.muvicat.data.model.Movie

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

    private fun assertMovieEquality(movie: Movie, cachedMovie: CachedMovie) {
        assertEquals(cachedMovie.id, movie.id)
        assertEquals(cachedMovie.title, movie.title)
        assertEquals(cachedMovie.originalTitle, movie.originalTitle)
        assertEquals(cachedMovie.year, movie.year)
        assertEquals(cachedMovie.direction, movie.direction)
        assertEquals(cachedMovie.cast, movie.cast)
        assertEquals(cachedMovie.plot, movie.plot)
        assertEquals(cachedMovie.releaseDate, movie.releaseDate)
        assertEquals(cachedMovie.posterUrl, movie.posterUrl)
        assertEquals(cachedMovie.priority, movie.priority)
        assertEquals(cachedMovie.originalLanguage, movie.originalLanguage)
        assertEquals(cachedMovie.ageRating, movie.ageRating)
        assertEquals(cachedMovie.trailerUrl, movie.trailerUrl)
    }
}