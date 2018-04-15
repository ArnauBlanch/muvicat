package xyz.arnau.muvicat.data.remote

import retrofit2.Call
import retrofit2.http.GET
import xyz.arnau.muvicat.model.Movie

interface GencatService {
    @GET("/provacin.xml")
    fun getMovies(): Call<List<Movie>>
}