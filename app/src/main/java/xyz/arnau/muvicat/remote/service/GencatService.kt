package xyz.arnau.muvicat.remote.service

import android.arch.lifecycle.LiveData
import retrofit2.http.GET
import xyz.arnau.muvicat.remote.model.GencatMovieResponse
import xyz.arnau.muvicat.remote.util.ApiResponse

interface GencatService {
    @GET("provacin.xml")
    fun getMovies(): LiveData<ApiResponse<GencatMovieResponse>>
}
// TODO: test with mock response