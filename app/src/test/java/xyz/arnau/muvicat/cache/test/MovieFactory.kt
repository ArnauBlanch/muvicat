package xyz.arnau.muvicat.cache.test

import xyz.arnau.muvicat.cache.model.CachedMovie
import xyz.arnau.muvicat.cache.test.DataFactory.Factory.randomDate
import xyz.arnau.muvicat.cache.test.DataFactory.Factory.randomInt
import xyz.arnau.muvicat.cache.test.DataFactory.Factory.randomLong
import xyz.arnau.muvicat.cache.test.DataFactory.Factory.randomString

class MovieFactory {
    companion object Factory {
        fun makeCachedMovie(): CachedMovie {
            return CachedMovie(randomLong(), randomString(), randomString(), randomInt(),
                    randomString(), randomString(), randomString(), randomDate(), randomString(),
                    randomInt(), randomString(), randomString(), randomString())
        }

        fun makeCachedMovieWithNullValues(): CachedMovie {
            return CachedMovie(randomLong(), null, null, null, null,
                    null, null, null, null, null,
                    null, null, null)
        }

        fun makeCachedMovieList(count: Int): List<CachedMovie>  {
            val cachedMovies = mutableListOf<CachedMovie>()
            repeat(count) {
                cachedMovies.add(makeCachedMovie())
            }
            return cachedMovies
        }

        fun makeCachedMovieListWithNullValues(count: Int): List<CachedMovie>  {
            val cachedMovies = mutableListOf<CachedMovie>()
            repeat(count) {
                cachedMovies.add(makeCachedMovieWithNullValues())
            }
            return cachedMovies
        }
    }
}