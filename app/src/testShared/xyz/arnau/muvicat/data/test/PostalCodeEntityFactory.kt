package xyz.arnau.muvicat.data.test

import xyz.arnau.muvicat.cache.model.PostalCodeEntity
import xyz.arnau.muvicat.utils.DataFactory.Factory.randomDouble
import xyz.arnau.muvicat.utils.DataFactory.Factory.randomInt

class PostalCodeEntityFactory {
    companion object Factory {
        fun makePostalCodeEntity() =
            PostalCodeEntity(
                randomInt(),
                randomDouble(),
                randomDouble()
            )

        fun makePostalCodeEntityList(count: Int): List<PostalCodeEntity> {
            val codes = mutableListOf<PostalCodeEntity>()
            repeat(count) {
                codes.add(makePostalCodeEntity())
            }
            return codes
        }
    }
}