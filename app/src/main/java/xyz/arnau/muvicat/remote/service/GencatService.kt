package xyz.arnau.muvicat.remote.service

import android.arch.lifecycle.LiveData
import retrofit2.http.GET
import retrofit2.http.Header
import xyz.arnau.muvicat.remote.model.GencatMovieResponse
import xyz.arnau.muvicat.remote.util.ApiResponse

interface GencatService {
    @GET("provacin.xml")
    fun getMovies(@Header("If-None-Match") eTag: String?): LiveData<ApiResponse<GencatMovieResponse>>
}
// TODO: test with mock response