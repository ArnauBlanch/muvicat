package xyz.arnau.muvicat.repository.model

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Relation
import xyz.arnau.muvicat.cache.model.MovieCastMemberJoin

class MovieWithCast {
    @Embedded
    lateinit var movie :Movie

    @Relation(parentColumn = "movie.id", entityColumn = "movieId", entity = MovieCastMemberJoin::class)
    var castMembers: List<CastMember>? = null
}