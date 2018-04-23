package xyz.arnau.muvicat.data.repository

//import xyz.arnau.muvicat.data.GencatNetworkBoundResource
import android.arch.lifecycle.LiveData
import xyz.arnau.muvicat.AppExecutors
import xyz.arnau.muvicat.data.NetworkBoundResource
import xyz.arnau.muvicat.data.PreferencesHelper
import xyz.arnau.muvicat.data.model.Movie
import xyz.arnau.muvicat.data.model.Resource
import xyz.arnau.muvicat.remote.model.Response
import xyz.arnau.muvicat.remote.model.ResponseStatus

class MovieRepository constructor(
        private val movieCache: MovieCache,
        private val gencatRemote: GencatRemote,
        private val appExecutors: AppExecutors,
        private val preferencesHelper: PreferencesHelper
) {

    fun getMovies(): LiveData<Resource<List<Movie>>> =
            object : NetworkBoundResource<List<Movie>>(appExecutors) {
                override fun saveResponse(response: Response<List<Movie>>) {
                    if (response.type == ResponseStatus.SUCCESSFUL) {
                        response.body?.let { movieCache.saveMovies(it) }
                    }
                    response.eTag?.let { preferencesHelper.moviesETag = response.eTag }
                    preferencesHelper.moviesUpdated()
                }

                override fun createCall(): LiveData<Response<List<Movie>>> {
                    return gencatRemote.getMovies(preferencesHelper.moviesETag)
                }

                override fun loadFromDb(): LiveData<List<Movie>> {
                    return movieCache.getMovies()
                }

                override fun shouldFetch(): Boolean {
                    return !movieCache.isCached() || movieCache.isExpired()
                }

            }.asLiveData()
}