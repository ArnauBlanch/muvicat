package xyz.arnau.muvicat.repository.test

import xyz.arnau.muvicat.repository.model.Cinema
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
                null,
                0,
                0
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