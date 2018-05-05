package xyz.arnau.muvicat.data.test

import xyz.arnau.muvicat.data.model.PostalCode
import xyz.arnau.muvicat.utils.DataFactory.Factory.randomDouble
import xyz.arnau.muvicat.utils.DataFactory.Factory.randomInt

class PostalCodeFactory {
    companion object Factory {
        fun makePostalCode() = PostalCode(randomInt(), randomDouble(), randomDouble())
    }
}