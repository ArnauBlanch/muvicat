package xyz.arnau.muvicat.remote

import io.reactivex.Flowable
import xyz.arnau.muvicat.data.model.MovieEntity
import xyz.arnau.muvicat.data.repository.GencatRemote
import xyz.arnau.muvicat.remote.mapper.GencatMovieEntityMapper
import xyz.arnau.muvicat.remote.service.GencatService

class GencatRemoteImpl(
    private val gencatService: GencatService, private val entityMapper: GencatMovieEntityMapper
) : GencatRemote {
    override fun getMovies(): Flowable<List<MovieEntity>> =
        gencatService.getMovies()
            .map { it.movies }
            .map {
                val entities = mutableListOf<MovieEntity>()
                it.forEach { entities.add(entityMapper.mapFromRemote(it)) }
                entities
            }
}