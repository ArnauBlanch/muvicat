package xyz.arnau.muvicat.remote.service

import android.arch.lifecycle.LiveData
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import xyz.arnau.muvicat.remote.model.tmdb.TMDBMovie
import xyz.arnau.muvicat.remote.model.tmdb.TMDBSearchMovieResponse
import xyz.arnau.muvicat.remote.utils.ApiResponse

interface TMDBService {
    @GET("search/movie")
    fun searchMovie(@Query("query") title: String, @Query("api_key") apiKey: String = TMDBApiKey): LiveData<ApiResponse<TMDBSearchMovieResponse>>

    @GET("movie/{movie_id}")
    fun getMovie(@Path("movie_id") movieId: Int, @Query("api_key") apiKey: String = TMDBApiKey, @Query("append_to_response") append: String = "credits"): LiveData<ApiResponse<TMDBMovie>>
}