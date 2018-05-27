package xyz.arnau.muvicat.repository.test

import xyz.arnau.muvicat.cache.model.ShowingEntity
import xyz.arnau.muvicat.repository.model.CinemaShowing

object CinemaShowingMapper {
    private fun mapFromShowingEntity(showing: ShowingEntity) =
        CinemaShowing(
            showing.id, showing.date, showing.version,
            showing.movieId, null, null, false
        )

    fun mapFromShowingEntityList(showingList: List<ShowingEntity>) =
        showingList.map { mapFromShowingEntity(it) }
}