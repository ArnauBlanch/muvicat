package xyz.arnau.muvicat.data

import android.arch.lifecycle.LiveData
import xyz.arnau.muvicat.AppExecutors
import xyz.arnau.muvicat.data.model.Movie
import xyz.arnau.muvicat.data.model.Resource
import xyz.arnau.muvicat.data.repository.GencatRemote
import xyz.arnau.muvicat.data.repository.MovieCache
import xyz.arnau.muvicat.data.utils.NetworkBoundResource
import xyz.arnau.muvicat.data.utils.PreferencesHelper
import xyz.arnau.muvicat.remote.model.Response
import xyz.arnau.muvicat.remote.model.ResponseStatus.NOT_MODIFIED
import xyz.arnau.muvicat.remote.model.ResponseStatus.SUCCESSFUL
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepository @Inject constructor(
    private val movieCache: MovieCache,
    private val gencatRemote: GencatRemote,
    private val appExecutors: AppExecutors,
    private val preferencesHelper: PreferencesHelper
) {

    fun getMovies(): LiveData<Resource<List<Movie>>> =
        object : NetworkBoundResource<List<Movie>>(appExecutors) {
            override fun saveResponse(response: Response<List<Movie>>) {
                if (response.type == SUCCESSFUL) {
                    response.body?.let { movieCache.updateMovies(it) }
                }
                response.eTag?.let { preferencesHelper.moviesETag = response.eTag }
                if ((response.type == SUCCESSFUL && response.body != null)
                    || response.type == NOT_MODIFIED
                ) {
                    preferencesHelper.moviesUpdated()
                }
            }

            override fun createCall(): LiveData<Response<List<Movie>>> {
                return gencatRemote.getMovies(preferencesHelper.moviesETag)
            }

            override fun loadFromDb(): LiveData<List<Movie>> {
                return movieCache.getMovies()
            }

            override fun shouldFetch(data: List<Movie>?): Boolean {
                return data == null || data.isEmpty() || movieCache.isExpired()
            }

        }.asLiveData()
}