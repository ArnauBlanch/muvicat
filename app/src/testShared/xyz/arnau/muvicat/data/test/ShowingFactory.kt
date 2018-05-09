package xyz.arnau.muvicat.data.test

import xyz.arnau.muvicat.data.model.Showing
import xyz.arnau.muvicat.utils.DataFactory.Factory.randomLong
import xyz.arnau.muvicat.utils.DataFactory.Factory.randomString
import java.util.*

@Suppress("DEPRECATION")
class ShowingFactory {
    companion object Factory {
        fun makeShowing() =
            Showing(
                null, randomLong(), randomLong(), Date(2000, 1, 1), randomString(), randomLong()
            )

        private fun makeShowingWithNullValues() =
            Showing(
                null, randomLong(), randomLong(), Date(2000, 1, 1),
                null,
                null
            )

        fun makeShowingList(count: Int): List<Showing> {
            val showings = mutableListOf<Showing>()
            repeat(count) {
                showings.add(makeShowing())
            }
            return showings
        }

        fun makeShowingListWithNullValues(count: Int): List<Showing> {
            val showings = mutableListOf<Showing>()
            repeat(count) {
                showings.add(makeShowingWithNullValues())
            }
            return showings
        }
    }
}