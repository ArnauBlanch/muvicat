package xyz.arnau.muvicat.data

import android.arch.lifecycle.LiveData
import xyz.arnau.muvicat.cache.model.ShowingEntity
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
                    response.body?.let { showings ->
                        countDownLatch.await()
                        val hasUpdated = showingCache.updateShowings(showings)
                        if (hasUpdated) {
                            preferencesHelper.showingsUpdated()
                            response.callback?.onDataUpdated()
                        }
                    }
                } else if (response.type == NOT_MODIFIED) {
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
}