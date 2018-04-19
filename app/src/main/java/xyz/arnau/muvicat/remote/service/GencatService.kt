package xyz.arnau.muvicat.remote.service

import io.reactivex.Single
import retrofit2.http.GET
import xyz.arnau.muvicat.remote.model.GencatMovieResponse

interface GencatService {
    @GET("provacin.xml")
    fun getMovies(): Single<GencatMovieResponse>
}
// TODO: test with mock response