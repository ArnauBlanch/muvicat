package xyz.arnau.muvicat.remote.service

import io.reactivex.Flowable
import retrofit2.http.GET
import xyz.arnau.muvicat.remote.model.GencatMovieModel

interface GencatService {
    @GET("/provacin.xml")
    fun getMovies(): Flowable<GencatMovieResponse>

    data class GencatMovieResponse(var movies: List<GencatMovieModel>) // TODO: fix this in the coverage report
}
// TODO: test with mock response