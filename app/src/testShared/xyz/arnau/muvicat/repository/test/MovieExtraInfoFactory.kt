package xyz.arnau.muvicat.repository.test

import xyz.arnau.muvicat.cache.model.MovieExtraInfo
import xyz.arnau.muvicat.utils.DataFactory.Factory.randomDouble
import xyz.arnau.muvicat.utils.DataFactory.Factory.randomInt
import xyz.arnau.muvicat.utils.DataFactory.Factory.randomString

class MovieExtraInfoFactory {
    companion object Factory {
        fun makeExtraInfo() =
            MovieExtraInfo(
                randomInt(),
                randomInt(),
                listOf(randomString(), randomString()),
                randomString(),
                randomDouble(),
                randomInt(),
                CastMemberEntityFactory.makeCastMemberEntityList(3)
            )
    }
}