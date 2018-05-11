package xyz.arnau.muvicat.data.test

import xyz.arnau.muvicat.cache.model.MovieEntity
import xyz.arnau.muvicat.utils.DataFactory.Factory.randomDate
import xyz.arnau.muvicat.utils.DataFactory.Factory.randomInt
import xyz.arnau.muvicat.utils.DataFactory.Factory.randomLong
import xyz.arnau.muvicat.utils.DataFactory.Factory.randomString

class MovieEntityFactory {
    companion object Factory {
        fun makeMovieEntity() =
            MovieEntity(
                randomLong(), randomString(), randomString(), randomInt(),
                randomString(), randomString(), randomString(), randomDate(), randomString(),
                randomInt(), randomString(), randomString(), randomString()
            )

        private fun makeMovieEntityWithNullValues() =
            MovieEntity(
                randomLong(), null, null, null, null,
                null, null, null, null, null,
                null, null, null
            )

        fun makeMovieEntityList(count: Int): List<MovieEntity> {
            val movies = mutableListOf<MovieEntity>()
            repeat(count) {
                movies.add(makeMovieEntity())
            }
            return movies
        }

        fun makeMovieEntityListWithNullValues(count: Int): List<MovieEntity> {
            val movies = mutableListOf<MovieEntity>()
            repeat(count) {
                movies.add(makeMovieEntityWithNullValues())
            }
            return movies
        }
    }
}