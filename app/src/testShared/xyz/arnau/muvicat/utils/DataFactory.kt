package xyz.arnau.muvicat.utils

import java.util.*
import java.util.concurrent.ThreadLocalRandom

class DataFactory {
    companion object Factory {
        fun randomInt() = ThreadLocalRandom.current().nextInt(0, 1000 + 1)

        fun randomLong() = randomInt().toLong()

        fun randomString() = UUID.randomUUID().toString()

        fun randomDouble() = ThreadLocalRandom.current().nextDouble(20.0)

        fun randomDate() = Date(randomLong())
    }
}