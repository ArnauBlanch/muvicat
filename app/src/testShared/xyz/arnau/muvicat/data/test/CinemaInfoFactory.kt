package xyz.arnau.muvicat.data.test

import xyz.arnau.muvicat.data.model.CinemaInfo
import xyz.arnau.muvicat.utils.DataFactory.Factory.randomLong
import xyz.arnau.muvicat.utils.DataFactory.Factory.randomString

class CinemaInfoFactory {
    companion object Factory {
        fun makeCinemaInfo() =
            CinemaInfo(
                randomLong(),
                randomString(),
                randomString(),
                randomString(),
                randomString(),
                randomString(),
                null,
                null
            )

        private fun makeCinemaInfoWithNullValues() =
            CinemaInfo(
                randomLong(), randomString(), randomString(), null, null,
                null, null, null
            )

        fun makeCinemaInfoList(count: Int): List<CinemaInfo> {
            val cinemas = mutableListOf<CinemaInfo>()
            repeat(count) {
                cinemas.add(makeCinemaInfo())
            }
            return cinemas
        }

        fun makeCinemaInfoListWithNullValues(count: Int): List<CinemaInfo> {
            val cinemas = mutableListOf<CinemaInfo>()
            repeat(count) {
                cinemas.add(makeCinemaInfoWithNullValues())
            }
            return cinemas
        }
    }
}