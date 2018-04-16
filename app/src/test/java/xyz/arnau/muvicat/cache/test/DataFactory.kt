package xyz.arnau.muvicat.cache.test

import java.util.*
import java.util.concurrent.ThreadLocalRandom

class DataFactory {
    companion object Factory {
        fun randomInt() = ThreadLocalRandom.current().nextInt(0, 1000 + 1)

        fun randomLong() = randomInt().toLong()

        fun randomString() = UUID.randomUUID().toString()

        fun randomDate() = Date(randomLong())
    }
}