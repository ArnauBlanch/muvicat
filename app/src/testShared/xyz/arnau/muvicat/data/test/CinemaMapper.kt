package xyz.arnau.muvicat.data.test

import xyz.arnau.muvicat.cache.model.CinemaEntity
import xyz.arnau.muvicat.data.model.Cinema

object CinemaMapper {
    fun mapFromCinemaEntity(cinema: CinemaEntity) =
        Cinema(
            cinema.id,
            cinema.name,
            cinema.address,
            cinema.town,
            cinema.region,
            cinema.province,
            null,
            null
        )

    fun mapFromCinemaEntityList(cinemaList: List<CinemaEntity>) =
        cinemaList.map { mapFromCinemaEntity(it) }
}