package xyz.arnau.muvicat.remote.test

import xyz.arnau.muvicat.remote.model.GencatCinema
import xyz.arnau.muvicat.remote.model.GencatCinemaResponse
import xyz.arnau.muvicat.remote.model.GencatMovie
import xyz.arnau.muvicat.remote.model.GencatMovieResponse
import xyz.arnau.muvicat.utils.DataFactory.Factory.randomInt
import xyz.arnau.muvicat.utils.DataFactory.Factory.randomString

class CinemaFactory {
    companion object Factory {
        fun makeGencatCinemaModelWithoutPostalCode(): GencatCinema =
                GencatCinema(
                    id = randomInt(),
                    name = randomString(),
                    address = "test address",
                    town = randomString(),
                    region = randomString(),
                    province = randomString()
                )

        fun makeGencatCinemaModelWithPostalCode(postalCode: Int): GencatCinema =
            GencatCinema(
                id = randomInt(),
                name = randomString(),
                address = "test address $postalCode test address",
                town = randomString(),
                region = randomString(),
                province = randomString()
            )

        fun makeGencatCinemaModelWithNullId(): GencatCinema =
            GencatCinema(
                id = null,
                name = randomString(),
                address = randomString(),
                town = randomString(),
                region = randomString(),
                province = randomString()
            )

        fun makeGencatCinemaModelWithNullName(): GencatCinema =
            GencatCinema(
                id = randomInt(),
                name = null,
                address = "test address",
                town = randomString(),
                region = randomString(),
                province = randomString()
            )

        fun makeGencatCinemaModelWithEmptyName(): GencatCinema =
            GencatCinema(
                id = randomInt(),
                name = "",
                address = "test address",
                town = randomString(),
                region = randomString(),
                province = randomString()
            )

        fun makeGencatCinemaModelWithInvalidName(): GencatCinema =
            GencatCinema(
                id = randomInt(),
                name = "--",
                address = "test address",
                town = randomString(),
                region = randomString(),
                province = randomString()
            )

        fun makeGencatCinemaModelWithNullAddress(): GencatCinema =
            GencatCinema(
                id = randomInt(),
                name = randomString(),
                address = null,
                town = randomString(),
                region = randomString(),
                province = randomString()
            )

        fun makeGencatCinemaModelWithEmptyAddress(): GencatCinema =
            GencatCinema(
                id = randomInt(),
                name = randomString(),
                address = "",
                town = randomString(),
                region = randomString(),
                province = randomString()
            )

        fun makeGencatCinemaModelWithInvalidAddress(): GencatCinema =
            GencatCinema(
                id = randomInt(),
                name = randomString(),
                address = "--",
                town = randomString(),
                region = randomString(),
                province = randomString()
            )

        fun makeGencatCinemaModelWithNullUnrequiredFields(): GencatCinema =
            GencatCinema(
                id = randomInt(),
                name = randomString(),
                address = "test address",
                town = null,
                region = null,
                province = null
            )

        fun makeGencatCinemaModelWithEmptyUnrequiredFields(): GencatCinema =
            GencatCinema(
                id = randomInt(),
                name = randomString(),
                address = "test address",
                town = "",
                region = "",
                province = ""
            )

        fun makeGencatCinemaModelWithInvalidUnrequiredFields(): GencatCinema =
            GencatCinema(
                id = randomInt(),
                name = randomString(),
                address = "test address",
                town = "--",
                region = "--",
                province = "--"
            )

        fun makeGencatCinemaResponse(count: Int): GencatCinemaResponse {
            return GencatCinemaResponse(makeGencatCinemaModelList(count))
        }

        private fun makeGencatCinemaModelList(count: Int): List<GencatCinema> {
            val cinemaModels = mutableListOf<GencatCinema>()
            repeat(count) {
                cinemaModels.add(makeGencatCinemaModelWithoutPostalCode())
            }
            return cinemaModels
        }
    }
}