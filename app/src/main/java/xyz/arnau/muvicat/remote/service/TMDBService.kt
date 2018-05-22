package xyz.arnau.muvicat.remote.service

import android.arch.lifecycle.LiveData
import retrofit2.http.*
import xyz.arnau.muvicat.remote.model.tmdb.*
import xyz.arnau.muvicat.remote.utils.ApiResponse

interface TMDBService {
    @GET("search/movie")
    fun searchMovie(
        @Query("query") title: String,
        @Query("api_key") apiKey: String = TMDBApiKey
    ): LiveData<ApiResponse<TMDBSearchMovieResponse>>

    @GET("movie/{movie_id}")
    fun getMovie(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String = TMDBApiKey,
        @Query("append_to_response") append: String = "credits"
    ): LiveData<ApiResponse<TMDBMovie>>

    @GET("authentication/guest_session/new")
    fun createGuestSession(
        @Query("api_key") apiKey: String = TMDBApiKey
    ): LiveData<ApiResponse<TMDBGuestSessionResponse>>

    @POST("movie/{movie_id}/rating")
    fun rateMovie(
        @Path("movie_id") movieId: Int,
        @Body ratingBody: TMDBRateMovieBody,
        @Query("guest_session_id") guestSessionId: String,
        @Query("api_key") apiKey: String = TMDBApiKey
    ): LiveData<ApiResponse<TMDBStatusResponse>>
}