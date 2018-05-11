package xyz.arnau.muvicat.data.test

import xyz.arnau.muvicat.data.model.Cinema
import xyz.arnau.muvicat.utils.DataFactory.Factory.randomLong
import xyz.arnau.muvicat.utils.DataFactory.Factory.randomString

class CinemaFactory {
    companion object Factory {
        fun makeCinema() =
            Cinema(
                randomLong(),
                randomString(),
                randomString(),
                randomString(),
                randomString(),
                randomString(),
                null,
                null
            )

        fun makeCinemaList(count: Int): List<Cinema> {
            val cinemas = mutableListOf<Cinema>()
            repeat(count) {
                cinemas.add(makeCinema())
            }
            return cinemas
        }

    }
}