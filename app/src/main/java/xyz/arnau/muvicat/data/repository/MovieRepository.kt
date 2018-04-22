package xyz.arnau.muvicat.data.repository

import android.arch.lifecycle.LiveData
import xyz.arnau.muvicat.data.AppExecutors
//import xyz.arnau.muvicat.data.GencatNetworkBoundResource
import xyz.arnau.muvicat.data.Resource
import xyz.arnau.muvicat.data.model.Movie
import xyz.arnau.muvicat.remote.model.GencatMovieResponse

class MovieRepository constructor(
    private val movieCache: MovieCache,
    private val gencatRemote: GencatRemote,
    private val appExecutors: AppExecutors
) {

    fun getMovies(): LiveData<Resource<List<Movie>>>? = null
        /*object : GencatNetworkBoundResource<List<Movie>, GencatMovieResponse>(appExecutors) {
            override fun loadFromDb(): LiveData<List<Movie>> = movieCache.getMovies()

            override fun shouldFetch(): Boolean =
                movieCache.isExpired() || !movieCache.isCached()

            fun saveCallResult(item: GencatMovieResponse) = movieCache.saveMovies(item)

        }.asLiveData()
*/}