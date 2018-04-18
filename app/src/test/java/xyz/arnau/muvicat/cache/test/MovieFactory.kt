package xyz.arnau.muvicat.cache.test

import xyz.arnau.muvicat.cache.model.CachedMovie
import xyz.arnau.muvicat.utils.DataFactory.Factory.randomDate
import xyz.arnau.muvicat.utils.DataFactory.Factory.randomInt
import xyz.arnau.muvicat.utils.DataFactory.Factory.randomLong
import xyz.arnau.muvicat.utils.DataFactory.Factory.randomString
import xyz.arnau.muvicat.data.model.MovieEntity

class MovieFactory {
    companion object Factory {
        fun makeCachedMovie() =
            CachedMovie(
                randomLong(), randomString(), randomString(), randomInt(),
                randomString(), randomString(), randomString(), randomDate(), randomString(),
                randomInt(), randomString(), randomString(), randomString()
            )

        fun makeMovieEntity() =
            MovieEntity(
                randomLong(), randomString(), randomString(), randomInt(),
                randomString(), randomString(), randomString(), randomDate(), randomString(),
                randomInt(), randomString(), randomString(), randomString()
            )

        private fun makeCachedMovieWithNullValues() =
            CachedMovie(
                randomLong(), null, null, null, null,
                null, null, null, null, null,
                null, null, null
            )

        fun makeMovieEntityList(count: Int): List<MovieEntity> {
            val movieEntities = mutableListOf<MovieEntity>()
            repeat(count) {
                movieEntities.add(makeMovieEntity())
            }
            return movieEntities
        }

        fun makeCachedMovieList(count: Int): List<CachedMovie> {
            val cachedMovies = mutableListOf<CachedMovie>()
            repeat(count) {
                cachedMovies.add(makeCachedMovie())
            }
            return cachedMovies
        }

        fun makeCachedMovieListWithNullValues(count: Int): List<CachedMovie> {
            val cachedMovies = mutableListOf<CachedMovie>()
            repeat(count) {
                cachedMovies.add(makeCachedMovieWithNullValues())
            }
            return cachedMovies
        }
    }
}