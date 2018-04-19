package xyz.arnau.muvicat.remote

import io.reactivex.Single
import xyz.arnau.muvicat.data.model.Movie
import xyz.arnau.muvicat.data.repository.GencatRemote
import xyz.arnau.muvicat.remote.mapper.GencatMovieEntityMapper
import xyz.arnau.muvicat.remote.service.GencatService

class GencatRemoteImpl(
    private val gencatService: GencatService, private val entityMapper: GencatMovieEntityMapper
) : GencatRemote {
    override fun getMovies(): Single<List<Movie>> =
        gencatService.getMovies()
            .map { it.moviesList }
            .map {
                val entities = mutableListOf<Movie>()
                it.forEach { entities.add(entityMapper.mapFromRemote(it)) }
                entities
            }
}