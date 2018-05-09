package xyz.arnau.muvicat.data.test

import xyz.arnau.muvicat.cache.model.CinemaEntity
import xyz.arnau.muvicat.utils.DataFactory.Factory.randomLong
import xyz.arnau.muvicat.utils.DataFactory.Factory.randomString

class CinemaEntityFactory {
    companion object Factory {
        fun makeCinemaEntity() =
            CinemaEntity(
                randomLong(),
                randomString(),
                randomString(),
                randomString(),
                randomString(),
                randomString(),
                null
            )

        private fun makeCinemaEntityWithNullValues() =
            CinemaEntity(
                randomLong(), randomString(), randomString(), null, null,
                null, null
            )

        fun makeCinemaEntityList(count: Int): List<CinemaEntity> {
            val cinemas = mutableListOf<CinemaEntity>()
            repeat(count) {
                cinemas.add(makeCinemaEntity())
            }
            return cinemas
        }

        fun makeCinemaEntityListWithNullValues(count: Int): List<CinemaEntity> {
            val cinemas = mutableListOf<CinemaEntity>()
            repeat(count) {
                cinemas.add(makeCinemaEntityWithNullValues())
            }
            return cinemas
        }
    }
}