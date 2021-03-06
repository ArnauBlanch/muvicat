package xyz.arnau.muvicat.repository.test

import xyz.arnau.muvicat.cache.model.ShowingEntity
import xyz.arnau.muvicat.repository.model.MovieShowing

object MovieShowingMapper {
    private fun mapFromShowingEntity(showing: ShowingEntity) =
        MovieShowing(
            showing.id, showing.date, showing.version,
            showing.cinemaId, showing.cinemaId.toString(), null, showing.cinemaId.toString(),
            null, null, null
        )

    fun mapFromShowingEntityList(showingList: List<ShowingEntity>) =
        showingList.map { mapFromShowingEntity(it) }
}