package xyz.arnau.muvicat.remote.test

import xyz.arnau.muvicat.remote.model.GencatMovie
import xyz.arnau.muvicat.remote.model.GencatMovieResponse
import xyz.arnau.muvicat.utils.DataFactory.Factory.randomInt
import xyz.arnau.muvicat.utils.DataFactory.Factory.randomString

class MovieFactory {
    companion object Factory {
        fun makeGencatMovieModel(): GencatMovie =
            GencatMovie(
                randomInt(),
                randomString(),
                randomString(),
                randomInt().toString(),
                randomString(),
                randomString(),
                randomString(),
                "01/01/2000",
                randomString(),
                randomInt(),
                randomString(),
                randomString(),
                randomString()
            )

        fun makeGencatMovieModelWithUnknownValues(): GencatMovie =
            GencatMovie(
                randomInt(),
                "--",
                "--",
                "--",
                "--",
                "--",
                "--",
                "--",
                "--",
                randomInt(), // TODO: test if priority is not an integer (i.e. "--")
                "--",
                "--",
                "--"
            )

        fun makeGencatMovieModelWithEmptyValues(): GencatMovie =
            GencatMovie(
                randomInt(),
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                randomInt(), // TODO: test if priority is not an integer (i.e. "--")
                "",
                "",
                ""
            )

        fun makeGencatMovieModelWithNullId(): GencatMovie =
            GencatMovie(
                null,
                randomString(),
                randomString(),
                randomInt().toString(),
                randomString(),
                randomString(),
                randomString(),
                "01/01/2000",
                randomString(),
                randomInt(),
                randomString(),
                randomString(),
                randomString()
            )

        fun makeGencatMovieResponse(count: Int): GencatMovieResponse {
            return GencatMovieResponse(makeGencatMovieModelList(count))
        }

        private fun makeGencatMovieModelList(count: Int): List<GencatMovie> {
            val movieModels = mutableListOf<GencatMovie>()
            repeat(count) {
                movieModels.add(makeGencatMovieModel())
            }
            return movieModels
        }
    }
}