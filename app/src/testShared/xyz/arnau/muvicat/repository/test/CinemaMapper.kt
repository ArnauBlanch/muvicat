package xyz.arnau.muvicat.repository.test

import xyz.arnau.muvicat.cache.model.CinemaEntity
import xyz.arnau.muvicat.repository.model.Cinema

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
            null,
            0,
            0
        )

    fun mapFromCinemaEntityList(cinemaList: List<CinemaEntity>) =
        cinemaList.map { mapFromCinemaEntity(it) }
}