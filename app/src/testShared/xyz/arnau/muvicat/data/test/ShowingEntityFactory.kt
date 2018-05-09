package xyz.arnau.muvicat.data.test

import xyz.arnau.muvicat.cache.model.ShowingEntity
import xyz.arnau.muvicat.utils.DataFactory.Factory.randomLong
import xyz.arnau.muvicat.utils.DataFactory.Factory.randomString
import java.util.*

@Suppress("DEPRECATION")
class ShowingEntityFactory {
    companion object Factory {
        private fun makeShowingEntity() =
            ShowingEntity(
                null, randomLong(), randomLong(), Date(2000, 1, 1), randomString(), randomLong()
            )

        fun makeShowingEntityList(count: Int): List<ShowingEntity> {
            val showings = mutableListOf<ShowingEntity>()
            repeat(count) {
                showings.add(makeShowingEntity())
            }
            return showings
        }
    }
}