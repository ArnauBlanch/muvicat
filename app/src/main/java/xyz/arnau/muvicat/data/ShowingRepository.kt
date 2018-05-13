package xyz.arnau.muvicat.data

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import xyz.arnau.muvicat.cache.model.ShowingEntity
import xyz.arnau.muvicat.data.model.Movie
import xyz.arnau.muvicat.data.model.Resource
import xyz.arnau.muvicat.data.model.Showing
import xyz.arnau.muvicat.data.repository.GencatRemote
import xyz.arnau.muvicat.data.repository.ShowingCache
import xyz.arnau.muvicat.data.utils.NetworkBoundResource
import xyz.arnau.muvicat.data.utils.RepoPreferencesHelper
import xyz.arnau.muvicat.remote.model.Response
import xyz.arnau.muvicat.remote.model.ResponseStatus.NOT_MODIFIED
import xyz.arnau.muvicat.remote.model.ResponseStatus.SUCCESSFUL
import xyz.arnau.muvicat.utils.AppExecutors
import java.util.concurrent.CountDownLatch

class ShowingRepository(
    private val showingCache: ShowingCache,
    private val gencatRemote: GencatRemote,
    private val appExecutors: AppExecutors,
    private val preferencesHelper: RepoPreferencesHelper,
    private val countDownLatch: CountDownLatch
) {
    companion object {
        const val EXPIRATION_TIME: Long = (3 * 60 * 60 * 1000).toLong()
    }

    internal fun hasExpired(): Boolean {
        val currentTime = System.currentTimeMillis()
        val lastUpdateTime = preferencesHelper.showingslastUpdateTime
        return currentTime - lastUpdateTime > EXPIRATION_TIME
    }

    fun getShowings(): LiveData<Resource<List<Showing>>> =
        object : NetworkBoundResource<List<Showing>, List<ShowingEntity>>(appExecutors) {
            override fun saveResponse(response: Response<List<ShowingEntity>>) {
                if (response.type == SUCCESSFUL) {
                    response.body!!.let { showings ->
                        countDownLatch.await()
                        val hasUpdated = showingCache.updateShowings(showings)
                        if (hasUpdated) {
                            preferencesHelper.showingsUpdated()
                            response.callback?.onDataUpdated()
                        }
                    }
                }
                if (response.type == NOT_MODIFIED) {
                    preferencesHelper.showingsUpdated()
                }
            }

            override fun onFetchFailed() {}

            override fun createCall(): LiveData<Response<List<ShowingEntity>>> {
                return gencatRemote.getShowings()
            }

            override fun loadFromDb(): LiveData<List<Showing>> {
                return showingCache.getShowings()
            }

            override fun shouldFetch(data: List<Showing>?): Boolean {
                return data == null || data.isEmpty() || hasExpired()
            }

        }.asLiveData()

    fun getShowingsByCinema(cinemaId: Long): LiveData<Resource<List<Showing>>> {
        return Transformations.switchMap(showingCache.getShowingsByCinema(cinemaId), {
            val liveData = MutableLiveData<Resource<List<Showing>>>()
            if (it == null || it.isEmpty()) {
                liveData.postValue(Resource.error("Showing not found", it))
            } else {
                liveData.postValue(Resource.success(it))
            }
            liveData
        })
    }
}