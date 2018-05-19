package xyz.arnau.muvicat.repository.test

import xyz.arnau.muvicat.cache.model.CastMemberEntity
import xyz.arnau.muvicat.cache.model.MovieExtraInfo
import xyz.arnau.muvicat.cache.model.PostalCodeEntity
import xyz.arnau.muvicat.utils.DataFactory.Factory.randomDouble
import xyz.arnau.muvicat.utils.DataFactory.Factory.randomInt
import xyz.arnau.muvicat.utils.DataFactory.Factory.randomLong
import xyz.arnau.muvicat.utils.DataFactory.Factory.randomString

class CastMemberEntityFactory {
    companion object Factory {
        private fun makeCastMemberEntity() =
            CastMemberEntity(
                randomInt(),
                randomLong(),
                randomInt(),
                randomString(),
                randomString(),
                randomString()
            )

        fun makeCastMemberEntityList(count: Int): List<CastMemberEntity> {
            val list = mutableListOf<CastMemberEntity>()
            repeat(count) { list.add(makeCastMemberEntity()) }
            return list
        }
    }
}