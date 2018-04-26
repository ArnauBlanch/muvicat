package xyz.arnau.muvicat.data.test

import xyz.arnau.muvicat.data.model.Movie
import xyz.arnau.muvicat.utils.DataFactory.Factory.randomDate
import xyz.arnau.muvicat.utils.DataFactory.Factory.randomInt
import xyz.arnau.muvicat.utils.DataFactory.Factory.randomLong
import xyz.arnau.muvicat.utils.DataFactory.Factory.randomString

class MovieFactory {
    companion object Factory {
        fun makeMovie() =
                Movie(
                        randomLong(), randomString(), randomString(), randomInt(),
                        randomString(), randomString(), randomString(), randomDate(), randomString(),
                        randomInt(), randomString(), randomString(), randomString()
                )

        private fun makeMovieWithNullValues() =
                Movie(
                        randomLong(), null, null, null, null,
                        null, null, null, null, null,
                        null, null, null
                )

        fun makeMovieList(count: Int): List<Movie> {
            val movies = mutableListOf<Movie>()
            repeat(count) {
                movies.add(makeMovie())
            }
            return movies
        }

        fun makeMovieListWithNullValues(count: Int): List<Movie> {
            val movies = mutableListOf<Movie>()
            repeat(count) {
                movies.add(makeMovieWithNullValues())
            }
            return movies
        }
    }
}