package xyz.arnau.muvicat.remote.test

import xyz.arnau.muvicat.remote.model.GencatShowing
import xyz.arnau.muvicat.remote.model.GencatShowingResponse
import xyz.arnau.muvicat.utils.DataFactory.Factory.randomInt
import xyz.arnau.muvicat.utils.DataFactory.Factory.randomString

class GencatShowingFactory {
    companion object Factory {
        fun makeGencatShowingModel(): GencatShowing =
            GencatShowing(
                movieId = randomInt(),
                cinemaId = randomInt(),
                date = "01/01/2000",
                version = randomString(),
                seasonId = randomInt()
            )

        fun makeGencatShowingModelWithNullMovieId(): GencatShowing =
            GencatShowing(
                movieId = null,
                cinemaId = randomInt(),
                date = "01/01/2000",
                version = randomString(),
                seasonId = randomInt()
            )

        fun makeGencatShowingModelWithNullCinemaId(): GencatShowing =
            GencatShowing(
                movieId = randomInt(),
                cinemaId = null,
                date = "01/01/2000",
                version = randomString(),
                seasonId = randomInt()
            )

        fun makeGencatShowingModelWithNullDate(): GencatShowing =
            GencatShowing(
                movieId = randomInt(),
                cinemaId = randomInt(),
                date = null,
                version = randomString(),
                seasonId = randomInt()
            )

        fun makeGencatShowingModelWithInvalidDate(): GencatShowing =
            GencatShowing(
                movieId = randomInt(),
                cinemaId = randomInt(),
                date = "0A1/0A1/20/00",
                version = randomString(),
                seasonId = randomInt()
            )

        fun makeGencatShowingModelWithNullVersion(): GencatShowing =
            GencatShowing(
                movieId = randomInt(),
                cinemaId = randomInt(),
                date = "01/01/2000",
                version = null,
                seasonId = randomInt()
            )

        fun makeGencatShowingModelWithInvalidVersion(): GencatShowing =
            GencatShowing(
                movieId = randomInt(),
                cinemaId = randomInt(),
                date = "01/01/2000",
                version = "--",
                seasonId = randomInt()
            )

        fun makeGencatShowingModelWithEmptyVersion(): GencatShowing =
            GencatShowing(
                movieId = randomInt(),
                cinemaId = randomInt(),
                date = "01/01/2000",
                version = "",
                seasonId = randomInt()
            )

        fun makeGencatShowingModelWithNullSeasonId(): GencatShowing =
            GencatShowing(
                movieId = randomInt(),
                cinemaId = randomInt(),
                date = "01/01/2000",
                version = randomString(),
                seasonId = null
            )

        fun makeGencatShowingModelWithSeasonIdEqualTo0(): GencatShowing =
            GencatShowing(
                movieId = randomInt(),
                cinemaId = randomInt(),
                date = "01/01/2000",
                version = randomString(),
                seasonId = 0
            )

        fun makeGencatShowingModelWithSeasonidEqualTo1(): GencatShowing =
            GencatShowing(
                movieId = randomInt(),
                cinemaId = randomInt(),
                date = "01/01/2000",
                version = randomString(),
                seasonId = 1
            )

        fun makeGencatShowingResponse(count: Int): GencatShowingResponse {
            return GencatShowingResponse(makeGencatShowingModelList(count))
        }

        private fun makeGencatShowingModelList(count: Int): List<GencatShowing> {
            val showingModels = mutableListOf<GencatShowing>()
            repeat(count) {
                showingModels.add(makeGencatShowingModel())
            }
            return showingModels
        }
    }
}