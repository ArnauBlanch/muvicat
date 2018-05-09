package xyz.arnau.muvicat.data.test

import xyz.arnau.muvicat.cache.model.ShowingEntity
import xyz.arnau.muvicat.data.model.Showing

object ShowingMapper {
    fun mapFromShowingEntity(showing: ShowingEntity) =
        Showing(
            showing.id, showing.date, showing.version, showing.seasonId,
            showing.movieId, null, null, showing.cinemaId.toString(),
            null, null, null,
            null, null
        )

    fun mapFromShowingEntityList(showingList: List<ShowingEntity>) =
        showingList.map { mapFromShowingEntity(it) }
}