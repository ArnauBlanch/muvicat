package xyz.arnau.muvicat.data.test

import xyz.arnau.muvicat.cache.model.ShowingEntity
import xyz.arnau.muvicat.data.model.CinemaShowing

object CinemaShowingMapper {
    private fun mapFromShowingEntity(showing: ShowingEntity) =
        CinemaShowing(
            showing.id, showing.date, showing.version,
            showing.movieId, null, null
        )

    fun mapFromShowingEntityList(showingList: List<ShowingEntity>) =
        showingList.map { mapFromShowingEntity(it) }
}