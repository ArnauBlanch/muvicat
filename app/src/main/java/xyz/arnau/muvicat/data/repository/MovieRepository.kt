package xyz.arnau.muvicat.data.repository

//import xyz.arnau.muvicat.data.GencatNetworkBoundResource
import android.arch.lifecycle.LiveData
import xyz.arnau.muvicat.AppExecutors
import xyz.arnau.muvicat.data.model.Movie
import xyz.arnau.muvicat.data.model.Resource

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
*/
}