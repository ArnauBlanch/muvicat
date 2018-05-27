package xyz.arnau.muvicat.remote.service

import android.arch.lifecycle.LiveData
import retrofit2.http.GET
import retrofit2.http.Header
import xyz.arnau.muvicat.remote.model.gencat.GencatCinemaResponse
import xyz.arnau.muvicat.remote.model.gencat.GencatMovieResponse
import xyz.arnau.muvicat.remote.model.gencat.GencatShowingResponse
import xyz.arnau.muvicat.remote.utils.ApiResponse

interface GencatService {
    @GET("provacin.xml")
    fun getMovies(@Header("If-None-Match") eTag: String?): LiveData<ApiResponse<GencatMovieResponse>>

    @GET("cinemes.xml")
    fun getCinemas(@Header("If-None-Match") eTag: String?): LiveData<ApiResponse<GencatCinemaResponse>>

    @GET("film_sessions.xml")
    fun getShowings(@Header("If-None-Match") eTag: String?): LiveData<ApiResponse<GencatShowingResponse>>
}