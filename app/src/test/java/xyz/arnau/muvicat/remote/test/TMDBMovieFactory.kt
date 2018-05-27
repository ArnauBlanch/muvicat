package xyz.arnau.muvicat.remote.test

import xyz.arnau.muvicat.remote.model.gencat.GencatShowing
import xyz.arnau.muvicat.remote.model.gencat.GencatShowingResponse
import xyz.arnau.muvicat.remote.model.tmdb.TMDBCastMember
import xyz.arnau.muvicat.remote.model.tmdb.TMDBCredits
import xyz.arnau.muvicat.remote.model.tmdb.TMDBMovie
import xyz.arnau.muvicat.remote.model.tmdb.TMDBSearchedMovie
import xyz.arnau.muvicat.utils.DataFactory.Factory.randomDouble
import xyz.arnau.muvicat.utils.DataFactory.Factory.randomInt
import xyz.arnau.muvicat.utils.DataFactory.Factory.randomString

class TMDBMovieFactory {
    companion object Factory {
        fun makeTMDBMovieModel(): TMDBMovie =
            TMDBMovie(
                id = randomInt(),
                runtime = Math.abs(randomInt()),
                vote_average = randomDouble(),
                vote_count = randomInt(),
                credits = TMDBCredits(listOf(
                    TMDBCastMember(randomInt(), randomInt(), randomString(), randomString(), randomString()),
                    TMDBCastMember(randomInt(), randomInt(), randomString(), randomString(), randomString()),
                    TMDBCastMember(randomInt(), randomInt(), randomString(), randomString(), randomString())
                ))
            )
        fun makeTMDBMovieModelWithZeroRuntime(): TMDBMovie =
            TMDBMovie(
                id = randomInt(),
                runtime = 0,
                vote_average = randomDouble(),
                vote_count = randomInt(),
                credits = TMDBCredits(listOf(
                    TMDBCastMember(randomInt(), randomInt(), randomString(), randomString(), randomString()),
                    TMDBCastMember(randomInt(), randomInt(), randomString(), randomString(), randomString()),
                    TMDBCastMember(randomInt(), randomInt(), randomString(), randomString(), randomString())
                ))
            )
    }
}