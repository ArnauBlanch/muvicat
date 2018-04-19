package xyz.arnau.muvicat.remote.test

import xyz.arnau.muvicat.remote.model.GencatMovieModel
import xyz.arnau.muvicat.remote.model.GencatMovieResponse
import xyz.arnau.muvicat.remote.service.GencatService
import xyz.arnau.muvicat.utils.DataFactory.Factory.randomInt
import xyz.arnau.muvicat.utils.DataFactory.Factory.randomString

class MovieFactory {
    companion object Factory {
        fun makeGencatMovieModel(): GencatMovieModel =
            GencatMovieModel(
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

        fun makeGencatMovieModelWithUnknownValues(): GencatMovieModel =
            GencatMovieModel(
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

        fun makeGencatMovieModelWithEmptyValues(): GencatMovieModel =
            GencatMovieModel(
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

        fun makeGencatMovieResponse(): GencatMovieResponse {
            return GencatMovieResponse(makeGencatMovieModelList(5))
        }

        private fun makeGencatMovieModelList(count: Int): List<GencatMovieModel> {
            val movieModels = mutableListOf<GencatMovieModel>()
            repeat(count) {
                movieModels.add(makeGencatMovieModel())
            }
            return movieModels
        }
    }
}